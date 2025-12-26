package com.yourapp.controller;

import com.yourapp.dto.NotificationDto;
import com.yourapp.model.User;
import com.yourapp.services_UI.NotificationUiService;
import com.yourapp.utils.SessionManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la barre supérieure avec système de notifications réel
 * Utilise uniquement des DTOs et communique directement avec le backend
 */
@Component
@Slf4j
public class TopbarController {

    // ============ FXML Components ============
    @FXML private ImageView topLogo;
    @FXML private Label topAppName;
    @FXML private Label userName;
    @FXML private Label userRole;
    @FXML private Circle notificationBadge;
    @FXML private Button btnNotifications;
    @FXML private StackPane notificationWrapper;
    @FXML private Button btnNewAudit;
    @FXML private Circle avatarCircle;
    @FXML private Label avatarText;
    @FXML private Button btnLanguage;
    @FXML private Button btnTheme;

    // ============ Services ============
    @Autowired
    private NotificationUiService notificationUiService;

    @Autowired
    private ApplicationContext springContext;

    // ============ Variables ============
    private MainLayoutController mainController;
    private PauseTransition notificationRefreshTimer;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        log.info("🚀 Initialisation du TopbarController");

        loadLogo();
        loadCurrentUser();
        setupNotificationSystem();
        setupButtons();
        setupUserMenu();
        setupLogoNavigation();

        log.info("✅ TopbarController initialisé avec succès");
    }

    /**
     * Charger le logo
     */
    private void loadLogo() {
        try {
            Image img = new Image(getClass().getResource("/views/icons/logo-audit.png").toExternalForm());
            if (topLogo != null) {
                topLogo.setImage(img);
                topLogo.setFitWidth(98);
                topLogo.setFitHeight(98);
                topLogo.setPreserveRatio(true);
                topLogo.setSmooth(true);
            }
        } catch (Exception ex) {
            log.error("❌ Logo not found", ex);
        }
    }

    /**
     * Charger l'utilisateur connecté depuis SessionManager
     */
    private void loadCurrentUser() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            userName.setText(currentUser.getFullName());
            userRole.setText(currentUser.getRole());

            // Avatar avec première lettre
            String firstLetter = currentUser.getFullName().substring(0, 1).toUpperCase();
            if (avatarText != null) {
                avatarText.setText(firstLetter);
            }

            updateAvatarColor(currentUser.getRole());
            log.info("✅ Topbar loaded for user: {}", currentUser.getFullName());
        } else {
            userName.setText("Guest");
            userRole.setText("Not logged in");
            if (avatarText != null) {
                avatarText.setText("G");
            }
            avatarCircle.setFill(javafx.scene.paint.Color.GRAY);
            log.warn("⚠️ No user logged in");
        }
    }

    /**
     * Mettre à jour la couleur de l'avatar selon le rôle
     */
    private void updateAvatarColor(String role) {
        if (role == null) {
            avatarCircle.setFill(javafx.scene.paint.Color.GRAY);
            return;
        }

        switch (role.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRATEUR":
                avatarCircle.setFill(javafx.scene.paint.Color.web("#dc3545"));
                break;
            case "MANAGER":
                avatarCircle.setFill(javafx.scene.paint.Color.web("#ffc107"));
                break;
            case "USER":
            default:
                avatarCircle.setFill(javafx.scene.paint.Color.web("#007bff"));
                break;
        }
    }

    /**
     * Configuration du système de notifications
     */
    private void setupNotificationSystem() {
        // Mise à jour initiale du badge
        updateNotificationBadge();

        // Démarrer le polling toutes les 10 secondes
        startNotificationPolling();

        // Action du bouton notifications
        btnNotifications.setOnAction(e -> showNotificationPopup());
    }

    /**
     * Mettre à jour le badge de notifications
     */
    private void updateNotificationBadge() {
        Platform.runLater(() -> {
            try {
                long unreadCount = notificationUiService.getUnreadCount();
                notificationBadge.setVisible(unreadCount > 0);

                if (unreadCount > 0) {
                    log.debug("🔔 {} unread notifications", unreadCount);
                }
            } catch (Exception e) {
                log.error("❌ Error updating notification badge", e);
                notificationBadge.setVisible(false);
            }
        });
    }

    /**
     * Démarrer le polling des notifications (toutes les 10 secondes)
     */
    private void startNotificationPolling() {
        notificationRefreshTimer = new PauseTransition(Duration.seconds(10));
        notificationRefreshTimer.setOnFinished(event -> {
            updateNotificationBadge();
            notificationRefreshTimer.playFromStart();
        });
        notificationRefreshTimer.play();
        log.info("🔄 Notification polling started (10s interval)");
    }

    /**
     * Afficher la popup de notifications avec données réelles
     */
    private void showNotificationPopup() {
        log.info("📬 Opening notifications popup");

        try {
            // Récupérer les notifications depuis le backend via le service UI
            List<NotificationDto> notifications = notificationUiService.getAllNotifications();
            log.info("📊 Loaded {} notifications", notifications.size());

            // Créer le dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Notifications");
            dialog.initOwner(btnNotifications.getScene().getWindow());

            // Bouton fermer
            ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            // Container principal
            VBox mainContainer = new VBox(0);
            mainContainer.setPrefWidth(420);
            mainContainer.setMaxHeight(550);
            mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

            // ========== HEADER ==========
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(20, 20, 15, 20));
            header.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 1 0;");

            Label titleLabel = new Label("Notifications");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #111827;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button markAllReadBtn = new Button("✓ Tout marquer lu");
            markAllReadBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #3b82f6;
                -fx-font-size: 13px;
                -fx-cursor: hand;
                -fx-font-weight: 600;
            """);
            markAllReadBtn.setOnMouseEntered(e ->
                    markAllReadBtn.setStyle(markAllReadBtn.getStyle() + "-fx-underline: true;"));
            markAllReadBtn.setOnMouseExited(e ->
                    markAllReadBtn.setStyle(markAllReadBtn.getStyle().replace("-fx-underline: true;", "")));
            markAllReadBtn.setOnAction(e -> {
                notificationUiService.markAllAsRead();
                dialog.close();
                updateNotificationBadge();
                log.info("✅ All notifications marked as read");
            });

            header.getChildren().addAll(titleLabel, spacer, markAllReadBtn);

            // ========== LISTE DES NOTIFICATIONS ==========
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox notificationsList = new VBox(0);

            if (notifications.isEmpty()) {
                // État vide
                VBox emptyState = new VBox(15);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));

                Label emptyIcon = new Label("🔔");
                emptyIcon.setStyle("-fx-font-size: 56px;");

                Label emptyText = new Label("Aucune notification");
                emptyText.setStyle("-fx-font-size: 15px; -fx-text-fill: #6b7280; -fx-font-weight: 600;");

                Label emptySubtext = new Label("Vous serez notifié ici lors de nouveaux événements");
                emptySubtext.setStyle("-fx-font-size: 13px; -fx-text-fill: #9ca3af;");
                emptySubtext.setWrapText(true);
                emptySubtext.setMaxWidth(280);

                emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
                notificationsList.getChildren().add(emptyState);
            } else {
                // Afficher les notifications
                for (NotificationDto notification : notifications) {
                    notificationsList.getChildren().add(createNotificationItem(notification, dialog));
                }
            }

            scrollPane.setContent(notificationsList);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            mainContainer.getChildren().addAll(header, scrollPane);
            dialog.getDialogPane().setContent(mainContainer);

            // Style du dialog
            dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");

            dialog.showAndWait();

            // Rafraîchir le badge après fermeture
            updateNotificationBadge();

        } catch (Exception e) {
            log.error("❌ Error showing notifications popup", e);
            showErrorAlert("Erreur", "Impossible de charger les notifications");
        }
    }

    /**
     * Créer un élément de notification avec design moderne
     */
    private VBox createNotificationItem(NotificationDto notification, Dialog<Void> parentDialog) {
        VBox item = new VBox(10);
        item.setPadding(new Insets(16, 20, 16, 20));
        item.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-cursor: hand;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 0 0 1 0;
        """, notification.isRead() ? "#ffffff" : "#eff6ff"));

        // ========== HEADER: Icône + Message ==========
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.TOP_LEFT);

        // Icône
        Label icon = new Label(notification.getIcon());
        icon.setStyle("-fx-font-size: 20px;");
        icon.setMinWidth(24);

        // Message
        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setStyle(String.format("""
            -fx-font-size: 14px;
            -fx-text-fill: #111827;
            -fx-font-weight: %s;
            -fx-wrap-text: true;
        """, notification.isRead() ? "normal" : "600"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(310);
        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        headerBox.getChildren().addAll(icon, messageLabel);

        // ========== FOOTER: Temps + Actions ==========
        HBox footerBox = new HBox(10);
        footerBox.setAlignment(Pos.CENTER_LEFT);

        Label timeLabel = new Label(notification.getRelativeTime());
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        // Bouton marquer comme lu (si non lu)
        if (!notification.isRead()) {
            Button markReadBtn = new Button("Marquer lu");
            markReadBtn.setStyle("""
                -fx-background-color: #3b82f6;
                -fx-text-fill: white;
                -fx-font-size: 11px;
                -fx-cursor: hand;
                -fx-padding: 4 10;
                -fx-background-radius: 4;
                -fx-font-weight: 600;
            """);
            markReadBtn.setOnAction(e -> {
                notificationUiService.markAsRead(notification.getId());
                parentDialog.close();
                showNotificationPopup(); // Recharger
                log.info("✅ Notification {} marked as read", notification.getId());
            });
            footerBox.getChildren().addAll(timeLabel, footerSpacer, markReadBtn);
        } else {
            footerBox.getChildren().addAll(timeLabel, footerSpacer);
        }

        item.getChildren().addAll(headerBox, footerBox);

        // Effet hover
        item.setOnMouseEntered(e -> {
            if (notification.isRead()) {
                item.setStyle(item.getStyle() + "-fx-background-color: #f9fafb;");
            }
        });
        item.setOnMouseExited(e -> {
            item.setStyle(item.getStyle().replace("-fx-background-color: #f9fafb;", ""));
        });

        // Clic: marquer comme lu si non lu
        item.setOnMouseClicked(e -> {
            if (!notification.isRead()) {
                notificationUiService.markAsRead(notification.getId());
                parentDialog.close();
                showNotificationPopup();
            }
        });

        return item;
    }

    /**
     * Configuration des boutons
     */
    private void setupButtons() {
        Platform.runLater(() -> {
            if (btnNotifications != null) {
                btnNotifications.setMnemonicParsing(false);
                btnNotifications.setEllipsisString("");
            }
            if (btnLanguage != null) {
                btnLanguage.setMnemonicParsing(false);
                btnLanguage.setEllipsisString("");
            }
            if (btnTheme != null) {
                btnTheme.setMnemonicParsing(false);
                btnTheme.setEllipsisString("");
            }
            if (btnNewAudit != null) {
                btnNewAudit.setMnemonicParsing(false);
            }
        });

        btnNewAudit.setOnAction(e -> loadAuditPage());
        btnLanguage.setOnAction(e -> showLanguageMenu());
        btnTheme.setOnAction(e -> toggleTheme());
    }

    /**
     * Configuration du menu utilisateur
     */
    private void setupUserMenu() {
        userName.setOnMouseClicked(e -> showUserMenu());
        userRole.setOnMouseClicked(e -> showUserMenu());
        avatarCircle.setOnMouseClicked(e -> showUserMenu());
        if (avatarText != null) {
            avatarText.setOnMouseClicked(e -> showUserMenu());
        }
    }

    /**
     * Configuration de la navigation via le logo
     */
    private void setupLogoNavigation() {
        topLogo.setOnMouseClicked(e -> loadDashboard());
        topAppName.setOnMouseClicked(e -> loadDashboard());
        topLogo.setStyle("-fx-cursor: hand;");
        topAppName.setStyle("-fx-cursor: hand;");
    }

    /**
     * Afficher le menu utilisateur
     */
    private void showUserMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem profile = new MenuItem("👤 Mon Profil");
        MenuItem help = new MenuItem("❓ Aide");
        MenuItem logout = new MenuItem("🚪 Se déconnecter");

        profile.setOnAction(e -> log.info("Profile clicked"));
        help.setOnAction(e -> log.info("Help clicked"));
        logout.setOnAction(e -> handleLogout());

        menu.getItems().addAll(profile, help, new SeparatorMenuItem(), logout);
        menu.show(userName, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    /**
     * Gérer la déconnexion
     */
    private void handleLogout() {
        // Arrêter le polling
        if (notificationRefreshTimer != null) {
            notificationRefreshTimer.stop();
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Déconnexion");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir vous déconnecter?");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionManager.getInstance().logout();
            log.info("✅ User logged out successfully");
            redirectToLogin();
        }
    }

    /**
     * Rediriger vers la page de connexion
     */
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) userName.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setMaximized(true);
            stage.setTitle("AuditDoc AI - Connexion");

            log.info("✅ Redirected to login page");
        } catch (Exception e) {
            log.error("❌ Error redirecting to login", e);
            showErrorAlert("Erreur", "Impossible de charger la page de connexion");
        }
    }

    /**
     * Charger la page d'audit
     */
    private void loadAuditPage() {
        if (mainController != null) {
            mainController.loadView("Audit.fxml");
            mainController.updateSidebarActive("audit");
        }
    }

    /**
     * Charger le dashboard
     */
    private void loadDashboard() {
        if (mainController != null) {
            mainController.loadView("Dashboard.fxml");
            mainController.updateSidebarActive("dashboard");
        }
    }

    /**
     * Afficher le menu langue
     */
    private void showLanguageMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem french = new MenuItem("🇫🇷 Français");
        MenuItem english = new MenuItem("🇬🇧 English");
        MenuItem arabic = new MenuItem("🇲🇦 العربية");

        french.setOnAction(e -> log.info("Language: Français"));
        english.setOnAction(e -> log.info("Language: English"));
        arabic.setOnAction(e -> log.info("Language: العربية"));

        menu.getItems().addAll(french, english, arabic);
        menu.show(btnLanguage, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    /**
     * Basculer le thème
     */
    private void toggleTheme() {
        if (btnTheme.getText().equals("☀")) {
            btnTheme.setText("🌙");
            log.info("Theme: Dark mode");
        } else {
            btnTheme.setText("☀");
            log.info("Theme: Light mode");
        }
    }

    /**
     * Afficher une alerte d'erreur
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Setter pour le contrôleur principal
     */
    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    /**
     * Nettoyer les ressources
     */
    public void cleanup() {
        if (notificationRefreshTimer != null) {
            notificationRefreshTimer.stop();
        }
        log.info("🧹 TopbarController cleaned up");
    }
}