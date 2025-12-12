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

    @FXML
    public void initialize() {
        // Charger logo avec taille ajustée
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
            System.err.println("TopbarController: logo introuvable -> " + ex.getMessage());
        }

        // Charger utilisateur
        User u = userService.getCurrentUser();
        if (u != null) {
            userName.setText(u.getFullName());
            userRole.setText(u.getRole());
        }

        // Notifications
        updateNotificationBadge();

        // Actions
        btnNotifications.setOnAction(e -> showNotificationPopup());
        btnNewAudit.setOnAction(e -> loadInCenter("/fxml/Audit.fxml"));
        btnLanguage.setOnAction(e -> showLanguageMenu());
        btnTheme.setOnAction(e -> toggleTheme());

        // Menu utilisateur - clickable sur tout le HBox
        userName.setOnMouseClicked(e -> showUserMenu());
        userRole.setOnMouseClicked(e -> showUserMenu());
        avatarCircle.setOnMouseClicked(e -> showUserMenu());
    }

    private void updateNotificationBadge() {
        int unread = notificationService.countUnread();
        notificationBadge.setVisible(unread > 0);
    }

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
                if (!n.isRead()) {
                    item.setStyle("-fx-font-weight: bold;");
                }
                menu.getItems().add(item);
                item.setOnAction(ev -> {
                    n.markAsRead();
                    updateNotificationBadge();
                });
            }
        }

        menu.show(btnNotifications, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    private void showLanguageMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem french = new MenuItem("Français");
        MenuItem english = new MenuItem("English");
        MenuItem arabic = new MenuItem("العربية");

        french.setOnAction(e -> changeLanguage("fr"));
        english.setOnAction(e -> changeLanguage("en"));
        arabic.setOnAction(e -> changeLanguage("ar"));

        menu.getItems().addAll(french, english, arabic);
        menu.show(btnLanguage, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    private void changeLanguage(String lang) {
        System.out.println("Changement de langue: " + lang);
        // Ajouter logique de changement de langue
    }

    private void toggleTheme() {
        System.out.println("Toggle theme (light/dark)");
        // Ajouter logique de changement de thème
        if (btnTheme.getText().equals("☀")) {
            btnTheme.setText("🌙");
        } else {
            btnTheme.setText("☀");
        }
    }

    private void showUserMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem profile = new MenuItem("Profil utilisateur");
        MenuItem help = new MenuItem("Aide");
        MenuItem logout = new MenuItem("Se déconnecter");

        profile.setOnAction(e -> loadInCenter("/fxml/ProfileView.fxml"));
        help.setOnAction(e -> loadInCenter("/fxml/HelpView.fxml"));
        logout.setOnAction(e -> {
            System.out.println("Logout clicked");
            // Ajouter logique de session / navigation vers login
        });

        menu.getItems().addAll(profile, help, logout);
        menu.show(userName, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    // ===============================
    //   CHARGE FXML DANS BorderPane
    // ===============================
    private void loadInCenter(String path) {
        try {
            Node loaded = loadFXML(path);
            if (loaded != null) setCenterOfBorderPane(loaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node loadFXML(String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            return loader.load();
        } catch (IOException | NullPointerException e) {
            System.err.println("Erreur chargement: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    private void setCenterOfBorderPane(Node node) {
        // Remonte depuis un noeud connu (btnNewAudit) jusqu'au BorderPane racine
        Node current = btnNewAudit;
        while (current != null) {
            if (current instanceof BorderPane) {
                ((BorderPane) current).setCenter(node);
                return;
            }
            current = current.getParent();
        }

        // Fallback: essayer la racine de la scène
        try {
            Node root = btnNewAudit.getScene().getRoot();
            if (root instanceof BorderPane) {
                ((BorderPane) root).setCenter(node);
            }
        } catch (Exception ignored) {}
    }
}