package com.yourapp.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import com.yourapp.services.GeminiService;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextField;
import com.yourapp.dto.DashboardStatsDto;
import com.yourapp.dto.ProjectProgressDto;
import com.yourapp.dto.RecentActivityDto;
import com.yourapp.services.DashboardService;
import com.yourapp.services.GeminiService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalAuditsLabel;
    @FXML private Label auditsThisMonthLabel;
    @FXML private Label projectsLabel;
    @FXML private Label projectsThisWeekLabel;
    @FXML private Label conformeLabel;
    @FXML private Label nonConformeLabel;
    @FXML private Label globalScoreLabel;
    @FXML private Label complianceStatusLabel;
    @FXML private VBox recentAuditsContainer;
    @FXML private VBox projectProgressContainer;
    @FXML private VBox complianceCard;
    @FXML private ImageView complianceStatusIcon;
    @FXML private VBox aiChatContainer;
    @FXML private TextField chatInput;
    @FXML private VBox chatMessagesContainer;
    @FXML private TextField chatInputField;
    @FXML private Button chatSendButton;
    @FXML private Button refreshButton;

    @Autowired private DashboardService dashboardService;
    @Autowired private GeminiService chatbotService;
    @Autowired private com.yourapp.utils.DashboardEventListener eventListener;

    @FXML
    public void initialize() {
        log.info("🚀 Initialisation du Dashboard");

        // Enregistrer ce contrôleur pour les événements de rafraîchissement
        eventListener.registerDashboardController(this);

        // Configurer le bouton de rafraîchissement
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadDashboardData());
        }

        // Charger les données
        loadDashboardData();

        // Initialiser le chatbot
        initializeChatbot();

        log.info("✅ Dashboard initialisé avec succès");
    }

    /**
     * Méthode publique pour rafraîchir le dashboard depuis l'extérieur
     */
    public void refresh() {
        loadDashboardData();
    }

    /**
     * Charger les données du dashboard avec les données réelles
     */
    private void loadDashboardData() {
        log.info("🔄 Chargement des données du dashboard...");

        // Récupérer l'utilisateur connecté
        com.yourapp.model.User currentUser =
                com.yourapp.utils.SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            Platform.runLater(() -> {
                welcomeLabel.setText("Bienvenue, Invité 👋");
                showError("Aucun utilisateur connecté");
            });
            return;
        }

        Long userId = currentUser.getId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // Charger les statistiques
                    DashboardStatsDto stats = dashboardService.getDashboardStats(userId);

                    // Charger la progression des projets
                    List<ProjectProgressDto> projectsProgress =
                            dashboardService.getProjectsProgress();

                    // Charger les activités récentes
                    List<RecentActivityDto> activities =
                            dashboardService.getRecentActivities();

                    // Mettre à jour l'interface sur le thread JavaFX
                    Platform.runLater(() -> {
                        updateStats(stats);
                        updateProjectsProgress(projectsProgress);
                        updateRecentActivities(activities);
                        log.info("✅ Dashboard mis à jour avec succès");
                    });

                } catch (Exception e) {
                    log.error("❌ Erreur lors du chargement des données du dashboard", e);
                    Platform.runLater(() ->
                            showError("Erreur de chargement: " + e.getMessage())
                    );
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    /**
     * Mettre à jour les statistiques du dashboard
     */
    private void updateStats(DashboardStatsDto stats) {
        // Message de bienvenue
        welcomeLabel.setText("Bienvenue, " + stats.getUserName() + " 👋");

        // Statistiques d'audits
        totalAuditsLabel.setText(String.valueOf(stats.getTotalAudits()));
        auditsThisMonthLabel.setText("+" + stats.getAuditsThisMonth() + " ce mois");

        // Statistiques de projets
        projectsLabel.setText(String.valueOf(stats.getTotalProjects()));
        projectsThisWeekLabel.setText("+" + stats.getProjectsThisWeek() + " cette semaine");

        // Conformité
        conformeLabel.setText(String.valueOf(stats.getAuditsConforme()));
        nonConformeLabel.setText(String.valueOf(stats.getAuditsNonConforme()));

        // Score global
        int score = stats.getGlobalScore();
        globalScoreLabel.setText(score + "%");
        complianceStatusLabel.setText(stats.getComplianceStatus());

        // Appliquer le style selon le score
        applyComplianceStyle(score);

        log.debug("📊 Stats mises à jour: {} audits, {} projets, score {}%",
                stats.getTotalAudits(), stats.getTotalProjects(), score);
    }

    /**
     * Appliquer le style de conformité selon le score
     */
    private void applyComplianceStyle(int score) {
        String baseStyle = "-fx-background-radius: 12px;" +
                "-fx-padding: 25px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 14, 0, 0, 5);" +
                "-fx-cursor: hand;";

        String colorStyle;
        if (score >= 80) {
            // Vert pour excellent
            colorStyle = "-fx-background-color: linear-gradient(to right, #10b981, #059669);";
        } else if (score >= 60) {
            // Bleu pour bon
            colorStyle = "-fx-background-color: linear-gradient(to right, #0ea5e9, #2563eb);";
        } else if (score >= 40) {
            // Orange pour moyen
            colorStyle = "-fx-background-color: linear-gradient(to right, #f59e0b, #d97706);";
        } else {
            // Rouge pour insuffisant
            colorStyle = "-fx-background-color: linear-gradient(to right, #ef4444, #dc2626);";
        }

        complianceCard.setStyle(baseStyle + colorStyle);

        String iconFile = score < 50 ? "close.png" : "check-mark.png";
        if (complianceStatusIcon != null) {
            String iconPath = Objects.requireNonNull(
                    getClass().getResource("/views/icons/" + iconFile)
            ).toExternalForm();
            complianceStatusIcon.setImage(new Image(iconPath));
        }
    }

    /**
     * Mettre à jour la progression des projets
     */
    private void updateProjectsProgress(List<ProjectProgressDto> projects) {
        projectProgressContainer.getChildren().clear();

        if (projects.isEmpty()) {
            Label emptyLabel = new Label("Aucun projet en cours");
            emptyLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            projectProgressContainer.getChildren().add(emptyLabel);
            return;
        }

        for (ProjectProgressDto project : projects) {
            addProjectProgress(
                    project.getProjectName(),
                    project.getProgress(),
                    project.getStatus()
            );
        }

        log.debug("📈 {} projets affichés", projects.size());
    }

    /**
     * Mettre à jour les activités récentes
     */
    private void updateRecentActivities(List<RecentActivityDto> activities) {
        recentAuditsContainer.getChildren().clear();

        if (activities.isEmpty()) {
            Label emptyLabel = new Label("Aucune activité récente");
            emptyLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            recentAuditsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (RecentActivityDto activity : activities) {
            addActivity(
                    activity.getTitle(),
                    activity.getTime(),
                    activity.getIcon(),
                    activity.getType()
            );
        }

        log.debug("📋 {} activités affichées", activities.size());
    }

    /**
     * Ajouter une barre de progression de projet
     */
    private void addProjectProgress(String name, double progress, String status) {
        VBox row = new VBox(5);
        row.setPadding(new Insets(5));

        // En-tête avec nom et pourcentage
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label statusLbl = new Label("(" + status + ")");
        statusLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-padding: 0 5px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label percentLbl = new Label((int)(progress * 100) + "%");
        percentLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #3b82f6;");

        labelBox.getChildren().addAll(nameLbl, statusLbl, spacer, percentLbl);

        // Barre de progression
        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.getStyleClass().add("custom-progress");
        pb.setPrefHeight(8);

        row.getChildren().addAll(labelBox, pb);
        projectProgressContainer.getChildren().add(row);
    }

    /**
     * Ajouter une activité récente
     */
    private void addActivity(String title, String time, String icon, String type) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 10, 12, 10));
        row.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

        // Icône avec fond coloré selon le type
        Label iconLbl = new Label(icon);
        iconLbl.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-min-width: 35px; -fx-min-height: 35px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 8px;" +
                        getIconBackgroundColor(type)
        );

        // Texte de l'activité
        VBox texts = new VBox(3);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        titleLbl.setWrapText(true);

        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        texts.getChildren().addAll(titleLbl, timeLbl);

        row.getChildren().addAll(iconLbl, texts);
        recentAuditsContainer.getChildren().add(row);
    }

    /**
     * Obtenir la couleur de fond pour l'icône selon le type d'activité
     */
    private String getIconBackgroundColor(String type) {
        return switch (type) {
            case "AUDIT_COMPLETED" -> "-fx-background-color: #d1fae5;";
            case "DOCUMENT_UPLOADED" -> "-fx-background-color: #dbeafe;";
            case "PROJECT_CREATED" -> "-fx-background-color: #fef3c7;";
            default -> "-fx-background-color: #f1f5f9;";
        };
    }

    // =============== CHATBOT LOGIC ===============

    /**
     * Initializes the chatbot by clearing the placeholder and adding a welcome message.
     */
    private void initializeChatbot() {
        // Matches fx:id="aiChatContainer" in Dashboard.fxml
        if (aiChatContainer != null) {
            aiChatContainer.getChildren().clear();
            addMessageToChat("Bonjour ! Je suis votre assistant IA. Comment puis-je vous aider aujourd'hui ?", false);
        }
    }

    @FXML
    private void handleSendMessage() {
        // Matches fx:id="chatInput" in Dashboard.fxml
        String message = chatInput.getText().trim();
        if (message.isEmpty()) return;

        // 1. Add User Message (Blue bubble, right side)
        addMessageToChat(message, true);
        chatInput.clear();

        // 2. Run Gemini API call in background using Spring-managed service
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                // Updated to use 'chatbotService' field and 'askGemini' method
                return chatbotService.askGemini(message);
            }
        };

        task.setOnSucceeded(e -> {
            // 3. Add AI Response (Grey bubble, left side)
            Platform.runLater(() -> addMessageToChat(task.getValue(), false));
        });

        task.setOnFailed(e -> {
            log.error("Gemini API Error", task.getException());
            Platform.runLater(() -> addMessageToChat("Désolé, je rencontre un problème de connexion.", false));
        });

        new Thread(task).start();
    }

    private void addMessageToChat(String text, boolean isUser) {
        HBox row = new HBox();
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));

        if (isUser) {
            Label textLbl = new Label(text);
            textLbl.setWrapText(true);
            textLbl.setMaxWidth(250);
            textLbl.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 8 12;");
            row.getChildren().add(textLbl);
        } else {
            TextFlow textFlow = new TextFlow();
            textFlow.setMaxWidth(250);
            textFlow.setStyle("-fx-background-color: #f1f3f4; -fx-background-radius: 15; -fx-padding: 8 12;");

            // Split the text by "**" markers for bold formatting
            String[] parts = text.split("\\*\\*");
            for (int i = 0; i < parts.length; i++) {
                Text t = new Text(parts[i]);
                if (i % 2 == 1) {
                    t.setFont(Font.font("System", FontWeight.BOLD, 12));
                } else {
                    t.setFont(Font.font("System", FontWeight.NORMAL, 12));
                }
                t.setStyle("-fx-fill: black;");
                textFlow.getChildren().add(t);
            }
            row.getChildren().add(textFlow);
        }

        aiChatContainer.getChildren().add(row);
    }
    /**
     * Helper method to show error alerts to the user.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}