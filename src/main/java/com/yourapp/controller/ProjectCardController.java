package com.yourapp.controller;

import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectCardController {

    @FXML private Label lblName;
    @FXML private Label lblPartner;
    @FXML private Label lblDesc;
    @FXML private Label lblStatus;
    @FXML private Label lblProgress;
    @FXML private Label lblStartDate;
    @FXML private Label lblEndDate;
    @FXML private Label lblAudit;

    @FXML private Button btnEdit;
    @FXML private Button btnFolder;

    private Project project;
    private ProjectService projectService;

    //  Nouvelle méthode pour recevoir le service depuis ProjetsController
    public void setProjectService(ProjectService service) {
        this.projectService = service;
    }

    /**
     * Remplit les éléments de l'interface avec les données du projet.
     * Cette méthode est appelée par ProjetsController lors de la création de la carte.
     */
    public void setProjectData(Project p) {
        this.project = p;

        // Remplissage des Labels avec vérification de nullité
        if (lblName != null) lblName.setText(p.getName());
        if (lblPartner != null) lblPartner.setText(p.getPartner());
        if (lblDesc != null) lblDesc.setText(p.getDescription());
        if (lblStatus != null) lblStatus.setText(p.getStatus());
        if (lblProgress != null) lblProgress.setText(p.getProgress() + "%");

        // Formatage des dates pour éviter les erreurs si elles sont nulles en base
        if (lblStartDate != null) {
            lblStartDate.setText(p.getStartDate() != null ? p.getStartDate().toString() : "--/--/----");
        }
        if (lblEndDate != null) {
            lblEndDate.setText(p.getEndDate() != null ? p.getEndDate().toString() : "--/--/----");
        }
        if (lblAudit != null) {
            lblAudit.setText(p.getProchainAuditDate() != null ? p.getProchainAuditDate().toString() : "Non planifié");
        }
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/ProjectForm.fxml"));
            Parent root = loader.load();

            ProjectFormController formController = loader.getController();

            // ✅ ON DONNE LE SERVICE AU FORMULAIRE
            formController.setProjectService(this.projectService);
            // ✅ ON DONNE LES DONNÉES DU PROJET
            formController.setProjectData(this.project);

            Stage stage = new Stage();
            stage.setTitle("Modifier le projet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Optionnel : Tu peux ajouter ici une logique pour rafraîchir la carte
            // ou recharger la liste si nécessaire.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenFolder() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dossier Projet");
        alert.setHeaderText(null);
        alert.setContentText("Ouverture des documents pour le projet : " + project.getName());
        alert.showAndWait();
    }
}