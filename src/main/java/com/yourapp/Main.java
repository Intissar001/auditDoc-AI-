package com.yourapp;

import com.yourapp.database.DatabaseConnection;
import com.yourapp.database.DatabaseSetup;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class.
 * Initializes the database and launches the JavaFX application.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database
        initializeDatabase();

        // Load settings FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/fxml/settings.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        // Load Settings CSS styles
        try {
            String settingsCss = getClass().getResource("/views/css/settings.css").toExternalForm();
            scene.getStylesheets().add(settingsCss);
        } catch (Exception e) {
            System.out.println("Settings CSS file not found: " + e.getMessage());
        }
        
        // Optional: Load general CSS styles
        try {
            String generalCss = getClass().getResource("/views/css/styles.css").toExternalForm();
            scene.getStylesheets().add(generalCss);
        } catch (Exception e) {
            System.out.println("General CSS file not found, using default styles.");
        }

        stage.setTitle("Audit Doc AI - Paramètres");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the database connection and sets up tables.
     */
    private void initializeDatabase() {
        if (DatabaseConnection.testConnection()) {
            DatabaseSetup.setupDatabase();
        } else {
            System.err.println("Warning: Database connection failed. Some features may not work.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}