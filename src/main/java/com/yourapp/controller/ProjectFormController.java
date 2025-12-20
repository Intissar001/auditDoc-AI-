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
            // Note: Ajoutez ici cbAuditStandards si vous l'avez dans votre modèle Project
        }
    }

    @FXML
    private void handleSave() {
        // 1. Vérification simple
        if (txtName.getText().isEmpty()) {
            showAlert("Erreur", "Le nom du projet est obligatoire.");
            return;
        }

        // 2. Création ou Mise à jour de l'objet
        if (currentProject == null) {
            // MODE CRÉATION
            currentProject = new Project(
                    txtName.getText(),
                    txtDescription.getText(),
                    dpStart.getValue(),
                    dpEnd.getValue(),
                    cbPartner.getValue(),
                    cbStatus.getValue(),
                    0, // Progrès initial à 0%
                    LocalDate.now().plusMonths(6) // Date audit par défaut (ex: dans 6 mois)
            );
        } else {
            // MODE MODIFICATION
            currentProject.setName(txtName.getText());
            currentProject.setDescription(txtDescription.getText());
            currentProject.setStartDate(dpStart.getValue());
            currentProject.setEndDate(dpEnd.getValue());
            currentProject.setPartner(cbPartner.getValue());
            currentProject.setStatus(cbStatus.getValue());
        }

        // 3. Sauvegarde réelle dans Supabase via le Service
        try {
            if (projectService != null) {
                projectService.saveProject(currentProject);
                System.out.println("Projet enregistré avec succès dans Supabase !");
                closeWindow();
            } else {
                System.err.println("Erreur: Le ProjectService n'est pas initialisé !");
                showAlert("Erreur Technique", "Le service de base de données est indisponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de sauvegarde", "Impossible de contacter Supabase : " + e.getMessage());
        }
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