package com.yourapp.controller;

import com.yourapp.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProjectCardController {

    @FXML private Label lblName;
    @FXML private Label lblPartner;
    @FXML private Label lblDesc;
    @FXML private Label lblStatus;
    @FXML private Label lblProgress;
    @FXML private Label lblStartDate;
    @FXML private Label lblEndDate;
    @FXML private Label lblAudit; // Doit correspondre à l'ID FXML

    @FXML private Button btnEdit;
    @FXML private Button btnFolder;

    private Project project;

    // Méthode publique pour initialiser la carte avec les données
    public void setProject(Project p, ProjetsController parentController) {
        this.project = p;

        // Remplissage des Labels
        lblName.setText(p.getName());
        lblPartner.setText(p.getPartner());
        lblDesc.setText(p.getDescription());
        lblStatus.setText(p.getStatus());
        lblProgress.setText(p.getProgress() + "%");
        lblStartDate.setText(p.getStartDate().toString());
        lblEndDate.setText(p.getEndDate().toString());
        // Utilise la méthode mise à jour du modèle
        if (p.getProchainAuditDate() != null) {
            lblAudit.setText(p.getProchainAuditDate().toString());
        }

        // Gestion des actions (délégation au contrôleur parent)
        btnEdit.setOnAction(e -> parentController.openProjectForm(p));
        btnFolder.setOnAction(e -> parentController.showFolderAlert(p.getName()));
    }
}