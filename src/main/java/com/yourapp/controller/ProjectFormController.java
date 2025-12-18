package com.yourapp.controller;

import com.sun.glass.ui.Menu;
import com.yourapp.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProjectFormController {

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private ComboBox<String> cbPartner;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<String> cbAuditStandards;

    private Project currentProject; // Le projet en cours d'édition (si null = création)

    @FXML
    public void initialize() {
        // Initialiser les listes déroulantes
        cbPartner.getItems().addAll("USAID", "UNICEF", "Banque Mondiale", "Agence Française");
        cbStatus.getItems().addAll("Actif", "Clôturé", "En Attente");
        cbAuditStandards.getItems().addAll("ISO 9001", "Interne", "Réglementaire", "Spécifique Bailleur");
    }

    // Méthode appelée par ProjetsController pour pré-remplir le formulaire
    public void setProjectData(Project project) {
        this.currentProject = project;
        if (project != null) {
            txtName.setText(project.getName());
            txtDescription.setText(project.getDescription());
            dpStart.setValue(project.getStartDate());
            dpEnd.setValue(project.getEndDate());
            cbPartner.setValue(project.getPartner());
            cbStatus.setValue(project.getStatus());
        }
    }

    @FXML
    private void handleSave() {
        // Logique de sauvegarde
        String name = txtName.getText();
        System.out.println("Sauvegarde du projet : " + name);

        // TODO: Mettre à jour 'currentProject' ou créer un nouveau et l'ajouter à la liste principale

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}