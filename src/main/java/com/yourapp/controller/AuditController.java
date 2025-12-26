package com.yourapp.controller;

import com.yourapp.dto.AuditCreateRequestDto;
import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.dto.AuditResponseDto;
import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.AuditIssueDto;
import com.yourapp.model.Project;
import com.yourapp.services_UI.AuditApiService;
import com.yourapp.services_UI.FileUploadService;
import com.yourapp.services_UI.ModelService;
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
import com.yourapp.services_UI.ReportService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur JavaFX pour la page d'audit
 * Communique uniquement avec le backend via les services (pas de HTTP)
 */
@Component
@Slf4j
public class AuditController {

    // ============ FXML Components ============
    @FXML private VBox dropzone;
    @FXML private ComboBox<Project> projetDropdown;
    @FXML private ComboBox<AuditTemplateDTO> partenaireDropdown;
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
    @Autowired private ModelService modelService;
    @Autowired private AuditApiService auditApiService;
    @Autowired private FileUploadService fileUploadService;
    @Autowired private ReportService reportService;

    // ============ Variables d'état ============
    private VBox notificationBox;
    private List<File> selectedFiles = new ArrayList<>();
    private Long currentAuditId;
    private Project selectedProject;
    private AuditTemplateDTO selectedModel;

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
            } else {
                partenaireDropdown.getItems().clear();
                selectedModel = null;
            }
        });

        // Configurer le modèle dropdown
        partenaireDropdown.setConverter(new javafx.util.StringConverter<AuditTemplateDTO>() {
            @Override
            public String toString(AuditTemplateDTO template) {
                return template != null ? template.getName() : "";
            }

            @Override
            public AuditTemplateDTO fromString(String string) {
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

        Task<List<AuditTemplateDTO>> task = new Task<>() {
            @Override
            protected List<AuditTemplateDTO> call() {
                try {
                    return modelService.getModelsByProject(projectId);
                } catch (Exception e) {
                    log.error("❌ Erreur lors du chargement des modèles", e);
                    return new ArrayList<>();
                }
            }
        };

        task.setOnSucceeded(e -> {
            List<AuditTemplateDTO> models = task.getValue();
            Platform.runLater(() -> {
                partenaireDropdown.getItems().clear();
                partenaireDropdown.getItems().addAll(models);
                partenaireDropdown.setValue(null);
                selectedModel = null;

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

        List<File> validFiles = fileUploadService.validateFiles(files);

        if (validFiles.isEmpty()) {
            showNotification("❌ Fichiers invalides",
                    "Les fichiers sélectionnés ne sont pas valides");
            return;
        }

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
    /**
     * Créer un élément visuel pour un fichier (Version sans bouton supprimer)
     */
    private HBox createFileItem(File file) {
        // --- INFOS DU FICHIER ---
        Label fileName = new Label(file.getName());
        fileName.setStyle("-fx-font-weight: 600; -fx-text-fill: #1f2937;");

        Label fileSize = new Label(String.format("%.2f KB", file.length() / 1024.0));
        fileSize.setStyle("-fx-text-fill: #667085; -fx-font-size: 12px;");

        VBox fileInfo = new VBox(fileName, fileSize);
        fileInfo.setSpacing(3);
        HBox.setHgrow(fileInfo, Priority.ALWAYS); // Permet aux infos de prendre l'espace

        // --- BOUTON OEIL (Visualiser) ---
        Button viewBtn = new Button("👁"); // Icône oeil
        viewBtn.setStyle("""
            -fx-background-color: #f3f4f6;
            -fx-text-fill: #1E88E5;
            -fx-font-size: 16px;
            -fx-padding: 5 10;
            -fx-background-radius: 5;
            -fx-cursor: hand;
        """);

        // Action pour l'oeil (Ouvrir le fichier localement pour vérification)
        viewBtn.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                log.error("Impossible d'ouvrir le fichier : {}", ex.getMessage());
            }
        });

        /* // --- BOUTON CORBEILLE SUPPRIMÉ ---
        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: transparent; ...");
        removeBtn.setOnAction(e -> { ... });
        */

        // --- ASSEMBLAGE ---
        HBox fileItem = new HBox(fileInfo, viewBtn); // On ne met QUE fileInfo et viewBtn
        fileItem.setAlignment(Pos.CENTER_LEFT);
        fileItem.setSpacing(15);
        fileItem.setStyle("""
            -fx-padding: 12;
            -fx-border-color: #e4e8ee;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-background-color: #ffffff;
        """);

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

        Task<AuditResponseDto> auditTask = createAuditTask(statusLabel, percentLabel, progressBar);

        auditTask.setOnSucceeded(e -> {
            progressDialog.close();
            AuditResponseDto audit = auditTask.getValue();

            // 🔥 FIX: Récupérer les issues depuis le service
            log.info("📊 Récupération des issues pour l'audit {}", audit.getId());
            List<AuditIssueDto> issues = auditApiService.getIssuesByAudit(audit.getId());
            audit.setIssues(issues);

            log.info("✅ {} issues récupérées pour affichage", issues.size());
            showAuditResultsDialog(audit);
            showSuccessNotification();
        });

        auditTask.setOnFailed(e -> {
            progressDialog.close();
            Throwable exception = auditTask.getException();
            log.error("❌ Erreur lors de l'audit", exception);
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
                    Platform.runLater(() -> {
                        statusLabel.setText("Création de l'audit...");
                        percentLabel.setText("20%");
                        progressBar.setProgress(0.2);
                    });

                    AuditCreateRequestDto request = new AuditCreateRequestDto();
                    request.setProjectId(selectedProject.getId());
                    request.setModelId(selectedModel.getId());
                    request.setDocumentIds(new ArrayList<>());

                    AuditResponseDto audit = auditApiService.createAudit(request);
                    currentAuditId = audit.getId();

                    log.info("✅ Audit créé avec ID: {}", currentAuditId);

                    // Étape 2: Upload des documents (Dans AuditController.java)
                    Platform.runLater(() -> {
                        statusLabel.setText("Upload des documents...");
                        percentLabel.setText("40%");
                        progressBar.setProgress(0.4);
                    });

// 🔥 MODIFICATION ICI : On ajoute selectedProject.getId()
                    List<AuditDocumentDto> uploadedDocs = fileUploadService.uploadMultipleFiles(
                            selectedFiles,
                            currentAuditId,
                            selectedProject.getId()
                    );

                    log.info("✅ {} documents liés au projet {}", uploadedDocs.size(), selectedProject.getName());

                    // Étape 3: Lancer l'analyse
                    Platform.runLater(() -> {
                        statusLabel.setText("Lancement de l'analyse IA...");
                        percentLabel.setText("60%");
                        progressBar.setProgress(0.6);
                    });

                    auditApiService.startAnalysis(currentAuditId);

                    log.info("✅ Analyse lancée");

                    // Étape 4: Polling du statut - OPTIMISÉ avec timeout réduit
                    Platform.runLater(() -> {
                        statusLabel.setText("Analyse en cours...");
                        percentLabel.setText("80%");
                        progressBar.setProgress(0.8);
                    });

                    // Polling avec timeout de 30 secondes max (au lieu de 60)
                    AuditResponseDto finalAudit = auditApiService.pollAuditStatus(
                            currentAuditId, 30, 2
                    );

                    // Étape 5: Terminé
                    Platform.runLater(() -> {
                        statusLabel.setText("Analyse terminée ✅");
                        percentLabel.setText("100%");
                        progressBar.setProgress(1.0);
                    });

                    return finalAudit;

                } catch (Exception e) {
                    log.error("❌ Erreur lors de l'audit", e);
                    throw e;
                }
            }
        };
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
        int issuesCount = audit.getIssues() != null ? audit.getIssues().size() : 0;

        Label subtitle = new Label(String.format(
                "Analyse terminée pour %d document(s) - %d problème(s) détecté(s)",
                docsCount, issuesCount
        ));
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        box.getChildren().addAll(title, subtitle);
        return box;
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

    /**
     * Afficher la boîte de dialogue des résultats
     */
    private void showAuditResultsDialog(AuditResponseDto audit) {
        Dialog<Void> resultsDialog = new Dialog<>();
        resultsDialog.setTitle("Analyse terminée");
        resultsDialog.setHeaderText(null);

        // FIX: Ajuster la taille de la fenêtre pour qu'elle tienne dans l'écran
        resultsDialog.setResizable(true);
        resultsDialog.getDialogPane().setMinSize(700, 600);
        resultsDialog.getDialogPane().setPrefSize(800, 650);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        resultsDialog.getDialogPane().getButtonTypes().add(closeButton);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #f9fafb;");

        // ========== TITRE ==========
        Label titleLabel = new Label("✅ Analyse terminée");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #1f2937;");

        // ========== RAPPORT DE CONFORMITÉ ==========
        VBox reportBox = createReportSummaryBox(audit);

        // ========== ONGLETS: PROBLÈMES ET RECOMMANDATIONS ==========
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("""
        -fx-background-color: white;
        -fx-border-radius: 12px;
        -fx-background-radius: 12px;
    """);

        // 🔴 ONGLET 1: PROBLÈMES IDENTIFIÉS
        Tab problemsTab = new Tab("🔴 Problèmes Identifiés");
        problemsTab.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");

        ScrollPane problemsScrollPane = new ScrollPane();
        problemsScrollPane.setFitToWidth(true);
        problemsScrollPane.setStyle("-fx-background-color: white;");
        problemsScrollPane.setPrefHeight(300);

        VBox problemsContainer = new VBox(12);
        problemsContainer.setPadding(new Insets(15));

        if (audit.getIssues() != null && !audit.getIssues().isEmpty()) {
            log.info("📋 Affichage de {} problèmes", audit.getIssues().size());
            for (var issue : audit.getIssues()) {
                problemsContainer.getChildren().add(createProblemCard(issue));
            }
        } else {
            Label noIssuesLabel = new Label("✅ Aucun problème critique détecté");
            noIssuesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-padding: 20;");
            problemsContainer.getChildren().add(noIssuesLabel);
        }

        problemsScrollPane.setContent(problemsContainer);
        problemsTab.setContent(problemsScrollPane);

        // 💡 ONGLET 2: RECOMMANDATIONS
        Tab recommendationsTab = new Tab("💡 Recommandations");
        recommendationsTab.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");

        ScrollPane recoScrollPane = new ScrollPane();
        recoScrollPane.setFitToWidth(true);
        recoScrollPane.setStyle("-fx-background-color: white;");
        recoScrollPane.setPrefHeight(300);

        VBox recoContainer = new VBox(12);
        recoContainer.setPadding(new Insets(15));

        // Générer des recommandations basées sur les problèmes
        if (audit.getIssues() != null && !audit.getIssues().isEmpty()) {
            for (var issue : audit.getIssues()) {
                if (issue.getSuggestion() != null && !issue.getSuggestion().isEmpty()) {
                    recoContainer.getChildren().add(createRecommendationCard(issue));
                }
            }
        }

        if (recoContainer.getChildren().isEmpty()) {
            Label noRecoLabel = new Label("✅ Aucune recommandation spécifique");
            noRecoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 20;");
            recoContainer.getChildren().add(noRecoLabel);
        }

        recoScrollPane.setContent(recoContainer);
        recommendationsTab.setContent(recoScrollPane);

        tabPane.getTabs().addAll(problemsTab, recommendationsTab);

        // ========== BOUTONS D'ACTION ==========
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        // 📥 BOUTON TÉLÉCHARGER LE RAPPORT
        Button downloadBtn = new Button("📥 Télécharger le Rapport");
        downloadBtn.setStyle("""
        -fx-background-color: #10b981;
        -fx-text-fill: white;
        -fx-font-weight: 600;
        -fx-font-size: 14px;
        -fx-padding: 12 24;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        downloadBtn.setOnMouseEntered(e -> downloadBtn.setStyle(downloadBtn.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.5), 10, 0, 0, 2);"));
        downloadBtn.setOnMouseExited(e -> downloadBtn.setStyle(downloadBtn.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.5), 10, 0, 0, 2);", "")));
        downloadBtn.setOnAction(e -> handleDownloadReport(audit));

        // 🔄 BOUTON NOUVEL AUDIT
        Button newAuditBtn = new Button("🔄 Lancer un Nouvel Audit");
        newAuditBtn.setStyle("""
        -fx-background-color: #1E88E5;
        -fx-text-fill: white;
        -fx-font-weight: 600;
        -fx-font-size: 14px;
        -fx-padding: 12 24;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        newAuditBtn.setOnAction(e -> {
            resultsDialog.close();
            resetAuditForm();
        });

        actionButtons.getChildren().addAll(downloadBtn, newAuditBtn);

        mainContent.getChildren().addAll(titleLabel, reportBox, tabPane, actionButtons);
        resultsDialog.getDialogPane().setContent(mainContent);
        resultsDialog.showAndWait();
    }

    /**
     * Créer une carte pour un problème - AMÉLIORÉE
     */
    private VBox createProblemCard(AuditIssueDto issue) {
        VBox card = new VBox(10);
        card.setStyle("""
        -fx-background-color: #fef2f2;
        -fx-border-color: #fca5a5;
        -fx-border-width: 1;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-padding: 15;
    """);

        // Icône + Type
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🔴");
        icon.setStyle("-fx-font-size: 18px;");

        Label typeLabel = new Label(issue.getIssueType() != null ? issue.getIssueType() : "Problème");
        typeLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #dc2626;");

        header.getChildren().addAll(icon, typeLabel);

        // Description
        Label descLabel = new Label(issue.getDescription() != null ? issue.getDescription() : "Aucune description");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-wrap-text: true;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(header, descLabel);

        // Location
        if (issue.getLocation() != null && !issue.getLocation().isEmpty()) {
            Label locationLabel = new Label("📍 " + issue.getLocation());
            locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            card.getChildren().add(locationLabel);
        }

        return card;
    }

    /**
     * Créer une carte pour une recommandation
     */
    private VBox createRecommendationCard(AuditIssueDto issue) {
        VBox card = new VBox(10);
        card.setStyle("""
        -fx-background-color: #f0fdf4;
        -fx-border-color: #86efac;
        -fx-border-width: 1;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-padding: 15;
    """);

        // Icône + Titre
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("💡");
        icon.setStyle("-fx-font-size: 18px;");

        Label titleLabel = new Label("Recommandation: " +
                (issue.getIssueType() != null ? issue.getIssueType() : ""));
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #059669;");

        header.getChildren().addAll(icon, titleLabel);

        // Suggestion
        Label suggestionLabel = new Label(issue.getSuggestion());
        suggestionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #065f46; -fx-wrap-text: true;");
        suggestionLabel.setWrapText(true);

        card.getChildren().addAll(header, suggestionLabel);

        return card;
    }

    /**
     * Gérer le téléchargement du rapport - MODIFIÉ pour Word/PDF
     */
    private void handleDownloadReport(AuditResponseDto audit) {
        log.info("📥 Téléchargement du rapport pour l'audit {}", audit.getId());

        // Créer une boîte de dialogue pour choisir le format
        Alert formatDialog = new Alert(Alert.AlertType.CONFIRMATION);
        formatDialog.setTitle("Format du Rapport");
        formatDialog.setHeaderText("Choisissez le format du rapport");
        formatDialog.setContentText("Quel format préférez-vous ?");

        // MODIFICATION: Supprimer TXT/HTML, ajouter Word/PDF
        ButtonType pdfButton = new ButtonType("📕 PDF (.pdf)");
        ButtonType wordButton = new ButtonType("📝 Word (.docx)");
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        formatDialog.getButtonTypes().setAll(pdfButton, wordButton, cancelButton);

        formatDialog.showAndWait().ifPresent(choice -> {
            if (choice == pdfButton) {
                downloadReportAs(audit, "pdf");
            } else if (choice == wordButton) {
                downloadReportAs(audit, "docx");
            }
        });
    }

    /**
     * Télécharger le rapport dans le format spécifié
     */
    private void downloadReportAs(AuditResponseDto audit, String format) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le Rapport");

        String fileName = "Rapport_Audit_" + audit.getId() +
                ("pdf".equals(format) ? ".pdf" : ".docx");
        fileChooser.setInitialFileName(fileName);

        if ("pdf".equals(format)) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        } else {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"));
        }

        File file = fileChooser.showSaveDialog(dropzone.getScene().getWindow());
        if (file == null) return;

        // 🔹 Génération EN ARRIÈRE-PLAN (sans UI)
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if ("pdf".equals(format)) {
                    reportService.generateAndSavePdfReport(audit, file);
                } else {
                    reportService.generateAndSaveWordReport(audit, file);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Rapport généré");
            success.setHeaderText("Le rapport a été généré avec succès !");
            success.setContentText("Voulez-vous ouvrir le fichier ?");

            success.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(file);
                        }
                    } catch (Exception ex) {
                        log.error("Impossible d'ouvrir le fichier", ex);
                    }
                }
            });
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            log.error("Erreur génération rapport", ex);

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText("Erreur lors de la génération du rapport");
            error.setContentText(ex.getMessage());
            error.show();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


}