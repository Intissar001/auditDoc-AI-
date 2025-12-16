package com.yourapp.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.util.List;


/**
 * Controller for the AuditView.fxml
 */
public class AuditController {

    @FXML
    private VBox dropzone;

    @FXML
    private ComboBox<String> projetDropdown;

    @FXML
    private ComboBox<String> partenaireDropdown;

    @FXML
    private HBox historiqueMenuItem;

    @FXML
    private VBox filesContainer;

    @FXML
    private VBox filesList;

    @FXML
    private Label filesCountLabel;

    @FXML
    private VBox auditProgressBox;

    @FXML
    private ProgressBar auditProgressBar;

    @FXML
    private Label auditStatusLabel;
    @FXML
    private VBox auditResultBox;

    @FXML
    private VBox issuesList;


    private VBox notificationBox;


    @FXML
    public void initialize() {
        // Populate dropdowns
        projetDropdown.getItems().addAll("Projet A", "Projet B", "Projet C");
        partenaireDropdown.getItems().addAll("Partenaire X", "Partenaire Y", "Partenaire Z");

        // Setup click handler for Historique menu
        if (historiqueMenuItem != null) {
            historiqueMenuItem.setOnMouseClicked(event -> openHistoryPage());
        } else {
            System.err.println("WARNING: historiqueMenuItem is NULL! Check fx:id in Audit.fxml");
        }

        // Créer la notification box (invisible au départ)
        createNotificationBox();
    }

    @FXML
    private void handleNewAudit() {
        System.out.println("Lancer un Nouvel Audit button clicked.");
    }

    private HBox createFileItem(File file) {

        Label fileName = new Label(file.getName());
        fileName.setStyle("-fx-font-weight: 600;");

        Label fileSize = new Label(String.format("%.2f KB", file.length() / 1024.0));
        fileSize.setStyle("-fx-text-fill: #667085; -fx-font-size: 12px;");

        VBox fileInfo = new VBox(fileName, fileSize);
        fileInfo.setSpacing(3);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #667085;
        -fx-font-size: 14px;
        -fx-cursor: hand;
    """);

        HBox fileItem = new HBox(fileInfo, removeBtn);
        fileItem.setAlignment(Pos.CENTER_LEFT);
        fileItem.setSpacing(15);
        fileItem.setStyle("""
        -fx-padding: 12;
        -fx-border-color: #e4e8ee;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-background-color: #ffffff;
    """);

        removeBtn.setOnAction(e -> {
            filesList.getChildren().remove(fileItem);
            updateFileCount();

            if (filesList.getChildren().isEmpty()) {
                filesContainer.setVisible(false);
                filesContainer.setManaged(false);
            }
        });

        return fileItem;
    }

    @FXML
    private void handleBrowseFiles() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner des documents");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents",
                        "*.pdf", "*.docx", "*.doc", "*.xlsx", "*.xls")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(
                dropzone.getScene().getWindow()
        );

        if (selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }

        filesList.getChildren().clear();

        for (File file : selectedFiles) {
            filesList.getChildren().add(createFileItem(file));
        }

        filesContainer.setVisible(true);
        filesContainer.setManaged(true);

        updateFileCount();
    }

    private void updateFileCount() {
        int count = filesList.getChildren().size();
        filesCountLabel.setText("Fichiers Importés (" + count + ")");
    }

    private void createNotificationBox() {
        notificationBox = new VBox(10);
        notificationBox.setAlignment(Pos.TOP_RIGHT);
        notificationBox.setStyle("""
            -fx-padding: 20;
        """);
        notificationBox.setPickOnBounds(false);
    }

    private void showNotification(String title, String message) {
        // Icône info (cercle bleu avec "i")
        Circle circle = new Circle(12);
        circle.setFill(Color.web("#1E88E5"));

        Label icon = new Label("i");
        icon.setTextFill(Color.WHITE);
        icon.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.layout.StackPane iconPane = new javafx.scene.layout.StackPane(circle, icon);
        iconPane.setPrefSize(24, 24);

        // Titre et message
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1E88E5;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #667085;");

        VBox textBox = new VBox(3, titleLabel, messageLabel);

        // Bouton fermer
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #667085;
            -fx-font-size: 16px;
            -fx-cursor: hand;
        """);

        HBox contentBox = new HBox(15, iconPane, textBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        HBox notification = new HBox(contentBox, closeBtn);
        notification.setAlignment(Pos.CENTER_LEFT);
        notification.setPadding(new Insets(15));
        notification.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);
            -fx-min-width: 320px;
        """);

        // Ajouter au parent
        VBox rootContent = (VBox) dropzone.getParent();
        VBox scrollContent = (VBox) rootContent.getParent();

        if (notificationBox.getParent() == null) {
            javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
            overlay.setPickOnBounds(false);
            overlay.getChildren().add(notificationBox);
            javafx.scene.layout.StackPane.setAlignment(notificationBox, Pos.TOP_RIGHT);
            javafx.scene.layout.StackPane.setMargin(notificationBox, new Insets(20, 20, 0, 0));

            if (scrollContent.getParent() instanceof javafx.scene.control.ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) scrollContent.getParent();
                javafx.scene.layout.StackPane container = new javafx.scene.layout.StackPane(scrollPane, overlay);
                scrollPane.getParent().getChildrenUnmodifiable().forEach(node -> {
                    if (node == scrollPane) {
                        ((javafx.scene.layout.Pane) scrollPane.getParent()).getChildren().set(
                                ((javafx.scene.layout.Pane) scrollPane.getParent()).getChildren().indexOf(scrollPane),
                                container
                        );
                    }
                });
            }
        }

        notificationBox.getChildren().add(notification);

        // Animation de fermeture
        closeBtn.setOnAction(e -> {
            notificationBox.getChildren().remove(notification);
        });

        // Auto-fermeture après 5 secondes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            notificationBox.getChildren().remove(notification);
        }));
        timeline.play();
    }

    @FXML
    private void handleStartAudit() {
        int fileCount = filesList.getChildren().size();

        // Afficher la notification
        showNotification(
                "Démarrage de l'analyse...",
                "Analyse de " + fileCount + " document(s) en cours"
        );

        // Créer le dialog de progression
        Dialog<Void> progressDialog = new Dialog<>();
        progressDialog.setTitle("Progression de l'analyse");
        progressDialog.setHeaderText(null);

        // Bouton fermer (X)
        ButtonType closeButtonType = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        progressDialog.getDialogPane().getButtonTypes().add(closeButtonType);

        // Contenu du dialog
        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(30, 40, 30, 40));
        dialogContent.setAlignment(Pos.CENTER_LEFT);
        dialogContent.setPrefWidth(500);

        Label titleLabel = new Label("Progression de l'analyse");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 600;");

        Label statusLabel = new Label("Analyse en cours...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #667085;");

        Label percentLabel = new Label("0%");
        percentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");

        HBox progressHeader = new HBox(statusLabel, new Region(), percentLabel);
        progressHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressHeader.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(10);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("""
            -fx-accent: #1E88E5;
        """);

        Label descLabel = new Label("L'IA analyse vos documents pour détecter les problèmes\nde conformité...");
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #667085; -fx-wrap-text: true;");

        dialogContent.getChildren().addAll(titleLabel, progressHeader, progressBar, descLabel);
        progressDialog.getDialogPane().setContent(dialogContent);

        // Supprimer les boutons par défaut du dialog
        progressDialog.getDialogPane().lookupButton(closeButtonType).setVisible(false);

        // Tâche d'audit en arrière-plan
        Task<Void> auditTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String[] steps = {
                        "Analyse des documents...",
                        "Extraction du contenu...",
                        "Vérification de conformité...",
                        "Génération du rapport..."
                };

                for (int i = 0; i < steps.length; i++) {
                    updateMessage(steps[i]);
                    updateProgress(i + 1, steps.length);
                    Thread.sleep(1500);
                }

                return null;
            }
        };

        // Bind properties
        statusLabel.textProperty().bind(auditTask.messageProperty());
        progressBar.progressProperty().bind(auditTask.progressProperty());

        auditTask.progressProperty().addListener((obs, oldVal, newVal) -> {
            int percent = (int) (newVal.doubleValue() * 100);
            percentLabel.setText(percent + "%");
        });

        auditTask.setOnSucceeded(e -> {
            progressDialog.close();
            showSuccessNotification();
            auditResultBox.setVisible(true);
            auditResultBox.setManaged(true);

            issuesList.getChildren().clear();

            issuesList.getChildren().addAll(
                    createIssueItem(
                            "Document incomplet",
                            "Le document ne contient pas toutes les sections obligatoires.",
                            "Ajouter les sections manquantes selon la norme."
                    ),
                    createIssueItem(
                            "Non-respect de la norme ISO",
                            "Certaines clauses ISO 9001 ne sont pas respectées.",
                            "Mettre à jour les procédures internes."
                    )
            );

        });

        auditTask.setOnFailed(e -> {
            progressDialog.close();
            showErrorNotification();
        });

        // Démarrer la tâche
        new Thread(auditTask).start();
        progressDialog.show();
    }

    private void showSuccessNotification() {
        showNotification(
                "Audit terminé avec succès ✅",
                "Les résultats sont disponibles dans l'historique"
        );
    }

    private void showErrorNotification() {
        showNotification(
                "Erreur lors de l'audit ❌",
                "Une erreur s'est produite. Veuillez réessayer."
        );
    }
    private HBox createIssueItem(String type, String description, String suggestion) {

        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #991b1b;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);

        Label suggLabel = new Label("Suggestion : " + suggestion);
        suggLabel.setStyle("-fx-text-fill: #065f46;");

        VBox content = new VBox(typeLabel, descLabel, suggLabel);
        content.setSpacing(5);

        Label statusBadge = new Label("NON CONFORME");
        statusBadge.getStyleClass().add("badge-non-conforme");

        HBox issueBox = new HBox(content, statusBadge);
        issueBox.setSpacing(20);
        issueBox.setAlignment(Pos.CENTER_LEFT);
        issueBox.setStyle("""
        -fx-padding: 15;
        -fx-border-color: #e4e8ee;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-background-color: #ffffff;
    """);

        return issueBox;
    }

    @FXML
    private void openHistoryPage() {
        try {
            System.out.println("=== STARTING NAVIGATION ===");
            System.out.println("Button clicked!");

            java.net.URL fxmlUrl = getClass().getResource("/views/fxml/history.fxml");
            System.out.println("FXML URL: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERROR: history.fxml NOT FOUND!");
                System.err.println("Make sure history.fxml is in: src/main/resources/views/fxml/");
                return;
            }

            System.out.println("Loading FXML...");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("FXML loaded successfully!");

            Stage stage = (Stage) historiqueMenuItem.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("=== NAVIGATION COMPLETED ===");

        } catch (Exception e) {
            System.err.println("=== NAVIGATION ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}