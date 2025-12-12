package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import java.io.File;
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
    public void initialize() {
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
}