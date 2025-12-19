package com.yourapp.controller;

import com.yourapp.model.Audit;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.AuditIssue;
import com.yourapp.model.AuditReport;
import com.yourapp.DAO.AuditReportRepository;
import com.yourapp.services.HistoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> partnerComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label auditCountLabel;

    @FXML private TableView<AuditReport> auditTable;
    @FXML private TableColumn<AuditReport, String> dateColumn;
    @FXML private TableColumn<AuditReport, String> projectColumn;
    @FXML private TableColumn<AuditReport, String> scoreColumn;
    @FXML private TableColumn<AuditReport, String> statusColumn;
    @FXML private TableColumn<AuditReport, String> problemsColumn;
    @FXML private TableColumn<AuditReport, Void> reportsColumn;

    private ObservableList<AuditReport> auditList;
    private ObservableList<AuditReport> filteredList;

    private HistoryService historyService;
    private AuditReportRepository auditReportRepository;

    @FXML
    public void initialize() {
        System.out.println("✅ HistoryController initialized - Prêt pour database");

        // Initialize empty lists
        auditList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        // Setup table columns
        setupTableColumns();

        // Load data into table
        auditTable.setItems(filteredList);

        // Setup filters
        setupFilters();

        // Update count
        updateAuditCount();

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAudits();
        });

        // Set placeholder message for empty table
        Label placeholderLabel = new Label("Aucun audit disponible.\nLancez un nouvel audit pour commencer.");
        placeholderLabel.setStyle("-fx-text-fill: #667085; -fx-font-size: 14px; -fx-text-alignment: center;");
        auditTable.setPlaceholder(placeholderLabel);

        System.out.println("⏳ En attente de l'injection de HistoryService...");
        System.out.println("   ➤ MainLayoutController doit appeler setHistoryService()");
    }

    // ======================== INJECTION DATABASE ========================

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
        System.out.println("🎯 HistoryService injecté avec succès!");
        System.out.println("   ➤ Service: " + (historyService != null ? "VALIDE" : "NULL"));

        if (historyService != null) {
            loadAuditsFromDatabase();
        } else {
            System.out.println("❌ ERREUR: HistoryService null - contacter l'équipe");
        }
    }

    public void setAuditReportRepository(AuditReportRepository repository) {
        this.auditReportRepository = repository;
        System.out.println("⚠️  MÉTHODE DÉPRÉCIÉE: Utilisez setHistoryService() à la place");
        System.out.println("   ➤ Repository: " + (repository != null ? "VALIDE" : "NULL"));
    }

    // ======================== CONFIGURATION UI ========================

    private void setupTableColumns() {
        // Date Column (inchangé)
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getGeneratedAt();
            if (date != null) {
                return new SimpleStringProperty(date.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        // ======================== PROJECT COLUMN - MODIFIÉ ========================
        // Project Column - Amélioré pour utiliser projectName
        projectColumn.setCellValueFactory(cellData -> {
            AuditReport report = cellData.getValue();

            if (report.getAudit() != null) {
                Audit audit = report.getAudit();

                // 1. Priorité: projectName du modèle Audit (si ajouté dans le modèle)
                if (audit.getProjectName() != null && !audit.getProjectName().trim().isEmpty()) {
                    return new SimpleStringProperty(audit.getProjectName());
                }

                // 2. Fallback: Utiliser le premier document non-null
                List<AuditDocument> docs = audit.getDocuments();
                if (docs != null && !docs.isEmpty()) {
                    for (AuditDocument doc : docs) {
                        if (doc != null && doc.getDocumentName() != null && !doc.getDocumentName().trim().isEmpty()) {
                            return new SimpleStringProperty(doc.getDocumentName());
                        }
                    }
                }

                // 3. Dernier recours: projectId
                return new SimpleStringProperty("Projet #" + audit.getProjectId());
            }
            return new SimpleStringProperty("N/A");
        });

        // ======================== SCORE COLUMN - MODIFIÉ ========================
        // Score Column - Amélioré avec priorité sur les champs de la database
        scoreColumn.setCellValueFactory(cellData -> {
            AuditReport report = cellData.getValue();

            if (report.getAudit() != null) {
                Audit audit = report.getAudit();

                // Priorité 1: Score depuis AuditReport (si le champ score existe dans le modèle)
                try {
                    if (report.getScore() != null) {
                        return new SimpleStringProperty(report.getScore() + "%");
                    }
                } catch (Exception e) {
                    // Si la méthode getScore() n'existe pas encore dans AuditReport
                }

                // Priorité 2: Score depuis Audit (si le champ score existe dans le modèle)
                try {
                    if (audit.getScore() != null) {
                        return new SimpleStringProperty(audit.getScore() + "%");
                    }
                } catch (Exception e) {
                    // Si la méthode getScore() n'existe pas encore dans Audit
                }

                // Priorité 3: Calculer depuis issues (comportement actuel)
                List<AuditIssue> issues = audit.getIssues();
                if (issues != null && !issues.isEmpty()) {
                    // Compter seulement les issues ouvertes
                    long openIssues = issues.stream()
                            .filter(issue -> issue != null &&
                                    ("Open".equals(issue.getStatus()) || "Ouvert".equals(issue.getStatus())))
                            .count();
                    int score = Math.max(0, 100 - ((int)openIssues * 10));
                    return new SimpleStringProperty(score + "%");
                }

                // Si pas d'issues: score par défaut
                return new SimpleStringProperty("100%");
            }

            return new SimpleStringProperty("0%");
        });

        scoreColumn.setCellFactory(column -> new TableCell<AuditReport, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    try {
                        int score = Integer.parseInt(item.replace("%", ""));

                        if (score >= 90) setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                        else if (score >= 80) setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                        else if (score >= 70) setStyle("-fx-text-fill: #84cc16; -fx-font-weight: bold;");
                        else if (score >= 60) setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        else setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } catch (NumberFormatException e) {
                        setText("N/A");
                        setStyle("");
                    }
                }
            }
        });

        // Status Column (inchangé)
        statusColumn.setCellValueFactory(cellData -> {
            AuditReport report = cellData.getValue();

            if (report.getAudit() != null && report.getAudit().getStatus() != null) {
                return new SimpleStringProperty(report.getAudit().getStatus());
            }
            return new SimpleStringProperty("N/A");
        });

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

                    if (item.equals("Completed") || item.equals("Terminé")) {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;");
                    } else if (item.equals("Pending") || item.equals("En attente")) {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;");
                    } else if (item.equals("In Progress") || item.equals("En cours")) {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;");
                    } else {
                        badge.setStyle(badge.getStyle() +
                                "-fx-background-color: #e5e7eb; -fx-text-fill: #374151;");
                    }

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // ======================== PROBLEMS COLUMN - MODIFIÉ ========================
        // Problems Column - Amélioré avec priorité sur les champs de la database
        problemsColumn.setCellValueFactory(cellData -> {
            AuditReport report = cellData.getValue();

            if (report.getAudit() != null) {
                Audit audit = report.getAudit();

                // Priorité 1: problemsCount depuis AuditReport (si le champ existe)
                try {
                    if (report.getProblemsCount() != null) {
                        int count = report.getProblemsCount();
                        if (count > 0) {
                            return new SimpleStringProperty(count + " problème" + (count > 1 ? "s" : ""));
                        }
                        return new SimpleStringProperty("Aucun");
                    }
                } catch (Exception e) {
                    // Si la méthode getProblemsCount() n'existe pas encore
                }

                // Priorité 2: problemsCount depuis Audit (si le champ existe)
                try {
                    if (audit.getProblemsCount() != null) {
                        int count = audit.getProblemsCount();
                        if (count > 0) {
                            return new SimpleStringProperty(count + " problème" + (count > 1 ? "s" : ""));
                        }
                        return new SimpleStringProperty("Aucun");
                    }
                } catch (Exception e) {
                    // Si la méthode getProblemsCount() n'existe pas encore
                }

                // Priorité 3: Compter depuis issues (comportement actuel)
                List<AuditIssue> issues = audit.getIssues();
                if (issues != null && !issues.isEmpty()) {
                    int issueCount = issues.size();
                    if (issueCount > 0) {
                        return new SimpleStringProperty(issueCount + " problème" + (issueCount > 1 ? "s" : ""));
                    }
                }
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

        // Reports Column with buttons (inchangé)
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
        // Partner filter
        partnerComboBox.getItems().clear();
        partnerComboBox.getItems().add("Tous les partenaires");
        partnerComboBox.setValue("Tous les partenaires");

        // Sort By filter
        sortByComboBox.getItems().addAll("Date", "Nom du projet", "Statut");
        sortByComboBox.setValue("Date");

        // Sort Order filter
        sortComboBox.getItems().addAll("Ascendant", "Descendant");
        sortComboBox.setValue("Descendant");

        // Add listeners
        partnerComboBox.setOnAction(e -> filterAudits());
        sortByComboBox.setOnAction(e -> filterAudits());
        sortComboBox.setOnAction(e -> filterAudits());
    }

    // ======================== LOGIQUE DATABASE ========================

    public void loadAuditsFromDatabase() {
        System.out.println("📋 Chargement des audits depuis HistoryService...");

        auditList.clear();
        filteredList.clear();

        try {
            if (historyService == null) {
                System.out.println("❌ ERREUR CRITIQUE: HistoryService est NULL");
                System.out.println("   ACTION REQUISE: MainLayoutController doit:");
                System.out.println("   1. Créer HistoryService avec @Service");
                System.out.println("   2. Injecter via setHistoryService()");
                System.out.println("   3. Vérifier que HistoryService est dans le package services/");
                return;
            }

            List<AuditReport> reports = historyService.getAllAuditReports();

            if (reports != null && !reports.isEmpty()) {
                auditList.addAll(reports);
                filteredList.addAll(reports);
                System.out.println("✅ SUCCÈS: " + reports.size() + " audits chargés via HistoryService");

                updatePartnerFilter();
                logSampleData(reports);
            } else {
                System.out.println("ℹ️  INFO: Aucun audit trouvé dans la database");
                System.out.println("   ➤ Les tables sont vides");
                System.out.println("   ➤ Lancez un audit pour générer des données");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR DATABASE via HistoryService: " + e.getMessage());
            System.err.println("   PROBLÈMES POSSIBLES:");
            System.err.println("   1. HistoryService non configuré");
            System.err.println("   2. Connexion PostgreSQL échouée");
            System.err.println("   3. Méthode getAllAuditReports() n'existe pas");
            e.printStackTrace();
        }

        updateAuditCount();
        filterAudits();
    }

    private void logSampleData(List<AuditReport> reports) {
        if (reports != null && !reports.isEmpty()) {
            System.out.println("📊 Échantillon des données chargées:");
            for (int i = 0; i < Math.min(reports.size(), 3); i++) {
                AuditReport report = reports.get(i);
                System.out.println("   Audit #" + (i+1) + ":");
                System.out.println("     ➤ ID: " + report.getId());
                System.out.println("     ➤ Date: " + report.getGeneratedAt());
                if (report.getAudit() != null) {
                    Audit audit = report.getAudit();
                    System.out.println("     ➤ Projet ID: " + audit.getProjectId());

                    // Essayer d'afficher projectName si disponible
                    try {
                        if (audit.getProjectName() != null) {
                            System.out.println("     ➤ Projet Nom: " + audit.getProjectName());
                        }
                    } catch (Exception e) {
                        // Si getProjectName() n'existe pas encore
                    }

                    System.out.println("     ➤ Statut: " + audit.getStatus());
                    System.out.println("     ➤ Documents: " + audit.getDocuments().size());
                    System.out.println("     ➤ Issues: " + audit.getIssues().size());

                    // Afficher score si disponible
                    try {
                        if (audit.getScore() != null) {
                            System.out.println("     ➤ Score: " + audit.getScore() + "%");
                        }
                    } catch (Exception e) {
                        // Si getScore() n'existe pas encore
                    }
                }
            }
        }
    }

    private void updatePartnerFilter() {
        partnerComboBox.getItems().clear();
        partnerComboBox.getItems().add("Tous les partenaires");

        auditList.stream()
                .map(report -> report.getAudit())
                .filter(audit -> audit != null)
                .map(audit -> audit.getProjectId())
                .distinct()
                .sorted()
                .forEach(projectId -> {
                    partnerComboBox.getItems().add("Projet #" + projectId);
                });

        partnerComboBox.setValue("Tous les partenaires");
        System.out.println("✅ Filtre partenaire: " + (partnerComboBox.getItems().size() - 1) + " projets");
    }

    // ======================== FONCTIONNALITÉS UI ========================

    private void filterAudits() {
        filteredList.clear();

        String searchText = searchField.getText().toLowerCase();
        String selectedPartner = partnerComboBox.getValue();

        for (AuditReport report : auditList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    (report.getReportSummary() != null && report.getReportSummary().toLowerCase().contains(searchText)) ||
                    (report.getAudit() != null && report.getAudit().getComments() != null &&
                            report.getAudit().getComments().toLowerCase().contains(searchText));

            boolean matchesPartner = selectedPartner == null ||
                    selectedPartner.equals("Tous les partenaires") ||
                    isMatchingPartner(report, selectedPartner);

            if (matchesSearch && matchesPartner) {
                filteredList.add(report);
            }
        }

        // Tri
        String sortBy = sortByComboBox.getValue();
        String sortOrder = sortComboBox.getValue();
        boolean ascending = sortOrder != null && sortOrder.equals("Ascendant");

        if (sortBy != null) {
            switch (sortBy) {
                case "Date":
                    filteredList.sort((r1, r2) -> {
                        LocalDateTime date1 = r1.getGeneratedAt();
                        LocalDateTime date2 = r2.getGeneratedAt();
                        if (date1 != null && date2 != null) {
                            return ascending ? date1.compareTo(date2) : date2.compareTo(date1);
                        }
                        return 0;
                    });
                    break;

                case "Statut":
                    filteredList.sort((r1, r2) -> {
                        String status1 = getStatusForSort(r1);
                        String status2 = getStatusForSort(r2);
                        int comparison = status1.compareToIgnoreCase(status2);
                        return ascending ? comparison : -comparison;
                    });
                    break;
            }
        }

        updateAuditCount();
    }

    private boolean isMatchingPartner(AuditReport report, String selectedPartner) {
        if (report.getAudit() == null || selectedPartner == null) {
            return false;
        }

        if (selectedPartner.startsWith("Projet #")) {
            try {
                Long selectedProjectId = Long.parseLong(selectedPartner.replace("Projet #", "").trim());
                return report.getAudit().getProjectId().equals(selectedProjectId);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    private String getStatusForSort(AuditReport report) {
        if (report.getAudit() != null && report.getAudit().getStatus() != null) {
            return report.getAudit().getStatus();
        }
        return "";
    }

    private void updateAuditCount() {
        auditCountLabel.setText(filteredList.size() + " audits");
    }

    private void handleViewReport(AuditReport report) {
        if (report != null) {
            System.out.println("📄 Viewing report #" + report.getId());
            System.out.println("   Report Path: " + report.getReportPath());
            if (report.getAudit() != null) {
                Audit audit = report.getAudit();
                System.out.println("   Audit ID: " + audit.getId());
                System.out.println("   Audit Status: " + audit.getStatus());
                System.out.println("   Documents: " + audit.getDocuments().size());
                System.out.println("   Issues: " + audit.getIssues().size());

                // Afficher plus d'informations si disponibles
                try {
                    if (audit.getProjectName() != null) {
                        System.out.println("   Project Name: " + audit.getProjectName());
                    }
                } catch (Exception e) {}

                try {
                    if (audit.getScore() != null) {
                        System.out.println("   Score: " + audit.getScore() + "%");
                    }
                } catch (Exception e) {}
            }
        }
    }

    private void handleDownloadPDF(AuditReport report) {
        if (report != null) {
            System.out.println("📥 Downloading PDF for report #" + report.getId());
            System.out.println("   PDF Path: " + report.getReportPath());
        }
    }

    // ======================== MÉTHODES PUBLIQUES ========================

    public void refreshAudits() {
        System.out.println("🔄 Rafraîchissement des audits via HistoryService...");
        loadAuditsFromDatabase();
    }

    public void addNewAudit(AuditReport newReport) {
        if (newReport == null) {
            System.out.println("⚠️  Audit null - non ajouté");
            return;
        }

        System.out.println("🎯 Ajout d'un NOUVEL audit à l'historique:");
        System.out.println("   ➤ Report ID: " + newReport.getId());
        System.out.println("   ➤ Date: " + newReport.getGeneratedAt());

        auditList.add(0, newReport);
        filterAudits();

        if (auditTable != null) {
            auditTable.scrollTo(0);
            auditTable.getSelectionModel().select(0);
        }

        updatePartnerFilter();
        System.out.println("✅ Nouvel audit ajouté à l'historique!");
    }

    public void setAuditData(List<AuditReport> reports) {
        auditList.clear();
        filteredList.clear();

        if (reports != null && !reports.isEmpty()) {
            auditList.addAll(reports);
            filteredList.addAll(reports);
            System.out.println("✅ " + reports.size() + " audits définis");
            updatePartnerFilter();
        }

        updateAuditCount();
        filterAudits();
    }

    public void checkStatus() {
        System.out.println("\n=== ÉTAT HISTORY CONTROLLER ===");
        System.out.println("HistoryService injecté: " + (historyService != null ? "✅ OUI" : "❌ NON"));
        System.out.println("Repository injecté: " + (auditReportRepository != null ? "✅ OUI (déprécié)" : "❌ NON"));
        System.out.println("Audits chargés: " + auditList.size());
        System.out.println("Table initialisée: " + (auditTable != null ? "✅ OUI" : "❌ NON"));
        System.out.println("===============================\n");
    }
}