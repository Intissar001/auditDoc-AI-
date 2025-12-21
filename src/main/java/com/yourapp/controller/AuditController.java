package com.yourapp.controller;

import com.yourapp.dto.AuditCreateRequestDto;
import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.dto.AuditResponseDto;
import com.yourapp.model.AuditTemplate;
import com.yourapp.model.Project;
import com.yourapp.services_UI.AuditApiService;
import com.yourapp.services_UI.FileUploadService;
import com.yourapp.services_UI.ModelApiService;
import com.yourapp.services_UI.ProjectApiService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur JavaFX pour la page d'audit
 * Communique uniquement avec le backend via les services API
 */
@Component
@Slf4j
public class AuditController {

    // ============ FXML Components ============
    @FXML private VBox dropzone;
    @FXML private ComboBox<Project> projetDropdown;
    @FXML private ComboBox<AuditTemplate> partenaireDropdown;
    @FXML private VBox filesContainer;
    @FXML private VBox filesList;
    @FXML private Label filesCountLabel;
    @FXML private VBox auditProgressBox;
    @FXML private ProgressBar auditProgressBar;
    @FXML private Label auditStatusLabel;
    @FXML private VBox auditResultBox;
    @FXML private VBox issuesList;

    // ============ Services Spring ============
    @Autowired private ProjectApiService projectApiService;
    @Autowired private ModelApiService modelApiService;
    @Autowired private AuditApiService auditApiService;
    @Autowired private FileUploadService fileUploadService;

    // ============ Variables d'état ============
    private VBox notificationBox;
    private List<File> selectedFiles = new ArrayList<>();
    private Long currentAuditId;
    private Project selectedProject;
    private AuditTemplate selectedModel;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        log.info("🚀 Initialisation du AuditController");

        createNotificationBox();
        setupComboBoxes();
        loadProjects();

        log.info("✅ AuditController initialisé avec succès");
    }

    /**
     * Configurer les ComboBox avec des convertisseurs personnalisés
     */
    private void setupComboBoxes() {
        // Configurer le projet dropdown
        projetDropdown.setConverter(new javafx.util.StringConverter<Project>() {
            @Override
            public String toString(Project project) {
                return project != null ? project.getName() : "";
            }

            @Override
            public Project fromString(String string) {
                return null;
            }
        });

        // Listener pour charger les modèles quand un projet est sélectionné
        projetDropdown.setOnAction(e -> {
            selectedProject = projetDropdown.getValue();
            if (selectedProject != null) {
                log.info("📌 Projet sélectionné: {}", selectedProject.getName());
                loadModelsForProject(selectedProject.getId());
            }
        });

        // Configurer le modèle dropdown
        partenaireDropdown.setConverter(new javafx.util.StringConverter<AuditTemplate>() {
            @Override
            public String toString(AuditTemplate template) {
                return template != null ? template.getName() : "";
            }

            @Override
            public AuditTemplate fromString(String string) {
                return null;
            }
        });

        partenaireDropdown.setOnAction(e -> {
            selectedModel = partenaireDropdown.getValue();
            if (selectedModel != null) {
                log.info("📌 Modèle sélectionné: {}", selectedModel.getName());
            }
        });
    }

    /**
     * Charger la liste des projets depuis l'API
     */
    private void loadProjects() {
        log.info("📥 Chargement des projets...");

        Task<List<Project>> task = new Task<>() {
            @Override
            protected List<Project> call() {
                try {
                    return projectApiService.getAllProjects();
                } catch (Exception e) {
                    log.error("❌ Erreur lors du chargement des projets", e);
                    return new ArrayList<>();
                }
            }
        };

        task.setOnSucceeded(e -> {
            List<Project> projects = task.getValue();
            Platform.runLater(() -> {
                projetDropdown.getItems().clear();
                projetDropdown.getItems().addAll(projects);

                if (projects.isEmpty()) {
                    showNotification("⚠️ Aucun projet", "Aucun projet disponible");
                } else {
                    log.info("✅ {} projets chargés", projects.size());
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showNotification("❌ Erreur", "Impossible de charger les projets");
            });
        });

        new Thread(task).start();
    }

    /**
     * Charger les modèles pour un projet spécifique
     */
    private void loadModelsForProject(Long projectId) {
        log.info("📥 Chargement des modèles pour le projet ID: {}", projectId);

        Task<List<AuditTemplate>> task = new Task<>() {
            @Override
            protected List<AuditTemplate> call() {
                try {
                    return modelApiService.getModelsByProject(projectId);
                } catch (Exception e) {
                    log.error("❌ Erreur lors du chargement des modèles", e);
                    return new ArrayList<>();
                }
            }
        };

        task.setOnSucceeded(e -> {
            List<AuditTemplate> models = task.getValue();
            Platform.runLater(() -> {
                partenaireDropdown.getItems().clear();
                partenaireDropdown.getItems().addAll(models);

                if (models.isEmpty()) {
                    showNotification("⚠️ Aucun modèle", "Aucun modèle disponible pour ce projet");
                } else {
                    log.info("✅ {} modèles chargés", models.size());
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showNotification("❌ Erreur", "Impossible de charger les modèles");
            });
        });

        new Thread(task).start();
    }

    /**
     * Gérer la sélection de fichiers
     */
    @FXML
    private void handleBrowseFiles() {
        log.info("📂 Ouverture du sélecteur de fichiers...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner des documents");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents",
                        "*.pdf", "*.docx", "*.doc", "*.xlsx", "*.xls", "*.txt")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(dropzone.getScene().getWindow());

        if (files == null || files.isEmpty()) {
            log.info("⚠️ Aucun fichier sélectionné");
            return;
        }

        // Valider les fichiers
        List<File> validFiles = fileUploadService.validateFiles(files);

        if (validFiles.isEmpty()) {
            showNotification("❌ Fichiers invalides",
                    "Les fichiers sélectionnés ne sont pas valides");
            return;
        }

        // Ajouter les fichiers à la liste
        filesList.getChildren().clear();
        selectedFiles.clear();

        for (File file : validFiles) {
            selectedFiles.add(file);
            filesList.getChildren().add(createFileItem(file));
        }

        filesContainer.setVisible(true);
        filesContainer.setManaged(true);
        updateFileCount();

        log.info("✅ {} fichiers sélectionnés", validFiles.size());
    }

    /**
     * Créer un élément visuel pour un fichier
     */
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

    /**
     * Mettre à jour le compteur de fichiers
     */
    private void updateFileCount() {
        int count = filesList.getChildren().size();
        filesCountLabel.setText("Fichiers Importés (" + count + ")");
    }

    /**
     * Lancer l'audit complet
     */
    @FXML
    private void handleStartAudit() {
        log.info("🚀 Démarrage de l'audit...");

        // Validations
        if (selectedProject == null) {
            showNotification("⚠️ Projet requis", "Veuillez sélectionner un projet");
            return;
        }

        if (selectedModel == null) {
            showNotification("⚠️ Modèle requis", "Veuillez sélectionner un modèle");
            return;
        }

        if (selectedFiles.isEmpty()) {
            showNotification("⚠️ Documents requis", "Veuillez sélectionner au moins un document");
            return;
        }

        // Afficher la boîte de dialogue de progression
        showProgressDialog();
    }

    /**
     * Afficher la boîte de dialogue de progression
     */
    private void showProgressDialog() {
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

        Label statusLabel = new Label("Initialisation...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #667085;");

        Label percentLabel = new Label("0%");
        percentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");

        HBox progressHeader = new HBox(statusLabel, new Region(), percentLabel);
        progressHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressHeader.getChildren().get(1), Priority.ALWAYS);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(10);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #1E88E5;");

        Label descLabel = new Label("L'IA analyse vos documents pour détecter les problèmes de conformité...");
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #667085; -fx-wrap-text: true;");

        dialogContent.getChildren().addAll(titleLabel, progressHeader, progressBar, descLabel);
        progressDialog.getDialogPane().setContent(dialogContent);
        progressDialog.getDialogPane().lookupButton(closeButtonType).setVisible(false);

        // Créer la tâche d'audit
        Task<AuditResponseDto> auditTask = createAuditTask(statusLabel, percentLabel, progressBar);

        auditTask.setOnSucceeded(e -> {
            progressDialog.close();
            AuditResponseDto audit = auditTask.getValue();
            showAuditResultsDialog(audit);
            showSuccessNotification();
        });

        auditTask.setOnFailed(e -> {
            progressDialog.close();
            showErrorNotification();
        });

        new Thread(auditTask).start();
        progressDialog.show();
    }

    /**
     * Créer la tâche d'audit complète
     */
    private Task<AuditResponseDto> createAuditTask(Label statusLabel, Label percentLabel, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected AuditResponseDto call() throws Exception {
                try {
                    // Étape 1: Créer l'audit
                    updateMessage("Création de l'audit...");
                    updateProgress(1, 5);

                    AuditCreateRequestDto request = new AuditCreateRequestDto();
                    request.setProjectId(selectedProject.getId());
                    request.setModelId(selectedModel.getId());
                    request.setDocumentIds(new ArrayList<>()); // Sera rempli après upload

                    AuditResponseDto audit = auditApiService.createAudit(request);
                    currentAuditId = audit.getId();

                    log.info("✅ Audit créé avec ID: {}", currentAuditId);

                    // Étape 2: Upload des documents
                    updateMessage("Upload des documents...");
                    updateProgress(2, 5);

                    List<AuditDocumentDto> uploadedDocs = fileUploadService.uploadMultipleFiles(
                            selectedFiles, currentAuditId
                    );

                    log.info("✅ {} documents uploadés", uploadedDocs.size());

                    // Étape 3: Lancer l'analyse
                    updateMessage("Lancement de l'analyse IA...");
                    updateProgress(3, 5);

                    auditApiService.startAnalysis(currentAuditId);

                    log.info("✅ Analyse lancée");

                    // Étape 4: Polling du statut
                    updateMessage("Analyse en cours...");
                    updateProgress(4, 5);

                    AuditResponseDto finalAudit = auditApiService.pollAuditStatus(
                            currentAuditId, 60, 2 // 60 tentatives toutes les 2 secondes
                    );

                    // Étape 5: Terminé
                    updateMessage("Analyse terminée ✅");
                    updateProgress(5, 5);

                    return finalAudit;

                } catch (Exception e) {
                    log.error("❌ Erreur lors de l'audit", e);
                    throw e;
                }
            }
        };
    }

    /**
     * Afficher la boîte de dialogue des résultats
     */
    private void showAuditResultsDialog(AuditResponseDto audit) {
        Dialog<Void> resultsDialog = new Dialog<>();
        resultsDialog.setTitle("Analyse terminée");
        resultsDialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        resultsDialog.getDialogPane().getButtonTypes().add(closeButton);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setPrefWidth(700);
        mainContent.setPrefHeight(600);

        // Titre
        Label titleLabel = new Label("Analyse terminée");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

        // Rapport de conformité
        VBox reportBox = createReportSummaryBox(audit);

        // Liste des problèmes
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background-color: white;");

        VBox issuesContainer = new VBox(12);
        issuesContainer.setPadding(new Insets(10));

        if (audit.getIssues() != null && !audit.getIssues().isEmpty()) {
            for (var issue : audit.getIssues()) {
                issuesContainer.getChildren().add(createIssueCard(issue));
            }
        } else {
            Label noIssuesLabel = new Label("✅ Aucun problème détecté");
            noIssuesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981;");
            issuesContainer.getChildren().add(noIssuesLabel);
        }

        scrollPane.setContent(issuesContainer);

        // Boutons d'action
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER);

        Button newAuditBtn = new Button("Lancer un Nouvel Audit");
        newAuditBtn.setStyle("""
            -fx-background-color: #1E88E5;
            -fx-text-fill: white;
            -fx-font-weight: 600;
            -fx-font-size: 13px;
            -fx-padding: 10 20;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);
        newAuditBtn.setOnAction(e -> {
            resultsDialog.close();
            resetAuditForm();
        });

        actionButtons.getChildren().add(newAuditBtn);

        mainContent.getChildren().addAll(titleLabel, reportBox, scrollPane, actionButtons);
        resultsDialog.getDialogPane().setContent(mainContent);
        resultsDialog.show();
    }

    /**
     * Créer le résumé du rapport
     */
    private VBox createReportSummaryBox(AuditResponseDto audit) {
        VBox box = new VBox(8);
        box.setStyle("""
            -fx-background-color: #f3f4f6;
            -fx-padding: 15;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        Label title = new Label("Rapport de Conformité");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");

        int docsCount = audit.getDocuments() != null ? audit.getDocuments().size() : 0;
        int issuesCount = audit.getProblemsCount() != null ? audit.getProblemsCount() : 0;

        Label subtitle = new Label(String.format(
                "Analyse terminée pour %d document(s) - %d problème(s) détecté(s)",
                docsCount, issuesCount
        ));
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        box.getChildren().addAll(title, subtitle);
        return box;
    }

    /**
     * Créer une carte pour un problème
     */
    private VBox createIssueCard(com.yourapp.dto.AuditIssueDto issue) {
        VBox card = new VBox(10);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 15;
        """);

        Label typeLabel = new Label(issue.getIssueType());
        typeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #dc2626;");

        Label descLabel = new Label(issue.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151; -fx-wrap-text: true;");

        if (issue.getLocation() != null) {
            Label locationLabel = new Label("📍 " + issue.getLocation());
            locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
            card.getChildren().add(locationLabel);
        }

        if (issue.getSuggestion() != null) {
            Label suggestionLabel = new Label("💡 " + issue.getSuggestion());
            suggestionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #059669; -fx-wrap-text: true;");
            card.getChildren().add(suggestionLabel);
        }

        card.getChildren().addAll(0, List.of(typeLabel, descLabel));
        return card;
    }

    /**
     * Réinitialiser le formulaire d'audit
     */
    private void resetAuditForm() {
        filesList.getChildren().clear();
        selectedFiles.clear();
        filesContainer.setVisible(false);
        filesContainer.setManaged(false);
        projetDropdown.setValue(null);
        partenaireDropdown.setValue(null);
        selectedProject = null;
        selectedModel = null;
        currentAuditId = null;

        showNotification("🔄 Réinitialisé", "Prêt pour un nouvel audit");
    }

    /**
     * Créer la boîte de notifications
     */
    private void createNotificationBox() {
        notificationBox = new VBox(10);
        notificationBox.setAlignment(Pos.TOP_RIGHT);
        notificationBox.setStyle("-fx-padding: 20;");
        notificationBox.setPickOnBounds(false);
    }

    /**
     * Afficher une notification
     */
    private void showNotification(String title, String message) {
        Platform.runLater(() -> {
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

            closeBtn.setOnAction(e -> notificationBox.getChildren().remove(notification));

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5),
                    e -> notificationBox.getChildren().remove(notification)));
            timeline.play();

            log.info("📢 Notification: {} - {}", title, message);
        });
    }

    private void showSuccessNotification() {
        showNotification("✅ Audit terminé", "Les résultats sont disponibles");
    }

    private void showErrorNotification() {
        showNotification("❌ Erreur", "Une erreur s'est produite. Veuillez réessayer.");
    }
}