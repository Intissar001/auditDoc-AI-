package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controller for the AuditView.fxml
 */
public class AuditController {

    @FXML
    private VBox dropzone; // Used to handle drag & drop events (not implemented here)

    @FXML
    private ComboBox<String> projetDropdown;

    @FXML
    private ComboBox<String> partenaireDropdown;

    @FXML
    private HBox settingsMenuItem;

    @FXML
    private HBox auditMenuItem;

    @FXML
    private BorderPane rootPane; // Root BorderPane to access center area

    private javafx.scene.Node originalCenterContent; // Store original center content

    @FXML
    public void initialize() {
        // Store the original center content so we can restore it later
        originalCenterContent = rootPane.getCenter();

        // Initialization logic goes here
        // e.g., Populating ComboBoxes, setting up event handlers

        // Example: Populate dropdowns
        projetDropdown.getItems().addAll("Projet A", "Projet B", "Projet C");
        partenaireDropdown.getItems().addAll("Partenaire X", "Partenaire Y", "Partenaire Z");

        // Example: Add drag and drop handlers to the dropzone (requires more detailed implementation)
        // dropzone.setOnDragOver(...)
    }

    // Example methods for button actions
    @FXML
    private void handleNewAudit() {
        System.out.println("Lancer un Nouvel Audit button clicked.");
    }

    @FXML
    private void handleBrowseFiles() {
        System.out.println("Parcourir les fichiers button clicked.");
    }

    /**
     * Handles the Settings menu item click.
     * Loads the Settings page in the center area while keeping the sidebar visible.
     */
    @FXML
    private void handleSettingsClick() {
        try {
            // Load settings.fxml
            FXMLLoader settingsLoader = new FXMLLoader(
                    getClass().getResource("/views/fxml/settings.fxml")
            );
            javafx.scene.layout.AnchorPane settingsContent = settingsLoader.load();

            // Load settings CSS
            try {
                String settingsCss = getClass().getResource("/views/css/settings.css").toExternalForm();
                settingsContent.getStylesheets().add(settingsCss);
            } catch (Exception e) {
                System.out.println("Settings CSS file not found: " + e.getMessage());
            }

            // Replace the center content with settings page
            rootPane.setCenter(settingsContent);

            // Update menu item styling to show Settings is active
            settingsMenuItem.getStyleClass().remove("menu-item");
            settingsMenuItem.getStyleClass().add("menu-item-active");
            
            // Update Audit menu item styling to show it's inactive
            auditMenuItem.getStyleClass().remove("menu-item-active");
            auditMenuItem.getStyleClass().add("menu-item");

        } catch (IOException e) {
            System.err.println("Error loading Settings page: " + e.getMessage());
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