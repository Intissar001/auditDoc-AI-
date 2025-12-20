package com.yourapp.controller;

import com.yourapp.model.Project;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Component
public class ProjetsController {

    @FXML
    private FlowPane projectsContainer;

    private List<Project> projectList = new ArrayList<>();

    @FXML
    public void initialize() {
        // 1. Simuler des données (Normalement ça vient d'une BDD)
        projectList.add(new Project("Projet Éducation Rurale", "Programme d'alphabétisation...", LocalDate.of(2024, 1, 15), LocalDate.of(2025, 12, 31), "Agence Française", "Actif", 87 , LocalDate.of(2024, 12, 15)));
        projectList.add(new Project("Santé Communautaire", "Accès aux soins...", LocalDate.of(2024, 3, 1), LocalDate.of(2026, 2, 28), "USAID", "Actif", 92 , LocalDate.of(2024, 12, 15)));

        // 2. Afficher les cartes
        refreshView();
    }

    private void refreshView() {
        projectsContainer.getChildren().clear();
        for (Project p : projectList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/ProjectCard.fxml"));
                Node cardNode = loader.load();
                ProjectCardController cardController = loader.getController();

                // --- Remplissage MANUEL de la carte ---
                // On utilise lookup("#id") pour trouver les éléments dans la carte chargée
                ((Label) cardNode.lookup("#lblName")).setText(p.getName());
                ((Label) cardNode.lookup("#lblPartner")).setText(p.getPartner());
                ((Label) cardNode.lookup("#lblDesc")).setText(p.getDescription());
                ((Label) cardNode.lookup("#lblStartDate")).setText(p.getStartDate().toString());
                ((Label) cardNode.lookup("#lblEndDate")).setText(p.getEndDate().toString());
                ((Label) cardNode.lookup("#lblAudit")).setText(p.getProchainAuditDate().toString());
                ((Label) cardNode.lookup("#lblStatus")).setText(p.getStatus());
                ((Label) cardNode.lookup("#lblProgress")).setText(p.getProgress() + "%");

                // --- Gestion des Boutons de la carte ---

                // Bouton Edit (Crayon)
                Button btnEdit = (Button) cardNode.lookup("#btnEdit");
                btnEdit.setOnAction(e -> openProjectForm(p));

                // Bouton Dossier
                Button btnFolder = (Button) cardNode.lookup("#btnFolder");
                btnFolder.setOnAction(e -> showFolderAlert(p.getName()));
                cardController.setProject(p, this);

                projectsContainer.getChildren().add(cardNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCreateProject() {
        openProjectForm(null); // null veut dire "Création"
    }

    void openProjectForm(Project projectToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/ProjectForm.fxml"));
            Parent root = loader.load();

            ProjectFormController formController = loader.getController();

            // Si on édite, on passe le projet. Sinon on laisse vide.
            if (projectToEdit != null) {
                formController.setProjectData(projectToEdit);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(projectToEdit == null ? "Créer un Projet" : "Modifier le Projet");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Ici, idéalement, on recharge la liste après fermeture
            // refreshView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showFolderAlert(String projectName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dossier du projet");
        alert.setHeaderText("Ouverture du dossier : " + projectName);
        alert.setContentText("Cette fonctionnalité permet d'accéder aux documents associés.");
        alert.showAndWait();
    }
}