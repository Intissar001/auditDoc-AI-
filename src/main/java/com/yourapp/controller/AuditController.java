package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    public void initialize() {
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
}