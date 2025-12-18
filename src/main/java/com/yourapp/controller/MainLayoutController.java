package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private TopbarController topbarController;

    @FXML
    public void initialize() {
        System.out.println("MainController initialized - contentArea: " + (contentArea != null));

        // Link sidebar controller to main controller
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        }

        // Link topbar controller to main controller
        if (topbarController != null) {
            topbarController.setMainController(this);
        }

        // Load default view (Dashboard)
        loadView("Dashboard.fxml");
    }

    public void loadView(String fxmlFile) {
        try {
            // Clear current content
            contentArea.getChildren().clear();

            // Load new FXML - adjust path based on your project structure
            String fxmlPath = "/views/fxml/" + fxmlFile;

            System.out.println("Attempting to load: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            Parent view = loader.load();

            // Add to content area
            contentArea.getChildren().add(view);

            System.out.println("Successfully loaded view: " + fxmlFile);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlFile);
            e.printStackTrace();

            // Show error message in content area
            showErrorView("Impossible de charger la page: " + fxmlFile + "\nChemin: /views/fxml/" + fxmlFile);
        } catch (Exception e) {
            System.err.println("Unexpected error loading view: " + fxmlFile);
            e.printStackTrace();
            showErrorView("Erreur inattendue: " + e.getMessage());
        }
    }

    public void updateSidebarActive(String menuName) {
        if (sidebarController != null) {
            sidebarController.setActiveMenuItemByName(menuName);
        }
    }

    private void showErrorView(String errorMessage) {
        try {
            contentArea.getChildren().clear();

            // Create a simple error label
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(errorMessage);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

            contentArea.getChildren().add(errorLabel);

        } catch (Exception e) {
            System.err.println("Error showing error view");
            e.printStackTrace();
        }
    }
}