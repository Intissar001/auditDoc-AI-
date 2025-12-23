package com.yourapp.controller;

import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
public class ProjectFormController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private ComboBox<String> cbPartner;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<String> cbAuditStandards;
    @FXML private DatePicker dpAudit;
    @Autowired
    private ProjectService projectService;
    private Project currentProject; // Si null = Création, sinon = Modification

    @FXML
    public void initialize() {
        // Initialisation des listes déroulantes
        cbPartner.getItems().addAll("USAID", "UNICEF", "Banque Mondiale", "Agence Française");
        cbStatus.getItems().addAll("Actif", "Clôturé", "En Attente");
        cbAuditStandards.getItems().addAll("ISO 9001", "Interne", "Réglementaire", "Spécifique Bailleur");

        // Valeurs par défaut
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
            cbPartner.setValue(project.getPartner());
            cbStatus.setValue(project.getStatus());

            if (project.getProchainAuditDate() != null) {
                dpAudit.setValue(project.getProchainAuditDate());
            }

        }
    }

    @FXML
    private void handleSave() {
        // 1. VALIDATION COMPLÈTE (Champs vides + Logique dates)
        if (!validateForm()) {
            showAlert("Formulaire invalide", "Veuillez corriger les champs en rouge.");
            return; // On arrête tout si ce n'est pas valide
        }

        // 2. CRÉATION OU MISE À JOUR
        if (currentProject == null) {
            // --- MODE CRÉATION ---
            currentProject = new Project(
                    txtName.getText().trim(),
                    txtDescription.getText().trim(),
                    dpStart.getValue(),
                    dpEnd.getValue(),
                    cbPartner.getValue(),
                    cbStatus.getValue(),
                    0, // Progrès 0%
                    dpAudit.getValue() // Date audit par défaut
            );
        } else {
            // --- MODE MODIFICATION ---
            currentProject.setName(txtName.getText().trim());
            currentProject.setDescription(txtDescription.getText().trim());
            currentProject.setStartDate(dpStart.getValue());
            currentProject.setEndDate(dpEnd.getValue());
            currentProject.setPartner(cbPartner.getValue());
            currentProject.setStatus(cbStatus.getValue());
            currentProject.setProchainAuditDate(dpAudit.getValue());
        }

        // 3. SAUVEGARDE VIA LE SERVICE
        try {
            if (projectService != null) {
                projectService.saveProject(currentProject);
                System.out.println("Projet enregistré avec succès !");
                closeWindow();
            } else {
                System.err.println("Erreur critique : ProjectService est null");
                showAlert("Erreur Technique", "Le service de base de données est indisponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de sauvegarde", "Impossible de contacter la base de données : " + e.getMessage());
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