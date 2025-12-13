package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the AuditView.fxml
 */
public class AuditController {

    @FXML
    private ComboBox<String> projetDropdown;

    @FXML
    private ComboBox<String> partenaireDropdown;

    @FXML
    private VBox dropzone;

    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox settingsMenuItem;

    @FXML
    private HBox auditMenuItem;

    private javafx.scene.Node originalCenterContent;

    @FXML
    public void initialize() {
        // Store the original center content so we can restore it later
        if (rootPane != null) {
            originalCenterContent = rootPane.getCenter();
        }
        // Populate dropdowns
        projetDropdown.getItems().addAll("Projet A", "Projet B", "Projet C");
        partenaireDropdown.getItems().addAll("Partenaire X", "Partenaire Y", "Partenaire Z");

        // Setup drag and drop handlers
        setupDragAndDrop();
    }

    /**
     * Setup drag and drop functionality for the dropzone
     */
    private void setupDragAndDrop() {
        dropzone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropzone && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropzone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                List<File> files = db.getFiles();
                handleFileUpload(files);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Handle file browsing button click
     */
    @FXML
    private void handleBrowseFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner des fichiers");

        // Set file filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tous les fichiers supportés", "*.pdf", "*.xlsx", "*.xls", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Excel", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("Word", "*.doc", "*.docx")
        );

        // Show open file dialog
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(dropzone.getScene().getWindow());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            handleFileUpload(selectedFiles);
        }
    }

    /**
     * Handle file upload logic
     */
    private void handleFileUpload(List<File> files) {
        System.out.println("Fichiers sélectionnés:");
        for (File file : files) {
            System.out.println("  - " + file.getName() + " (" + file.length() / 1024 + " KB)");

            // Verify file size (Max 10MB)
            if (file.length() > 10 * 1024 * 1024) {
                System.out.println("    ERREUR: Fichier trop volumineux (> 10MB)");
            }
        }

        // TODO: Implement actual file processing logic here
        // - Upload files to server
        // - Display uploaded files in UI
        // - Start audit process
    }

    /**
     * Handles the Settings menu item click.
     * Loads the Settings page in the center area while keeping the sidebar visible.
     */
    @FXML
    private void handleSettingsClick() {
        try {
            // Check if rootPane is available
            if (rootPane == null) {
                System.err.println("Error: rootPane is null. Cannot load Settings page.");
                return;
            }

            // Load settings.fxml
            java.net.URL settingsUrl = getClass().getResource("/views/fxml/settings.fxml");
            if (settingsUrl == null) {
                System.err.println("Error: Cannot find settings.fxml at /views/fxml/settings.fxml");
                System.err.println("Current class location: " + getClass().getResource("."));
                return;
            }

            FXMLLoader settingsLoader = new FXMLLoader(settingsUrl);
            javafx.scene.layout.AnchorPane settingsContent = settingsLoader.load();

            // Load settings CSS
            try {
                java.net.URL cssUrl = getClass().getResource("/views/css/settings.css");
                if (cssUrl != null) {
                    String settingsCss = cssUrl.toExternalForm();
                    settingsContent.getStylesheets().add(settingsCss);
                } else {
                    System.out.println("Settings CSS file not found at /views/css/settings.css");
                }
            } catch (Exception e) {
                System.out.println("Settings CSS file not found: " + e.getMessage());
            }

            // Replace the center content with settings page
            rootPane.setCenter(settingsContent);

            // Update menu item styling to show Settings is active
            if (settingsMenuItem != null) {
                settingsMenuItem.getStyleClass().remove("menu-item");
                settingsMenuItem.getStyleClass().add("menu-item-active");
            }
            
            // Update Audit menu item styling to show it's inactive
            if (auditMenuItem != null) {
                auditMenuItem.getStyleClass().remove("menu-item-active");
                auditMenuItem.getStyleClass().add("menu-item");
            }

        } catch (IOException e) {
            System.err.println("Error loading Settings page: " + e.getMessage());
            System.err.println("Path attempted: /views/fxml/settings.fxml");
            e.printStackTrace();
            
            // Show user-friendly error message
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger la page Settings");
            alert.setContentText("Erreur: " + e.getMessage() + "\nChemin: /views/fxml/settings.fxml");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Unexpected error loading Settings page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Audit menu item click.
     * Restores the original Audit page content.
     */
    @FXML
    private void handleAuditClick() {
        // Restore the original center content (Audit page)
        rootPane.setCenter(originalCenterContent);

        // Update menu item styling to show Audit is active
        auditMenuItem.getStyleClass().remove("menu-item");
        auditMenuItem.getStyleClass().add("menu-item-active");
        
        // Update Settings menu item styling to show it's inactive
        settingsMenuItem.getStyleClass().remove("menu-item-active");
        settingsMenuItem.getStyleClass().add("menu-item");
    }
}
