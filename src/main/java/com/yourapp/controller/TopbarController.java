package com.yourapp.controller;

import com.yourapp.model.Notification;
import com.yourapp.model.User;
import com.yourapp.services.NotificationService;
import com.yourapp.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.List;

public class TopbarController {

    @FXML private ImageView topLogo;
    @FXML private Label topAppName;
    @FXML private TextField searchField;

    @FXML private Label userName;
    @FXML private Label userRole;
    @FXML private Circle notificationBadge;
    @FXML private Button btnNotifications;
    @FXML private StackPane notificationWrapper;
    @FXML private Button btnNewAudit;
    @FXML private Circle avatarCircle;
    @FXML private Button btnLanguage;
    @FXML private Button btnTheme;

    private final NotificationService notificationService = new NotificationService();
    private final UserService userService = new UserService();
  //khadija's work

    // ================== INITIALIZE ==================
    @FXML
    public void initialize() {
        System.out.println("✅ TopbarController initialized");

        loadLogo();
        loadUserInfo();
        updateNotificationBadge();
        initActions();
    }

    // ================== UI SETUP ==================
    private void loadLogo() {
        try {
            Image img = new Image(
                    getClass().getResource("/views/icons/logo-audit.png").toExternalForm()
            );
            if (topLogo != null) {
                topLogo.setImage(img);
                topLogo.setFitWidth(98);
                topLogo.setFitHeight(98);
                topLogo.setPreserveRatio(true);
                topLogo.setSmooth(true);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Logo not found");
        }

        if (topAppName != null) {
            topAppName.setText("AuditDoc AI");
        }
    }

    private void loadUserInfo() {
        User u = userService.getCurrentUser();
        if (u != null) {
            userName.setText(u.getFullName());
            userRole.setText(u.getRole());
        } else {
            userName.setText("Utilisateur Test");
            userRole.setText("Administrateur");
        }
    }

    private void initActions() {
        if (btnNotifications != null) {
            btnNotifications.setOnAction(e -> showNotificationPopup());
         
        }
//khadija's work
        if (btnNewAudit != null) {
            btnNewAudit.setOnAction(e -> loadInCenter("/views/fxml/Audit.fxml"));
        }

        if (btnLanguage != null) {
            btnLanguage.setOnAction(e -> showLanguageMenu());
        }

        if (btnTheme != null) {
            btnTheme.setOnAction(e -> toggleTheme());
        }

        // menu utilisateur
        userName.setOnMouseClicked(e -> showUserMenu());
        userRole.setOnMouseClicked(e -> showUserMenu());
        avatarCircle.setOnMouseClicked(e -> showUserMenu());

        // Logo et nom de l'app - clickable pour retourner au dashboard
        topLogo.setOnMouseClicked(e -> loadDashboard());
        topAppName.setOnMouseClicked(e -> loadDashboard());
        topLogo.setStyle("-fx-cursor: hand;");
        topAppName.setStyle("-fx-cursor: hand;");
    }

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    private void loadAuditPage() {
        if (mainController != null) {
            mainController.loadView("Audit.fxml");
            mainController.updateSidebarActive("audit");
        } else {
            System.err.println("MainController is not set in TopbarController");
        }
    }

    private void loadDashboard() {
        if (mainController != null) {
            mainController.loadView("Dashboard.fxml");
            mainController.updateSidebarActive("dashboard");
        } else {
            System.err.println("MainController is not set in TopbarController");
        }
    }

    // ================== NOTIFICATIONS ==================
    private void updateNotificationBadge() {
        int unread = notificationService.countUnread();
        if (notificationBadge != null) {
            notificationBadge.setVisible(unread > 0);
        }
    }

    private void showNotificationPopup() {
        ContextMenu menu = new ContextMenu();
        List<Notification> notifications = notificationService.getAll();

        if (notifications.isEmpty()) {
            MenuItem empty = new MenuItem("Aucune notification");
            empty.setDisable(true);
            menu.getItems().add(empty);
        } else {
            for (Notification n : notifications) {
                MenuItem item = new MenuItem(n.getMessage());
                if (!n.isRead()) {
                    item.setStyle("-fx-font-weight: bold;");
                }
                item.setOnAction(e -> {
                    n.markAsRead();
                    updateNotificationBadge();
                });
                menu.getItems().add(item);
            }
        }

        menu.show(btnNotifications, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    // ================== MENUS ==================
    private void showLanguageMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem fr = new MenuItem("Français");
        MenuItem en = new MenuItem("English");
        MenuItem ar = new MenuItem("العربية");

        fr.setOnAction(e -> changeLanguage("fr"));
        en.setOnAction(e -> changeLanguage("en"));
        ar.setOnAction(e -> changeLanguage("ar"));

        menu.getItems().addAll(fr, en, ar);
        menu.show(btnLanguage, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    private void showUserMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem profile = new MenuItem("Profil utilisateur");
        MenuItem help = new MenuItem("Aide");
        MenuItem logout = new MenuItem("Se déconnecter");

        profile.setOnAction(e -> loadInCenter("/views/fxml/ProfileView.fxml"));
        help.setOnAction(e -> loadInCenter("/views/fxml/HelpView.fxml"));
        logout.setOnAction(e -> System.out.println("🚪 Logout clicked"));

        menu.getItems().addAll(profile, help, logout);
        menu.show(userName, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    // ================== ACTIONS ==================
    private void changeLanguage(String lang) {
        System.out.println("🌍 Changement de langue : " + lang);
    }

    private void toggleTheme() {
        System.out.println("🎨 Toggle theme");
        if ("☀".equals(btnTheme.getText())) {
            btnTheme.setText("🌙");
        } else {
            btnTheme.setText("☀");
        }
    }

    // ================== NAVIGATION ==================
    private void loadInCenter(String path) {
        try {
            Node view = loadFXML(path);
            if (view != null) {
                setCenterOfBorderPane(view);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading view: " + path);
            e.printStackTrace();
        }
    }

    private Node loadFXML(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            return loader.load();
        } catch (IOException | NullPointerException e) {
            System.err.println("❌ Error loading FXML: " + path);
            return null;
        }
    }

    /**
     * Set content in BorderPane center (VERSION FIXED)
     */
    private void setCenterOfBorderPane(Node node) {
        try {
            Node current = btnNewAudit;

            while (current != null) {
                if (current instanceof BorderPane) {
                    ((BorderPane) current).setCenter(node);
                    return;
                }
                current = current.getParent();
            }

            // fallback
            Node root = btnNewAudit.getScene().getRoot();
            if (root instanceof BorderPane) {
                ((BorderPane) root).setCenter(node);
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting center");
            e.printStackTrace();
        }
    }
}
