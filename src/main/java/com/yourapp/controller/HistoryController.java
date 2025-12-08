package com.yourapp.controller;

import com.yourapp.model.AuditReport;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> partnerComboBox;
    @FXML private ComboBox<String> sortByComboBox;  // NEW: Trier par
    @FXML private ComboBox<String> sortComboBox;     // Ordre (Ascendant/Descendant)
    @FXML private Label auditCountLabel;

    @FXML private TableView<AuditReport> auditTable;
    @FXML private TableColumn<AuditReport, String> dateColumn;
    @FXML private TableColumn<AuditReport, String> projectColumn;
    @FXML private TableColumn<AuditReport, String> scoreColumn;
    @FXML private TableColumn<AuditReport, String> statusColumn;
    @FXML private TableColumn<AuditReport, String> problemsColumn;
    @FXML private TableColumn<AuditReport, Void> reportsColumn;

    // Sidebar navigation
    @FXML private HBox auditMenuItem;

    // Database service
    private com.yourapp.service.AuditService auditService;

    private ObservableList<AuditReport> auditList;
    private ObservableList<AuditReport> filteredList;

    @FXML
    public void initialize() {
        // Initialize service
        try {
            auditService = new com.yourapp.service.AuditService();
            System.out.println("✅ AuditService initialized");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize AuditService: " + e.getMessage());
            e.printStackTrace();
        }
        // Initialize empty lists
        auditList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        // Setup table columns
        setupTableColumns();

        // Load data into table (will be empty initially)
        auditTable.setItems(filteredList);

        // Setup filters
        setupFilters();

        // Update count (will show "0 audits")
        updateAuditCount();

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAudits();
        });

        // Set placeholder message for empty table
        Label placeholderLabel = new Label("Aucun audit disponible.\nLancez un nouvel audit pour commencer.");
        placeholderLabel.setStyle("-fx-text-fill: #667085; -fx-font-size: 14px; -fx-text-alignment: center;");
        auditTable.setPlaceholder(placeholderLabel);

        // Setup navigation - Go back to Audit page
        if (auditMenuItem != null) {
            auditMenuItem.setOnMouseClicked(event -> goBackToAudit());
        } else {
            System.err.println("WARNING: auditMenuItem is NULL! Check fx:id in history.fxml");
        }

        // Load audits from database (if any exist)
        loadAuditsFromDatabase();
    }

    private void setupTableColumns() {
        // Date Column
        dateColumn.setCellValueFactory(cellData -> {
            ZonedDateTime date = cellData.getValue().getCreatedAt();
            if (date != null) {
                return new SimpleStringProperty(date.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            return new SimpleStringProperty("");
        });

        // Project Column
        projectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProjectName() != null ?
                        cellData.getValue().getProjectName() : "N/A"));

        // Score Column with colors
        scoreColumn.setCellValueFactory(cellData -> {
            Integer score = cellData.getValue().getScore();
            return new SimpleStringProperty(score != null ? score + "%" : "N/A");
        });

        scoreColumn.setCellFactory(column -> new TableCell<AuditReport, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.equals("N/A")) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int score = Integer.parseInt(item.replace("%", ""));

                    if (score >= 90) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else if (score >= 80) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else if (score >= 70) {
                        setStyle("-fx-text-fill: #84cc16; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Status Column with badges
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplianceStatus() != null ?
                        cellData.getValue().getComplianceStatus() : "N/A"));

        statusColumn.setCellFactory(column -> new TableCell<AuditReport, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.equals("N/A")) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.setFont(Font.font("System", FontWeight.BOLD, 11));
                    badge.setPadding(new javafx.geometry.Insets(5, 12, 5, 12));
                    badge.setAlignment(Pos.CENTER);
                    badge.setStyle("-fx-background-radius: 5;");

                    if (item.equals("Conforme")) {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;");
                    } else if (item.equals("Non-Conforme")) {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;");
                    } else {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #e5e7eb; -fx-text-fill: #374151;");
                    }

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Problems Column
        problemsColumn.setCellValueFactory(cellData -> {
            Integer problems = cellData.getValue().getProblemsCount();
            if (problems != null && problems > 0) {
                return new SimpleStringProperty(problems + " problème" + (problems > 1 ? "s" : ""));
            }
            return new SimpleStringProperty("Aucun");
        });

        problemsColumn.setCellFactory(column -> new TableCell<AuditReport, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Aucun")) {
                        setStyle("-fx-text-fill: #6b7280;");
                    } else {
                        setStyle("-fx-text-fill: #374151; -fx-font-weight: normal;");
                    }
                }
            }
        });

        // Reports Column with buttons
        reportsColumn.setCellFactory(column -> new TableCell<AuditReport, Void>() {
            private final Button viewButton = new Button("👁 Voir");
            private final Button pdfButton = new Button("📥 PDF");
            private final HBox buttonsBox = new HBox(10, viewButton, pdfButton);

            {
                viewButton.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: #3b82f6; -fx-cursor: hand; " +
                        "-fx-font-size: 12px; -fx-padding: 5 10 5 10;");

                pdfButton.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: #3b82f6; -fx-cursor: hand; " +
                        "-fx-font-size: 12px; -fx-padding: 5 10 5 10;");

                viewButton.setOnMouseEntered(e ->
                        viewButton.setStyle(viewButton.getStyle() + "-fx-underline: true;"));
                viewButton.setOnMouseExited(e ->
                        viewButton.setStyle(viewButton.getStyle().replace("-fx-underline: true;", "")));

                pdfButton.setOnMouseEntered(e ->
                        pdfButton.setStyle(pdfButton.getStyle() + "-fx-underline: true;"));
                pdfButton.setOnMouseExited(e ->
                        pdfButton.setStyle(pdfButton.getStyle().replace("-fx-underline: true;", "")));

                viewButton.setOnAction(e -> handleViewReport(getTableRow().getItem()));
                pdfButton.setOnAction(e -> handleDownloadPDF(getTableRow().getItem()));

                buttonsBox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void setupFilters() {
        // Partner filter - Load from database (DYNAMIC)
        loadPartnerFilter();

        // Sort By filter
        sortByComboBox.getItems().addAll(
                "Date",
                "Nom du projet",
                "Statut"
        );
        sortByComboBox.setValue("Date");

        // Sort Order filter
        sortComboBox.getItems().addAll("Ascendant", "Descendant");
        sortComboBox.setValue("Descendant");

        // Add listeners
        partnerComboBox.setOnAction(e -> filterAudits());
        sortByComboBox.setOnAction(e -> filterAudits());
        sortComboBox.setOnAction(e -> filterAudits());
    }

    /**
     * Load partner filter dynamically from database
     */
    private void loadPartnerFilter() {
        try {
            // Clear existing items
            partnerComboBox.getItems().clear();

            // Add default option
            partnerComboBox.getItems().add("Tous les partenaires");

            // Load unique PARTNER names from database (not project names!)
            List<String> partnerNames = auditService.getAllPartnerNames();

            // Add to dropdown
            partnerComboBox.getItems().addAll(partnerNames);

            // Set default value
            partnerComboBox.setValue("Tous les partenaires");

            System.out.println("✅ Loaded " + partnerNames.size() + " partners in filter");

        } catch (Exception e) {
            System.err.println("❌ Error loading partner filter: " + e.getMessage());
            // Fallback: just show "Tous les partenaires"
            partnerComboBox.getItems().clear();
            partnerComboBox.getItems().add("Tous les partenaires");
            partnerComboBox.setValue("Tous les partenaires");
        }
    }

    private void filterAudits() {
        filteredList.clear();

        String searchText = searchField.getText().toLowerCase();
        String selectedPartner = partnerComboBox.getValue();

        // Filter by search and partner
        for (AuditReport audit : auditList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    (audit.getProjectName() != null && audit.getProjectName().toLowerCase().contains(searchText)) ||
                    (audit.getTitle() != null && audit.getTitle().toLowerCase().contains(searchText));

            boolean matchesPartner = selectedPartner == null ||
                    selectedPartner.equals("Tous les partenaires") ||
                    (audit.getPartnerName() != null && audit.getPartnerName().equals(selectedPartner));

            if (matchesSearch && matchesPartner) {
                filteredList.add(audit);
            }
        }

        // Sort based on sortByComboBox and sortComboBox
        String sortBy = sortByComboBox.getValue();
        String sortOrder = sortComboBox.getValue();
        boolean ascending = sortOrder != null && sortOrder.equals("Ascendant");

        if (sortBy != null) {
            switch (sortBy) {
                case "Date":
                    // Trier par date
                    filteredList.sort((a1, a2) -> {
                        if (a1.getCreatedAt() != null && a2.getCreatedAt() != null) {
                            return ascending ?
                                    a1.getCreatedAt().compareTo(a2.getCreatedAt()) :
                                    a2.getCreatedAt().compareTo(a1.getCreatedAt());
                        }
                        return 0;
                    });
                    break;

                case "Nom du projet":
                    // Trier par nom de projet (A→Z ou Z→A)
                    filteredList.sort((a1, a2) -> {
                        String name1 = a1.getProjectName() != null ? a1.getProjectName() : "";
                        String name2 = a2.getProjectName() != null ? a2.getProjectName() : "";
                        return ascending ?
                                name1.compareToIgnoreCase(name2) :
                                name2.compareToIgnoreCase(name1);
                    });
                    break;

                case "Statut":
                    // Trier par statut (Non-Conforme d'abord si Descendant)
                    filteredList.sort((a1, a2) -> {
                        String status1 = a1.getComplianceStatus() != null ? a1.getComplianceStatus() : "";
                        String status2 = a2.getComplianceStatus() != null ? a2.getComplianceStatus() : "";

                        // "Non-Conforme" vient avant "Conforme" en ordre descendant
                        int comparison = status1.compareToIgnoreCase(status2);
                        // Inverse car "Non-Conforme" > "Conforme" alphabétiquement mais on veut l'inverse
                        return ascending ? comparison : -comparison;
                    });
                    break;
            }
        }

        updateAuditCount();
    }

    private void updateAuditCount() {
        auditCountLabel.setText(filteredList.size() + " audits");
    }

    private void handleViewReport(AuditReport audit) {
        if (audit != null) {
            System.out.println("Viewing report for: " + audit.getProjectName());
            System.out.println("Audit ID: " + audit.getId());
            // TODO: Open report details view
        }
    }

    private void handleDownloadPDF(AuditReport audit) {
        if (audit != null) {
            System.out.println("Downloading PDF for: " + audit.getProjectName());
            System.out.println("Audit ID: " + audit.getId());
            // TODO: Generate and download PDF
        }
    }

    @FXML
    private void handleReduce() {
        System.out.println("Reduce button clicked");
        // TODO: Collapse sidebar functionality
    }

    /**
     * Navigate back to Audit page
     */
    private void goBackToAudit() {
        try {
            System.out.println("Navigating back to Audit page...");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/Audit.fxml")
            );
            Parent root = loader.load();

            // Get current stage and change scene
            Stage stage = (Stage) auditMenuItem.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("✅ Returned to Audit page successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error navigating to Audit page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load audits from database
     */
    public void loadAuditsFromDatabase() {
        try {
            System.out.println("📊 Loading audits from database...");

            // Fetch audits from database
            List<AuditReport> audits = auditService.getAllAudits();

            // Clear existing data
            auditList.clear();
            filteredList.clear();

            // Add to lists
            auditList.addAll(audits);
            filteredList.addAll(audits);

            // Update count
            updateAuditCount();

            System.out.println("✅ Successfully loaded " + audits.size() + " audits");

        } catch (Exception e) {
            System.err.println("❌ Error loading audits from database: " + e.getMessage());
            e.printStackTrace();

            // Show error alert
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de chargement");
            alert.setContentText("Impossible de charger les audits depuis la base de données.");
            alert.showAndWait();
        }
    }

    /**
     * Refresh the audit list
     * Call this when returning to History page after creating new audit
     */
    public void refreshAudits() {
        auditList.clear();
        filteredList.clear();
        loadAuditsFromDatabase();
        updateAuditCount();
    }
}