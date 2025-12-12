package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main Application Class - Entry Point for AuditDoc AI
 *
 * This is the JavaFX application entry point that:
 * 1. Loads the MainLayout.fxml (which contains Sidebar, Topbar, ContentArea)
 * 2. Sets up the main window with proper dimensions
 * 3. Configures the application icon and title
 * 4. Shows the primary stage
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    🚀 Starting AuditDoc AI             ║");
        System.out.println("╚════════════════════════════════════════╝");

        try {
            // ============================================
            // STEP 1: Load MainLayout.fxml
            // ============================================
            String fxmlPath = "/views/fxml/MainLayout.fxml";
            System.out.println("📂 Loading FXML: " + fxmlPath);

            // Check if file exists
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("❌ CRITICAL ERROR: MainLayout.fxml NOT FOUND!");
                System.err.println("📍 Expected location: src/main/resources/views/fxml/MainLayout.fxml");
                throw new RuntimeException("MainLayout.fxml not found at: " + fxmlPath);
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            System.out.println("✅ MainLayout.fxml loaded successfully!");

            // ============================================
            // STEP 2: Create Scene with proper dimensions
            // ============================================
            Scene scene = new Scene(root, 1200, 700);
            System.out.println("✅ Scene created (1200x700)");

            // ============================================
            // STEP 3: Configure Primary Stage
            // ============================================
            primaryStage.setTitle("AuditDoc AI - Intelligent Audit System");
            primaryStage.setScene(scene);

            // Set minimum window size
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);

            System.out.println("✅ Stage configured");

            // ============================================
            // STEP 4: Set Application Icon (Optional)
            // ============================================
            try {
                // Try to load application icon
                Image icon = new Image(getClass().getResourceAsStream("/views/icons/logo-audit.png"));
                primaryStage.getIcons().add(icon);
                System.out.println("✅ Application icon loaded");
            } catch (Exception e) {
                System.out.println("⚠️ Application icon not found (optional)");
            }

            // ============================================
            // STEP 5: Show Stage
            // ============================================
            primaryStage.show();

            System.out.println("✅ Application started successfully!");
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    ✨ AuditDoc AI is now running!     ║");
            System.out.println("╚════════════════════════════════════════╝\n");

            // Log window dimensions
            System.out.println("📐 Window Size: " + scene.getWidth() + "x" + scene.getHeight());
            System.out.println("📐 Minimum Size: " + primaryStage.getMinWidth() + "x" + primaryStage.getMinHeight());

        } catch (Exception e) {
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║    ❌ CRITICAL ERROR                   ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Failed to start application!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\n📋 Stack Trace:");
            e.printStackTrace();

            // Show error dialog to user
            showErrorDialog(e);

            throw e;
        }
    }

    /**
     * Show error dialog to user when application fails to start
     */
    private void showErrorDialog(Exception e) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Erreur de Démarrage");
            alert.setHeaderText("Impossible de démarrer AuditDoc AI");
            alert.setContentText(
                    "Une erreur critique s'est produite lors du démarrage de l'application.\n\n" +
                            "Erreur: " + e.getMessage() + "\n\n" +
                            "Veuillez vérifier:\n" +
                            "1. Que tous les fichiers FXML sont présents\n" +
                            "2. Que les contrôleurs sont correctement configurés\n" +
                            "3. Que les ressources sont dans le bon dossier"
            );
            alert.showAndWait();
        } catch (Exception dialogError) {
            // If alert fails, just print to console
            System.err.println("⚠️ Could not show error dialog");
        }
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        System.out.println("\n" +
                "╔════════════════════════════════════════════════════════╗\n" +
                "║                                                        ║\n" +
                "║              🔍 AUDITDOC AI                            ║\n" +
                "║         Intelligent Audit Management System           ║\n" +
                "║                                                        ║\n" +
                "║              Version: 1.0.0                            ║\n" +
                "║              JavaFX Application                        ║\n" +
                "║                                                        ║\n" +
                "╚════════════════════════════════════════════════════════╝\n"
        );

        // Launch JavaFX application
        launch(args);
    }

    /**
     * Called when application is stopping
     */
    @Override
    public void stop() throws Exception {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    🛑 Shutting down AuditDoc AI        ║");
        System.out.println("╚════════════════════════════════════════╝");

        // Cleanup resources here if needed
        // - Close database connections
        // - Save user preferences
        // - Stop background tasks

        super.stop();

        System.out.println("✅ Application closed successfully");
        System.out.println("👋 Goodbye!\n");
    }
}