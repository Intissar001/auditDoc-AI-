package com.yourapp.controller;

import com.yourapp.model.Notification;
import com.yourapp.model.User;
import com.yourapp.services.NotificationService;
import com.yourapp.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    private final NotificationService notificationService = new NotificationService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        System.out.println("✅ TopbarController initialized");

        // Load logo
        try {
            Image img = new Image(getClass().getResource("/views/icons/logo-audit.png").toExternalForm());
            if (topLogo != null) {
                topLogo.setImage(img);
                topLogo.setFitWidth(48);
                topLogo.setFitHeight(48);
                topLogo.setPreserveRatio(true);
            }
        } catch (Exception ex) {
            System.err.println("⚠️ Logo not found: " + ex.getMessage());
        }

        // Set app name
        if (topAppName != null) {
            topAppName.setText("AuditDoc AI");
        }

        // Load user info
        User u = userService.getCurrentUser();
        if (u != null) {
            userName.setText(u.getFullName());
            userRole.setText(u.getRole());
        } else {
            // Default user
            userName.setText("Utilisateur Test");
            userRole.setText("Administrateur");
        }

        // Update notification badge
        updateNotificationBadge();

        // Setup button actions
        if (btnNotifications != null) {
            btnNotifications.setOnAction(e -> showNotificationPopup());
        }

        if (btnNewAudit != null) {
            btnNewAudit.setOnAction(e -> loadAuditPage());
        }

        // User menu click
        if (userName != null) {
            userName.setOnMouseClicked(e -> showUserMenu());
        }
    }

    /**
     * Update notification badge visibility
     */
    private void updateNotificationBadge() {
        int unread = notificationService.countUnread();
        if (notificationBadge != null) {
            notificationBadge.setVisible(unread > 0);
        }
    }

    /**
     * Show notification popup menu
     */
    private void showNotificationPopup() {
        ContextMenu menu = new ContextMenu();
        List<Notification> all = notificationService.getAll();

        if (all.isEmpty()) {
            MenuItem noNotif = new MenuItem("Aucune notification");
            noNotif.setDisable(true);
            menu.getItems().add(noNotif);
        } else {
            for (Notification n : all) {
                MenuItem item = new MenuItem(n.getMessage());
                menu.getItems().add(item);
                item.setOnAction(ev -> {
                    n.markAsRead();
                    updateNotificationBadge();
                });
            }
        }

        menu.show(btnNotifications, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    /**
     * Show user menu
     */
    private void showUserMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem profile = new MenuItem("Profil utilisateur");
        MenuItem help = new MenuItem("Aide");
        MenuItem logout = new MenuItem("Se déconnecter");

        profile.setOnAction(e -> loadInCenter("/views/fxml/ProfileView.fxml"));
        help.setOnAction(e -> loadInCenter("/views/fxml/HelpView.fxml"));
        logout.setOnAction(e -> {
            System.out.println("🚪 Logout clicked");
            // TODO: Add logout logic
        });

        menu.getItems().addAll(profile, help, logout);
        menu.show(userName, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    /**
     * Load Audit page (when "Lancer un Nouvel Audit" is clicked)
     */
    private void loadAuditPage() {
        System.out.println("➕ New Audit button clicked!");
        loadInCenter("/views/fxml/Audit.fxml");
    }

    /**
     * Load FXML in MainLayout's center area
     */
    private void loadInCenter(String path) {
        try {
            System.out.println("🔄 Loading: " + path);

            Node loaded = loadFXML(path);
            if (loaded != null) {
                setCenterOfBorderPane(loaded);
                System.out.println("✅ View loaded!");
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load FXML file
     */
    private Node loadFXML(String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            return loader.load();
        } catch (IOException | NullPointerException e) {
            System.err.println("❌ Error loading FXML: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set content in BorderPane center
     */
    private void setCenterOfBorderPane(Node node) {
        try {
            // Navigate up from btnNewAudit to find BorderPane
            Node current = btnNewAudit;
            while (current != null) {
                if (current instanceof BorderPane) {
                    ((BorderPane) current).setCenter(node);
                    return;
                }
                current = current.getParent();
            }

            // Fallback: try from scene root
            Node root = btnNewAudit.getScene().getRoot();
            if (root instanceof BorderPane) {
                ((BorderPane) root).setCenter(node);
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting center: " + e.getMessage());
        }
    }
}