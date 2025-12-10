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

        // Load Audit FXML file (default startup page)
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/fxml/Audit.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        // Load general CSS styles
        try {
            String generalCss = getClass().getResource("/views/css/styles.css").toExternalForm();
            scene.getStylesheets().add(generalCss);
        } catch (Exception e) {
            System.out.println("General CSS file not found, using default styles.");
        }

        stage.setTitle("Audit Doc AI");
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