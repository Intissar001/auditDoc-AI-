package com.yourapp.controller;

import com.yourapp.model.AuditTemplate;
import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProjectFormController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private ComboBox<AuditTemplate> cbPartner;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<AuditTemplate> cbAuditStandards;
    @FXML private DatePicker dpAudit;
    @Autowired
    private ProjectService projectService;
    private Project currentProject; // Si null = Création, sinon = Modification

    @FXML
    public void initialize() {
        // 1. Charger les vraies données de la base
        List<AuditTemplate> templates = projectService.getAllTemplates();

        // 2. Remplir les ComboBox
        cbPartner.getItems().setAll(templates);
        cbAuditStandards.getItems().setAll(templates);

        // 3. Configurer l'affichage (Converter) pour éviter les adresses mémoires @...
        StringConverter<AuditTemplate> partnerConverter = new StringConverter<>() {
            @Override public String toString(AuditTemplate t) { return t != null ? t.getOrganization() : ""; }
            @Override public AuditTemplate fromString(String s) { return null; }
        };

        StringConverter<AuditTemplate> standardConverter = new StringConverter<>() {
            @Override public String toString(AuditTemplate t) { return t != null ? t.getName() : ""; }
            @Override public AuditTemplate fromString(String s) { return null; }
        };

        cbPartner.setConverter(partnerConverter);
        cbAuditStandards.setConverter(standardConverter);

        // Status reste en String
        cbStatus.getItems().addAll("Actif", "Clôturé", "En Attente");
        cbStatus.setValue("Actif");
    }

    /**
     * Reçoit le service Spring depuis le ProjetsController
     */
    /**
     * Pré-remplit le formulaire pour la MODIFICATION d'un projet existant
     */
    public void setProjectData(Project project) {
        this.currentProject = project;
        if (project != null) {
            txtName.setText(project.getName());
            txtDescription.setText(project.getDescription());
            dpStart.setValue(project.getStartDate());
            dpEnd.setValue(project.getEndDate());
            cbStatus.setValue(project.getStatus());
            if (project.getProchainAuditDate() != null) dpAudit.setValue(project.getProchainAuditDate());

            // --- Sélection automatique des objets lors de la modification ---
            if (project.getPartner() != null) {
                cbPartner.getItems().stream()
                        .filter(t -> t.getOrganization().equals(project.getPartner()))
                        .findFirst().ifPresent(cbPartner::setValue);
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        // On extrait les Strings des objets sélectionnés
        String selectedPartner = cbPartner.getValue() != null ? cbPartner.getValue().getOrganization() : "";

        if (currentProject == null) {
            currentProject = new Project();
            currentProject.setProgress(0);
        }

        currentProject.setName(txtName.getText().trim());
        currentProject.setDescription(txtDescription.getText().trim());
        currentProject.setStartDate(dpStart.getValue());
        currentProject.setEndDate(dpEnd.getValue());
        currentProject.setPartner(selectedPartner); // On passe bien un String
        currentProject.setStatus(cbStatus.getValue());
        currentProject.setProchainAuditDate(dpAudit.getValue());

        try {
            projectService.saveProject(currentProject);
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Sauvegarde impossible : " + e.getMessage());
        }
    }

    /**
     * Vérifie chaque champ. Si un champ est vide, il devient rouge.
     * @return true si tout est bon, false s'il y a une erreur.
     */
    private boolean validateForm() {
        boolean isValid = true;
        String errorStyle = "-fx-border-color: red; -fx-border-width: 1px;";

        // 1. Vérification du NOM
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            txtName.setStyle(errorStyle);
            isValid = false;
        } else {
            txtName.setStyle(null); // Enlève le rouge si c'est bon
        }

        // 2. Vérification de la DESCRIPTION
        if (txtDescription.getText() == null || txtDescription.getText().trim().isEmpty()) {
            txtDescription.setStyle(errorStyle);
            isValid = false;
        } else {
            txtDescription.setStyle(null);
        }

        // 3. Vérification des DATES
        if (dpStart.getValue() == null) {
            dpStart.setStyle(errorStyle);
            isValid = false;
        } else {
            dpStart.setStyle(null);
        }

        if (dpEnd.getValue() == null) {
            dpEnd.setStyle(errorStyle);
            isValid = false;
        } else {
            dpEnd.setStyle(null);
        }

        // 4. Vérification LOGIQUE des dates (Fin après Début)
        if (dpStart.getValue() != null && dpEnd.getValue() != null) {
            if (dpEnd.getValue().isBefore(dpStart.getValue())) {
                showAlert("Erreur de date", "La date de fin ne peut pas être avant la date de début !");
                dpEnd.setStyle(errorStyle);
                isValid = false;
            }
        }

        // 5. Vérification des LISTES DÉROULANTES (Combobox)
        if (cbPartner.getValue() == null) {
            cbPartner.setStyle(errorStyle);
            isValid = false;
        } else {
            cbPartner.setStyle(null);
        }

        if (cbStatus.getValue() == null) {
            cbStatus.setStyle(errorStyle);
            isValid = false;
        } else {
            cbStatus.setStyle(null);
        }

        // --- AJOUT : Vérification Date Audit ---
        if (dpAudit.getValue() == null) {
            dpAudit.setStyle(errorStyle);
            isValid = false;
        } else {
            dpAudit.setStyle(null);
        }

        // --- AJOUT : Logique (L'audit ne peut pas être avant le début du projet) ---
        if (dpStart.getValue() != null && dpAudit.getValue() != null) {
            if (dpAudit.getValue().isBefore(dpStart.getValue())) {
                showAlert("Erreur Date Audit", "La date d'audit ne peut pas être avant le début du projet !");
                dpAudit.setStyle(errorStyle);
                isValid = false;
            }
        }
        return isValid;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}