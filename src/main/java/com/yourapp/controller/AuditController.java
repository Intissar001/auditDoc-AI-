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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.yourapp.services.AuditService;
import com.yourapp.model.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AuditController {

    @FXML private VBox dropzone;
    @FXML private ComboBox<String> projetDropdown;
    @FXML private ComboBox<String> partenaireDropdown;
    @FXML private HBox historiqueMenuItem;
    @FXML private VBox filesContainer;
    @FXML private VBox filesList;
    @FXML private Label filesCountLabel;
    @FXML private VBox auditProgressBox;
    @FXML private ProgressBar auditProgressBar;
    @FXML private Label auditStatusLabel;
    @FXML private VBox auditResultBox;
    @FXML private VBox issuesList;
    @Autowired
     private AuditService auditService;

    private VBox notificationBox;
    private List<File> selectedFiles = new ArrayList<>();

    @FXML
    public void initialize() {
        projetDropdown.getItems().addAll("Projet A", "Projet B", "Projet C");
        partenaireDropdown.getItems().addAll("Partenaire X", "Partenaire Y", "Partenaire Z");
        createNotificationBox();
        System.out.println("✅ AuditController chargé par Spring");
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
            selectedFiles.remove(file);
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

        List<File> files = fileChooser.showOpenMultipleDialog(
                dropzone.getScene().getWindow()
        );

        if (files == null || files.isEmpty()) {
            return;
        }

        filesList.getChildren().clear();
        selectedFiles.clear();

        for (File file : files) {
            selectedFiles.add(file);
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
        Circle circle = new Circle(12);
        circle.setFill(Color.web("#1E88E5"));

        Label icon = new Label("i");
        icon.setTextFill(Color.WHITE);
        icon.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        StackPane iconPane = new StackPane(circle, icon);
        iconPane.setPrefSize(24, 24);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1E88E5;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #667085;");

        VBox textBox = new VBox(3, titleLabel, messageLabel);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #667085;
            -fx-font-size: 16px;
            -fx-cursor: hand;
        """);

        HBox contentBox = new HBox(15, iconPane, textBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

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

        notificationBox.getChildren().add(notification);

        closeBtn.setOnAction(e -> {
            notificationBox.getChildren().remove(notification);
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            notificationBox.getChildren().remove(notification);
        }));
        timeline.play();
    }

    @FXML
    private void handleStartAudit() {
        Audit audit = new Audit();

        audit.setAuditorId(1L);
        audit.setProjectId(1L);// ✅ OBLIGATOIRE
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("STARTED");
        audit = auditService.createAudit(audit); // ✅ TRÈS IMPORTANT
        System.out.println("Audit créé avec ID = " + audit.getId());

        callAuditApi();
        int fileCount = filesList.getChildren().size();

        showNotification(
                "Démarrage de l'analyse...",
                "Analyse de " + fileCount + " document(s) en cours"
        );

        Dialog<Void> progressDialog = new Dialog<>();
        progressDialog.setTitle("Progression de l'analyse");
        progressDialog.setHeaderText(null);

        ButtonType closeButtonType = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        progressDialog.getDialogPane().getButtonTypes().add(closeButtonType);

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
        HBox.setHgrow(progressHeader.getChildren().get(1), Priority.ALWAYS);

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

        progressDialog.getDialogPane().lookupButton(closeButtonType).setVisible(false);

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


        statusLabel.textProperty().bind(auditTask.messageProperty());
        progressBar.progressProperty().bind(auditTask.progressProperty());

        auditTask.progressProperty().addListener((obs, oldVal, newVal) -> {
            int percent = (int) (newVal.doubleValue() * 100);
            percentLabel.setText(percent + "%");
        });

        auditTask.setOnSucceeded(e -> {
            progressDialog.close();
            showAuditResultsDialog(fileCount);
            showSuccessNotification();
        });

        auditTask.setOnFailed(e -> {
            progressDialog.close();
            showErrorNotification();
        });

        new Thread(auditTask).start();
        progressDialog.show();
    }
    private void callAuditApi() {
        try {
            String boundary = "----JavaFXBoundary";
            java.net.URL url = new java.net.URL("http://localhost:8080/api/audits/start");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            var output = conn.getOutputStream();
            var writer = new java.io.PrintWriter(
                    new java.io.OutputStreamWriter(output, java.nio.charset.StandardCharsets.UTF_8),
                    true
            );

            // JSON data
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"data\"\r\n");
            writer.append("Content-Type: application/json\r\n\r\n");
            writer.append("""
                {
                  "projectId": 1,
                  "partnerId": 1
                }
                """).append("\r\n");

            // Files
            for (File file : selectedFiles) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"files\"; filename=\"")
                        .append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: application/octet-stream\r\n\r\n");
                writer.flush();

                java.nio.file.Files.copy(file.toPath(), output);
                output.flush();

                writer.append("\r\n").flush();
            }

            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.close();

            int responseCode = conn.getResponseCode();
            System.out.println("API RESPONSE CODE: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAuditResultsDialog(int fileCount) {
        Dialog<Void> resultsDialog = new Dialog<>();
        resultsDialog.setTitle("Analyse terminée");
        resultsDialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        resultsDialog.getDialogPane().getButtonTypes().add(closeButton);

        // Style du bouton X
        javafx.scene.Node closeButtonNode = resultsDialog.getDialogPane().lookupButton(closeButton);
        closeButtonNode.setStyle("""
            -fx-background-color: transparent;
            -fx-font-size: 20px;
            -fx-text-fill: #667085;
            -fx-cursor: hand;
        """);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setPrefWidth(700);
        mainContent.setPrefHeight(600);

        // Header
        Label titleLabel = new Label("Analyse terminée");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        // Rapport de conformité section
        VBox reportBox = new VBox(8);
        reportBox.setStyle("""
            -fx-background-color: #f3f4f6;
            -fx-padding: 15;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        Label reportTitle = new Label("Rapport de Conformité");
        reportTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        Label reportSubtitle = new Label("Analyse terminée pour " + fileCount + " document(s)");
        reportSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        reportBox.getChildren().addAll(reportTitle, reportSubtitle);

        // ScrollPane pour les documents et recommandations
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPrefHeight(350);
        scrollPane.setMaxHeight(350);
        scrollPane.setStyle("""
            -fx-background-color: white; 
            -fx-background: white;
            -fx-border-color: transparent;
        """);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox scrollContent = new VBox(15);
        scrollContent.setPadding(new Insets(5, 10, 5, 0));

        // Documents container
        VBox documentsContainer = new VBox(12);
        for (File file : selectedFiles) {
            documentsContainer.getChildren().add(createDocumentResultCard(file.getName()));
        }

        // Recommandations section
        VBox recommendationsSection = createRecommendationsSection();

        scrollContent.getChildren().addAll(documentsContainer, recommendationsSection);
        scrollPane.setContent(scrollContent);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Boutons d'action en bas
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(5, 0, 0, 0));

        Button downloadBtn = new Button("⬇ Télécharger le rapport");
        downloadBtn.setStyle("""
            -fx-background-color: #10b981;
            -fx-text-fill: white;
            -fx-font-weight: 600;
            -fx-font-size: 13px;
            -fx-padding: 10 20;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-pref-width: 220;
        """);
        downloadBtn.setOnAction(e -> handleDownloadReport());

        Button newAuditBtn = new Button("Lancer un Nouvel Audit");
        newAuditBtn.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #374151;
            -fx-font-weight: 600;
            -fx-font-size: 13px;
            -fx-padding: 10 20;
            -fx-border-color: #d1d5db;
            -fx-border-width: 1;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-pref-width: 220;
        """);
        newAuditBtn.setOnAction(e -> {
            resultsDialog.close();
            resetAuditForm();
        });

        actionButtons.getChildren().addAll(downloadBtn, newAuditBtn);

        mainContent.getChildren().addAll(titleLabel, reportBox, scrollPane, actionButtons);
        resultsDialog.getDialogPane().setContent(mainContent);

        // Taille fixe pour meilleure compatibilité
        resultsDialog.setResizable(false);
        resultsDialog.getDialogPane().setPrefWidth(700);
        resultsDialog.getDialogPane().setPrefHeight(600);

        resultsDialog.show();
    }

    private VBox createRecommendationsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 0, 0));

        // Header avec icône
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle checkCircle = new Circle(10);
        checkCircle.setFill(Color.TRANSPARENT);
        checkCircle.setStroke(Color.web("#10b981"));
        checkCircle.setStrokeWidth(2);

        Label checkIcon = new Label("✓");
        checkIcon.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");

        StackPane checkStack = new StackPane(checkCircle, checkIcon);
        checkStack.setPrefSize(20, 20);

        Label headerTitle = new Label("Recommandations Suggérées:");
        headerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        header.getChildren().addAll(checkStack, headerTitle);

        // Liste des recommandations
        VBox recommendationsList = new VBox(12);

        recommendationsList.getChildren().addAll(
                createRecommendationItem(
                        "Re-scanner le document en haute qualité",
                        "Page 7, Paragraphe 2"
                ),
                createRecommendationItem(
                        "Compléter toutes les sections obligatoires",
                        "Page 2, Paragraphe 1"
                ),
                createRecommendationItem(
                        "Vérifier les montants et totaux",
                        "Page 7, Paragraphe 2"
                )
        );

        section.getChildren().addAll(header, recommendationsList);
        return section;
    }

    private VBox createRecommendationItem(String title, String location) {
        VBox item = new VBox(8);
        item.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-padding: 15;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
        """);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");
        titleLabel.setWrapText(true);

        Label locationLabel = new Label(location);
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        item.getChildren().addAll(titleLabel, locationLabel);
        return item;
    }

    private void handleDownloadReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport");
        fileChooser.setInitialFileName("Rapport_Audit_" + System.currentTimeMillis() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(dropzone.getScene().getWindow());

        if (file != null) {
            showNotification(
                    "Rapport téléchargé ✅",
                    "Le rapport a été enregistré avec succès"
            );
        }
    }

    private void resetAuditForm() {
        filesList.getChildren().clear();
        selectedFiles.clear();
        filesContainer.setVisible(false);
        filesContainer.setManaged(false);
        projetDropdown.setValue(null);
        partenaireDropdown.setValue(null);
        showNotification(
                "Nouveau audit",
                "Prêt pour un nouvel audit"
        );
    }

    private VBox createDocumentResultCard(String fileName) {
        VBox card = new VBox(15);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 20;
        """);

        // Header avec icône d'erreur et badge
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        // Icône d'erreur (cercle rouge avec X)
        Circle errorCircle = new Circle(12);
        errorCircle.setFill(Color.TRANSPARENT);
        errorCircle.setStroke(Color.web("#dc2626"));
        errorCircle.setStrokeWidth(2);

        Label xIcon = new Label("✕");
        xIcon.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 14px; -fx-font-weight: bold;");

        StackPane errorIcon = new StackPane(errorCircle, xIcon);
        errorIcon.setPrefSize(24, 24);

        // Nom du fichier
        Label docName = new Label(fileName);
        docName.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Badge Critique
        Label badge = new Label("Critique");
        badge.setStyle("""
            -fx-background-color: #fee2e2;
            -fx-text-fill: #dc2626;
            -fx-padding: 6 14;
            -fx-border-radius: 16;
            -fx-background-radius: 16;
            -fx-font-weight: 600;
            -fx-font-size: 12px;
        """);

        header.getChildren().addAll(errorIcon, docName, spacer, badge);

        // État de conformité
        Label statusLabel = new Label("État de Conformité");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9ca3af; -fx-font-weight: 500;");

        // Section Problèmes Identifiés
        VBox problemsSection = new VBox(10);

        HBox problemsHeader = new HBox(8);
        problemsHeader.setAlignment(Pos.CENTER_LEFT);

        Circle warningCircle = new Circle(8);
        warningCircle.setFill(Color.TRANSPARENT);
        warningCircle.setStroke(Color.web("#dc2626"));
        warningCircle.setStrokeWidth(2);

        Label warningIcon = new Label("!");
        warningIcon.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");

        StackPane warningStack = new StackPane(warningCircle, warningIcon);
        warningStack.setPrefSize(16, 16);

        Label problemsTitle = new Label("Problèmes Identifiés:");
        problemsTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626; -fx-font-weight: 600;");

        problemsHeader.getChildren().addAll(warningStack, problemsTitle);

        // Problème détaillé
        VBox problemBox = new VBox(8);
        problemBox.setStyle("""
            -fx-background-color: #fef2f2;
            -fx-padding: 15;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        Label problemTitle = new Label("Document incomplet - sections manquantes");
        problemTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626; -fx-font-weight: 600;");
        problemTitle.setWrapText(true);

        Label problemLocation = new Label("Page 2, Paragraphe 1");
        problemLocation.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        problemBox.getChildren().addAll(problemTitle, problemLocation);

        problemsSection.getChildren().addAll(problemsHeader, problemBox);

        card.getChildren().addAll(header, statusLabel, problemsSection);

        return card;
    }

    private void showSuccessNotification() {
        showNotification(
                "Audit terminé avec succès ✅",
                "Les résultats sont disponibles"
        );
    }

    private void showErrorNotification() {
        showNotification(
                "Erreur lors de l'audit ❌",
                "Une erreur s'est produite. Veuillez réessayer."
        );
    }

}