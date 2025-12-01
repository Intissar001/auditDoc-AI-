package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import com.yourapp.database.DatabaseConnection;

public class Main extends Application {

    private BorderPane mainLayout;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ Database ready!");
        }

        // Create main layout with sidebar
        mainLayout = new BorderPane();

        // Create sidebar menu
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Load Audit page by default
        loadAuditPage();

        // Create scene
        Scene scene = new Scene(mainLayout, 1400, 900);

        // Load CSS if exists
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found, using default styles");
        }

        primaryStage.setTitle("AuditDoc AI - Smart Auditing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Create sidebar with navigation buttons
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-min-width: 200px;" +
                        "-fx-pref-width: 200px;"
        );

        // Logo/Title
        Label title = new Label("📋 AuditDoc AI");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 20 0;"
        );

        // Navigation buttons
        Button btnAudit = createNavButton("📊 Audit", true);
        Button btnSettings = createNavButton("⚙️ Paramètres", false);
        Button btnDashboard = createNavButton("📈 Tableau de Bord", false);
        Button btnHistory = createNavButton("📜 Historique", false);

        // Button actions
        btnAudit.setOnAction(e -> {
            loadAuditPage();
            updateActiveButton(btnAudit, btnSettings, btnDashboard, btnHistory);
        });

        btnSettings.setOnAction(e -> {
            loadSettingsPage();
            updateActiveButton(btnSettings, btnAudit, btnDashboard, btnHistory);
        });

        btnDashboard.setOnAction(e -> {
            showComingSoon("Tableau de Bord");
            updateActiveButton(btnDashboard, btnAudit, btnSettings, btnHistory);
        });

        btnHistory.setOnAction(e -> {
            showComingSoon("Historique");
            updateActiveButton(btnHistory, btnAudit, btnSettings, btnDashboard);
        });

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer
        Label footer = new Label("v1.0.0");
        footer.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

        sidebar.getChildren().addAll(
                title,
                btnAudit,
                btnSettings,
                btnDashboard,
                btnHistory,
                spacer,
                footer
        );

        return sidebar;
    }

    /**
     * Create navigation button
     */
    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        String activeStyle =
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;";

        String inactiveStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ecf0f1;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;";

        btn.setStyle(active ? activeStyle : inactiveStyle);

        // Hover effect
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#3498db")) {
                btn.setStyle(inactiveStyle + "-fx-background-color: #34495e;");
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#3498db")) {
                btn.setStyle(inactiveStyle);
            }
        });

        return btn;
    }

    /**
     * Update active button style
     */
    private void updateActiveButton(Button active, Button... others) {
        String activeStyle =
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;";

        String inactiveStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ecf0f1;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;";

        active.setStyle(activeStyle);
        for (Button btn : others) {
            btn.setStyle(inactiveStyle);
        }
    }

    /**
     * Load Audit page
     */
    private void loadAuditPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/Audit.fxml"));
            Parent auditPage = loader.load();
            mainLayout.setCenter(auditPage);
            primaryStage.setTitle("AuditDoc AI - Audit");
            System.out.println("✅ Audit page loaded");
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger la page Audit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load Settings page
     */
    private void loadSettingsPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/settings.fxml"));
            Parent settingsPage = loader.load();
            mainLayout.setCenter(settingsPage);
            primaryStage.setTitle("AuditDoc AI - Paramètres");
            System.out.println("✅ Settings page loaded");
        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de charger la page Settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show coming soon message
     */
    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("En cours de développement");
        alert.setHeaderText(feature);
        alert.setContentText("Cette fonctionnalité sera disponible prochainement.");
        alert.showAndWait();
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}