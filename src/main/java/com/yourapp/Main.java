package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database
        initializeDatabase();

        // Load Audit.fxml directly (it has its own sidebar)
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/views/fxml/Audit.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        // Load CSS
        try {
            String generalCss = getClass().getResource("/views/css/styles.css").toExternalForm();
            scene.getStylesheets().add(generalCss);
        } catch (Exception e) {
            System.out.println("General CSS file not found, using default styles.");
        }
        
        try {
            String settingsCss = getClass().getResource("/views/css/settings.css").toExternalForm();
            scene.getStylesheets().add(settingsCss);
        } catch (Exception e) {
            System.out.println("Settings CSS file not found: " + e.getMessage());
        }

        stage.setTitle("Audit Doc AI");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the database connection and sets up tables.
     */
    private void initializeDatabase() {
        if (com.yourapp.database.DatabaseConnection.testConnection()) {
            com.yourapp.database.DatabaseSetup.setupDatabase();
        } else {
            System.err.println("Warning: Database connection failed. Some features may not work.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
