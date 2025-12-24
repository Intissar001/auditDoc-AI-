package com.yourapp.controller;

import com.yourapp.model.AuditDocument;
import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import com.yourapp.utils.SpringContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ProjectCardController {

    @FXML private Label lblName, lblPartner, lblDesc, lblStatus, lblProgress, lblStartDate, lblEndDate, lblAudit;
    @FXML private Button btnEdit, btnFolder;

    private Project project;

    @Autowired
    private ProjectService projectService;

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setProjectData(Project p) {
        this.project = p;
        if (lblName != null) lblName.setText(p.getName());
        if (lblPartner != null) lblPartner.setText(p.getPartner());
        if (lblDesc != null) lblDesc.setText(p.getDescription());
        if (lblStatus != null) lblStatus.setText(p.getStatus());
        if (lblProgress != null) lblProgress.setText(p.getProgress() + "%");
        if (lblStartDate != null) lblStartDate.setText(p.getStartDate() != null ? p.getStartDate().toString() : "--/--/----");
        if (lblEndDate != null) lblEndDate.setText(p.getEndDate() != null ? p.getEndDate().toString() : "--/--/----");
        if (lblAudit != null) lblAudit.setText(p.getProchainAuditDate() != null ? p.getProchainAuditDate().toString() : "Non planifié");
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/ProjectForm.fxml"));
            loader.setControllerFactory(clazz -> SpringContext.getContext().getBean(clazz));
            Parent root = loader.load();
            ProjectFormController formController = loader.getController();
            formController.setProjectData(this.project);

            Stage stage = new Stage();
            stage.setTitle("Modifier le projet : " + this.project.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }

    @FXML
    private void handleOpenFolder() {
        if (this.project == null) return;

        List<AuditDocument> docs = projectService.getDocumentsByProjectId(this.project.getId());

        Stage stage = new Stage();
        stage.setTitle("Documents - " + this.project.getName());

        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");
        root.setPrefWidth(520);

        Label titleLabel = new Label(this.project.getName());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        TextField searchField = new TextField();
        searchField.setPromptText(" 🔎 Rechercher un document...");
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 8 15; -fx-border-color: #e5e7eb; -fx-border-radius: 20;");
        searchField.setFocusTraversable(false); // Évite que la barre soit bleue au démarrage

        VBox documentListContainer = new VBox(10);

        // Méthode de rafraîchissement local
        Runnable refreshList = () -> {
            documentListContainer.getChildren().clear();
            String filter = searchField.getText().toLowerCase();
            for (AuditDocument doc : docs) {
                if (doc.getDocumentName().toLowerCase().contains(filter)) {
                    // On passe bien 3 arguments ici : le doc, la liste, et le container visuel
                    documentListContainer.getChildren().add(createDocumentRow(doc, docs, documentListContainer));
                }
            }
        };

        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshList.run());
        refreshList.run();

        ScrollPane scrollPane = new ScrollPane(documentListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(320);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Button btnClose = new Button("Fermer");
        btnClose.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #4b5563; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5; -fx-font-weight: bold;");
        btnClose.setOnAction(e -> stage.close());

        HBox footer = new HBox(btnClose);
        footer.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titleLabel, searchField, new Separator(), scrollPane, footer);

        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        Platform.runLater(btnClose::requestFocus);
    }

    // Corrigé : 3 arguments seulement pour correspondre à l'appel
    private HBox createDocumentRow(AuditDocument doc, List<AuditDocument> currentList, VBox container) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

        Label fileName = new Label(doc.getDocumentName());
        fileName.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnView = new Button("👁");
        btnView.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-cursor: hand; -fx-background-radius: 6; -fx-min-width: 35;");
        btnView.setOnAction(e -> ouvrirFichier(doc.getDocumentPath()));

        Button btnDel = new Button("🗑");
        btnDel.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-background-radius: 6; -fx-min-width: 35;");

        btnDel.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le document ?", ButtonType.OK, ButtonType.CANCEL);
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    // 1. Suppression en base de données d'abord
                    // Si cette ligne échoue, on saute directement au catch et on ne supprime rien d'autre
                    projectService.deleteDocument(doc);

                    // 2. Suppression physique (Seulement si la base a accepté)
                    String cleanPath = doc.getDocumentPath().replace("/", java.io.File.separator).replace("\\", java.io.File.separator);
                    java.io.File file = new java.io.File(System.getProperty("user.dir") + java.io.File.separator + cleanPath);
                    if (file.exists()) {
                        file.delete();
                    }

                    // 3. Mise à jour de l'interface
                    currentList.remove(doc);
                    container.getChildren().remove(row);

                    System.out.println("✅ Suppression réussie");

                } catch (Exception ex) {
                    System.err.println("❌ Erreur critique : " + ex.getMessage());
                    showAlert("Erreur de suppression", "La base de données a refusé la suppression.\nErreur : " + ex.getMessage());
                }
            }
        });

        row.getChildren().addAll(fileName, spacer, btnView, btnDel);
        return row;
    }

    private void ouvrirFichier(String path) {
        try {
            String cleanPath = path.replace("/", java.io.File.separator).replace("\\", java.io.File.separator);
            java.io.File file = new java.io.File(System.getProperty("user.dir") + java.io.File.separator + cleanPath);

            if (file.exists()) {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                } else {
                    new ProcessBuilder("explorer.exe", file.getAbsolutePath()).start();
                }
            } else {
                showAlert("Fichier Introuvable", "Le fichier n'existe pas physiquement sur le disque.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le fichier.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}