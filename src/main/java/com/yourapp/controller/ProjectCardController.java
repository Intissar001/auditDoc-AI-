package com.yourapp.controller;

import com.yourapp.model.AuditDocument;
import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import com.yourapp.utils.SpringContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

        // --- CONTENEUR RACINE ---
        VBox root = new VBox(20);
        // Fond très légèrement teinté pour faire ressortir les cartes blanches
        root.setStyle("-fx-background-color: #fbfcfd; -fx-padding: 25;");
        root.setPrefWidth(540);

        // --- TITRE ---
        Label titleLabel = new Label(this.project.getName());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        // --- BARRE DE RECHERCHE AMICALE ---
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un document...");
        searchField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 25; " +
                        "-fx-padding: 10 18; " +
                        "-fx-border-color: #e2e8f0; " +
                        "-fx-border-radius: 25; " +
                        "-fx-font-size: 14px;"
        );
        searchField.setFocusTraversable(false);

        // Conteneur pour la liste des documents
        VBox documentListContainer = new VBox(12); // Espacement entre les cartes
        documentListContainer.setStyle("-fx-background-color: transparent;");

        // --- LOGIQUE DE RAFRAÎCHISSEMENT ---
        Runnable refreshList = () -> {
            documentListContainer.getChildren().clear();
            String filter = searchField.getText().toLowerCase();
            for (AuditDocument doc : docs) {
                if (doc.getDocumentName().toLowerCase().contains(filter)) {
                    documentListContainer.getChildren().add(createDocumentRow(doc, docs, documentListContainer));
                }
            }
        };

        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshList.run());
        refreshList.run();

        // --- SCROLLPANE ÉPURÉ ---
        ScrollPane scrollPane = new ScrollPane(documentListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(380);
        // Supprime les bordures et fonds gris par défaut du ScrollPane
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        // --- BOUTON FERMER MODERNE ---
        Button btnClose = new Button("Fermer");

        String fixedStructure =
                "-fx-padding: 10 25; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-radius: 12; " +
                        "-fx-border-width: 1; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-cursor: hand; ";

        String btnCloseBase = fixedStructure +
                "-fx-background-color: #f1f5f9; " +
                "-fx-text-fill: #475569; " +
                "-fx-border-color: #e2e8f0;";

        String btnCloseHover = fixedStructure +
                "-fx-background-color: #e2e8f0; " +
                "-fx-text-fill: #1e293b; " +
                "-fx-border-color: #cbd5e1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);";

        btnClose.setStyle(btnCloseBase);
        btnClose.setOnMouseEntered(e -> btnClose.setStyle(btnCloseHover));
        btnClose.setOnMouseExited(e -> btnClose.setStyle(btnCloseBase));
        btnClose.setOnAction(e -> stage.close());

        HBox footer = new HBox(btnClose);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        // --- ASSEMBLAGE ---
        root.getChildren().addAll(titleLabel, searchField, scrollPane, footer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        // Focus sur le bouton fermer au démarrage pour éviter d'activer la barre de recherche
        Platform.runLater(btnClose::requestFocus);
    }

    private HBox createDocumentRow(AuditDocument doc, List<AuditDocument> currentList, VBox container) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);

        // Style de la carte (Bulle blanche avec ombre douce)
        String baseStyle =
                "-fx-background-color: #ffffff; " +
                        "-fx-padding: 14 22; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 10, 0, 0, 4); " +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #f0f9ff; " +
                        "-fx-padding: 14 22; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 6); " +
                        "-fx-cursor: hand;";

        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        // Badge Icône
        StackPane iconBadge = new StackPane();
        iconBadge.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 14; -fx-padding: 10;");
        Label fileIcon = new Label("📑");
        fileIcon.setStyle("-fx-font-size: 18px;");
        iconBadge.getChildren().add(fileIcon);

        // Nom du fichier
        Label fileName = new Label(doc.getDocumentName());
        fileName.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155; -fx-font-weight: 600;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton Oeil
        Button btnView = new Button("👁");
        String btnBase =
                "-fx-background-color: #f8fafc; " +
                        "-fx-text-fill: #94a3b8; " +
                        "-fx-min-width: 40; " +
                        "-fx-min-height: 40; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand;";

        String btnHover =
                "-fx-background-color: #ffffff; " +
                        "-fx-text-fill: #0ea5e9; " +
                        "-fx-min-width: 40; " +
                        "-fx-min-height: 40; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.15), 8, 0, 0, 0); " +
                        "-fx-cursor: hand;";

        btnView.setStyle(btnBase);
        btnView.setOnMouseEntered(e -> btnView.setStyle(btnHover));
        btnView.setOnMouseExited(e -> btnView.setStyle(btnBase));
        btnView.setOnAction(e -> ouvrirFichier(doc.getDocumentPath()));

        card.getChildren().addAll(iconBadge, fileName, spacer, btnView);
        return card;
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