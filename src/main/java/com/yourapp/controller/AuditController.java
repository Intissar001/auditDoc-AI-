package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the AuditView.fxml
 */
public class AuditController {

    @FXML
    private VBox dropzone;

    @FXML
    private ComboBox<String> projetDropdown;

    @FXML
    private ComboBox<String> partenaireDropdown;

    // ⭐ ADDED: Reference to Historique menu item
    @FXML
    private HBox historiqueMenuItem;

    @FXML
    public void initialize() {
        // Populate dropdowns
        projetDropdown.getItems().addAll("Projet A", "Projet B", "Projet C");
        partenaireDropdown.getItems().addAll("Partenaire X", "Partenaire Y", "Partenaire Z");

        // ⭐ ADDED: Setup click handler for Historique menu
        if (historiqueMenuItem != null) {
            historiqueMenuItem.setOnMouseClicked(event -> openHistoryPage());
        } else {
            System.err.println("WARNING: historiqueMenuItem is NULL! Check fx:id in Audit.fxml");
        }
    }

    @FXML
    private void handleNewAudit() {
        System.out.println("Lancer un Nouvel Audit button clicked.");
    }

    @FXML
    private void handleBrowseFiles() {
        System.out.println("Parcourir les fichiers button clicked.");
    }

    // ⭐ ADDED: Navigation method to History page
    @FXML
    private void openHistoryPage() {
        try {
            System.out.println("=== STARTING NAVIGATION ===");
            System.out.println("Button clicked!");

            // Check if FXML file exists
            java.net.URL fxmlUrl = getClass().getResource("/views/fxml/history.fxml");
            System.out.println("FXML URL: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERROR: history.fxml NOT FOUND!");
                System.err.println("Make sure history.fxml is in: src/main/resources/views/fxml/");
                return;
            }

            System.out.println("Loading FXML...");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("FXML loaded successfully!");

            // Change scene
            Stage stage = (Stage) historiqueMenuItem.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("=== NAVIGATION COMPLETED ===");

        } catch (Exception e) {
            System.err.println("=== NAVIGATION ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}