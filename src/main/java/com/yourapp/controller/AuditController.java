package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

/**
 * Controller for Audit.fxml (CONTENT ONLY)
 * Navigation is now handled by NavbarController in MainLayout
 * This controller only manages the audit form functionality
 */
public class AuditController {

    @FXML private VBox dropzone;
    @FXML private ComboBox<String> projetDropdown;
    @FXML private ComboBox<String> partenaireDropdown;

    @FXML
    public void initialize() {
        System.out.println("✅ AuditController initialized");

        // Populate projet dropdown with sample data
        projetDropdown.getItems().addAll(
                "Projet A - Infrastructure",
                "Projet B - Application Web",
                "Projet C - Mobile App",
                "Projet D - Data Migration",
                "Projet E - Security Audit"
        );

        // Populate partenaire dropdown with sample data
        partenaireDropdown.getItems().addAll(
                "Partenaire X - Tech Solutions",
                "Partenaire Y - Digital Agency",
                "Partenaire Z - Consulting Group",
                "Partenaire Alpha - Cloud Services",
                "Partenaire Beta - Integration Partners"
        );

        // Set prompt text
        projetDropdown.setPromptText("Sélectionner le Projet");
        partenaireDropdown.setPromptText("Sélectionner un Partenaire");

        // Add selection listeners (optional)
        projetDropdown.setOnAction(e -> {
            String selected = projetDropdown.getValue();
            if (selected != null) {
                System.out.println("📋 Projet sélectionné: " + selected);
            }
        });

        partenaireDropdown.setOnAction(e -> {
            String selected = partenaireDropdown.getValue();
            if (selected != null) {
                System.out.println("👥 Partenaire sélectionné: " + selected);
            }
        });
    }

    /**
     * Handle "Lancer un Nouvel Audit" button click
     * This would typically start the audit process
     */
    @FXML
    private void handleNewAudit() {
        System.out.println("=== LANCER UN NOUVEL AUDIT ===");

        String projet = projetDropdown.getValue();
        String partenaire = partenaireDropdown.getValue();

        // Validate selections
        if (projet == null || projet.isEmpty()) {
            System.err.println("⚠️ Please select a project!");
            showAlert("Erreur", "Veuillez sélectionner un projet");
            return;
        }

        if (partenaire == null || partenaire.isEmpty()) {
            System.err.println("⚠️ Please select a partner!");
            showAlert("Erreur", "Veuillez sélectionner un partenaire");
            return;
        }

        System.out.println("📋 Projet: " + projet);
        System.out.println("👥 Partenaire: " + partenaire);

        // TODO: Implement audit launch logic
        // - Validate uploaded documents
        // - Start audit process
        // - Navigate to audit results page

        System.out.println("✅ Audit process would start here");
    }

    /**
     * Handle "Parcourir les fichiers" button click
     * Opens file chooser for document upload
     */
    @FXML
    private void handleBrowseFiles() {
        System.out.println("=== BROWSE FILES CLICKED ===");

        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner les Documents d'Audit");

            // Set initial directory (optional)
            // fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            // Add file extension filters
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Tous les Documents", "*.pdf", "*.xlsx", "*.xls", "*.docx", "*.doc"),
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"),
                    new FileChooser.ExtensionFilter("Word Files", "*.docx", "*.doc"),
                    new FileChooser.ExtensionFilter("Tous les Fichiers", "*.*")
            );

            // Show open multiple files dialog
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(
                    dropzone.getScene().getWindow()
            );

            // Process selected files
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                System.out.println("✅ " + selectedFiles.size() + " fichier(s) sélectionné(s):");

                for (File file : selectedFiles) {
                    // Check file size (max 10MB)
                    long fileSizeInMB = file.length() / (1024 * 1024);

                    if (fileSizeInMB > 10) {
                        System.err.println("⚠️ File too large: " + file.getName() + " (" + fileSizeInMB + "MB)");
                        showAlert("Fichier trop volumineux",
                                file.getName() + " dépasse la limite de 10MB");
                        continue;
                    }

                    System.out.println("  📄 " + file.getName() + " (" + fileSizeInMB + "MB)");

                    // TODO: Process file
                    // - Upload to server
                    // - Extract text/data
                    // - Add to audit queue
                }

                // TODO: Update UI to show uploaded files
                // - Display file list in dropzone
                // - Show progress indicators
                // - Enable audit button

            } else {
                System.out.println("❌ No files selected");
            }

        } catch (Exception e) {
            System.err.println("❌ Error opening file chooser: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le sélecteur de fichiers");
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get selected project
     */
    public String getSelectedProject() {
        return projetDropdown.getValue();
    }

    /**
     * Get selected partner
     */
    public String getSelectedPartner() {
        return partenaireDropdown.getValue();
    }

    /**
     * Reset form
     */
    public void resetForm() {
        projetDropdown.setValue(null);
        partenaireDropdown.setValue(null);
        System.out.println("🔄 Form reset");
    }
}