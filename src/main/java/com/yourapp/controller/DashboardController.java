package com.yourapp.controller;

import com.yourapp.services.DashboardService;
import com.yourapp.services.ChatbotService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    @FXML private VBox chatMessagesContainer;
    @FXML private TextField chatInputField;
    @FXML private Button chatSendButton;
    @FXML private VBox chatSuggestionsContainer;

    @Autowired private DashboardService dashboardService;
    @Autowired private ChatbotService chatbotService;

//    private static final Long CURRENT_USER_ID = 1L; // À remplacer par l'ID utilisateur connecté

    @FXML
    public void initialize() {
        log.info("🚀 Initialisation du Dashboard");

        // Charger les données
        loadDashboardData();

        // Initialiser le chatbot
        initializeChatbot();

        log.info("✅ Dashboard initialisé avec succès");
    }

    /**
     * Charger les données du dashboard
     */
    private void loadDashboardData() {
        // 1. Get the real user from the session
        com.yourapp.model.User currentUser = com.yourapp.utils.SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            // If no one is logged in, we can't show a name
            Platform.runLater(() -> welcomeLabel.setText("Bienvenue, Invité "));
            return;
        }

        Long actualUserId = currentUser.getId(); // Use the actual ID from the database

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // 2. Pass the dynamic ID to the service
                    DashboardService.DashboardStats stats =
                            dashboardService.getDashboardStats(actualUserId);

                    List<DashboardService.ProjectProgress> projectsProgress =
                            dashboardService.getProjectsProgress();

                    List<DashboardService.RecentActivity> activities =
                            dashboardService.getRecentActivities();

                    Platform.runLater(() -> {
                        updateStats(stats);
                        updateProjectsProgress(projectsProgress);
                        updateRecentActivities(activities);
                    });
                } catch (Exception e) {
                    log.error("❌ Erreur lors du chargement des données", e);
                    Platform.runLater(() -> showError("Erreur de chargement: " + e.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Mettre à jour les statistiques
     */
    private void updateStats(DashboardService.DashboardStats stats) {
        // Bienvenue
        welcomeLabel.setText("Bienvenue, " + stats.getUserName() + " 👋");

        // Nombre d'audits
        totalAuditsLabel.setText(String.valueOf(stats.getTotalAudits()));
        auditsThisMonthLabel.setText("+" + stats.getAuditsThisMonth() + " ce mois");

        // Nombre de projets
        projectsLabel.setText(String.valueOf(stats.getTotalProjects()));
        projectsThisWeekLabel.setText("+" + stats.getProjectsThisWeek() + " cette semaine");

        // Audits conforme/non-conforme
        conformeLabel.setText(String.valueOf(stats.getAuditsConforme()));
        nonConformeLabel.setText(String.valueOf(stats.getAuditsNonConforme()));

        // Score global
        int score = stats.getGlobalScore();
        globalScoreLabel.setText(score + "%");
        complianceStatusLabel.setText(stats.getComplianceStatus());

        // Changer la couleur et l'icône selon le score
        if (score < 50) {
            // Rouge pour score insuffisant
            complianceCard.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ef4444, #dc2626);" +
                            "-fx-background-radius: 12px;" +
                            "-fx-padding: 25px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 14, 0, 0, 5);" +
                            "-fx-cursor: hand;"
            );
        } else {
            // Bleu pour score excellent
            complianceCard.setStyle(
                    "-fx-background-color: linear-gradient(to right, #0ea5e9, #2563eb);" +
                            "-fx-background-radius: 12px;" +
                            "-fx-padding: 25px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 14, 0, 0, 5);" +
                            "-fx-cursor: hand;"
            );
        }
    }

    /**
     * Mettre à jour la progression des projets
     */
    private void updateProjectsProgress(List<DashboardService.ProjectProgress> projects) {
        projectProgressContainer.getChildren().clear();

        for (DashboardService.ProjectProgress project : projects) {
            addProjectProgress(project.getProjectName(), project.getProgress());
        }
    }

    /**
     * Mettre à jour les activités récentes
     */
    private void updateRecentActivities(List<DashboardService.RecentActivity> activities) {
        recentAuditsContainer.getChildren().clear();

        for (DashboardService.RecentActivity activity : activities) {
            addActivity(activity.getTitle(), activity.getTime(), activity.getIcon());
        }
    }

    /**
     * Ajouter une progression de projet
     */
    private void addProjectProgress(String name, double progress) {
        VBox row = new VBox(5);
        HBox labelBox = new HBox();
        Label nameLbl = new Label(name);
        Label percentLbl = new Label((int)(progress * 100) + "%");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        labelBox.getChildren().addAll(nameLbl, spacer, percentLbl);

        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.getStyleClass().add("custom-progress");

        row.getChildren().addAll(labelBox, pb);
        projectProgressContainer.getChildren().add(row);
    }

    /**
     * Ajouter une activité
     */
    private void addActivity(String title, String time, String icon) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18px;");

        VBox texts = new VBox(2);
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold;");
        Label d = new Label(time);
        d.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        texts.getChildren().addAll(t, d);
        row.getChildren().addAll(iconLbl, texts);
        recentAuditsContainer.getChildren().add(row);
    }

    // =============== CHATBOT ===============

    /**
     * Initialiser le chatbot
     */
    private void initializeChatbot() {
        // Message de bienvenue
        addChatMessage("Bonjour ! Je suis votre assistant IA. " +
                "Comment puis-je vous aider aujourd'hui ?", false);


        // Configurer l'envoi de message
        chatSendButton.setOnAction(e -> sendChatMessage());
        chatInputField.setOnAction(e -> sendChatMessage());
    }


    /**
     * Envoyer un message au chatbot
     */
    @FXML
    private void sendChatMessage() {
        String message = chatInputField.getText().trim();

        if (message.isEmpty()) return;

        // Afficher le message de l'utilisateur
        addChatMessage(message, true);

        // Vider le champ
        chatInputField.clear();

        // Afficher un indicateur de chargement
        Label loadingLabel = new Label("⏳ En cours...");
        loadingLabel.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
        chatMessagesContainer.getChildren().add(loadingLabel);

        // Envoyer le message au chatbot (asynchrone)
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return chatbotService.sendMessage(message);
            }
        };

        task.setOnSucceeded(e -> {
            String response = task.getValue();
            Platform.runLater(() -> {
                chatMessagesContainer.getChildren().remove(loadingLabel);
                addChatMessage(response, false);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                chatMessagesContainer.getChildren().remove(loadingLabel);
                addChatMessage("Désolé, une erreur s'est produite.", false);
            });
        });

        new Thread(task).start();
    }

    /**
     * Ajouter un message au chat
     */
    private void addChatMessage(String text, boolean isUser) {
        VBox messageBox = new VBox(5);
        messageBox.setPadding(new Insets(10));
        messageBox.setMaxWidth(300);

        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-padding: 10;"
        );

        if (isUser) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.setStyle(messageLabel.getStyle() +
                    "-fx-background-color: #3b82f6;" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 12 12 0 12;"
            );
            HBox.setHgrow(messageBox, Priority.ALWAYS);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageLabel.setStyle(messageLabel.getStyle() +
                    "-fx-background-color: #f1f5f9;" +
                    "-fx-text-fill: #1e293b;" +
                    "-fx-background-radius: 12 12 12 0;"
            );
        }

        messageBox.getChildren().add(messageLabel);
        chatMessagesContainer.getChildren().add(messageBox);

        // Scroll vers le bas
        if (chatMessagesContainer.getParent() instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) chatMessagesContainer.getParent();
            scrollPane.setVvalue(1.0);
        }
    }

    /**
     * Afficher une erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}