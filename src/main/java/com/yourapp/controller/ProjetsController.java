package com.yourapp.controller;

import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import com.yourapp.utils.SpringContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
@Component
public class ProjetsController {

    @FXML
    private FlowPane projectsContainer; // Assure-toi que cet ID existe dans ProjetsView.fxml
    @Autowired
    private ProjectService projectService;

    /**
     * Cette méthode est appelée manuellement par MainLayoutController
     * après le chargement du fichier FXML.
     */
    /*public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
        loadProjectsFromDatabase();
    }*/

    @FXML
    public void initialize() {
        // On ne charge rien ici car projectService est encore null à ce stade
        System.out.println("ProjetsView initialisée, en attente du service...");
    }

    /**
     * Utilise le service pour récupérer les projets et créer les cartes
     */
    private void loadProjectsFromDatabase() {
        if (projectService == null) return;

        try {
            // 1. Récupérer la liste des projets depuis Supabase via le service
            List<Project> projects = projectService.getAllProjects();

            // 2. Nettoyer l'affichage actuel
            projectsContainer.getChildren().clear();

            // 3. Créer une carte pour chaque projet
            for (Project project : projects) {
                createProjectCard(project);
            }

            System.out.println(projects.size() + " projets chargés avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des projets : " + e.getMessage());
        }
    }

    /**
     * Charge le fichier ProjectCard.fxml et remplit ses données
     */
    private void createProjectCard(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/ProjectCard.fxml")
            );

            loader.setControllerFactory(SpringContext.getContext()::getBean);

            Node card = loader.load();

            ProjectCardController cardController = loader.getController();
            cardController.setProjectData(project);

            // ✨ AJOUTE CETTE LIGNE : On donne le service à la CARTE
            //cardController.setProjectService(this.projectService);

            projectsContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateProject() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/ProjectForm.fxml")
            );

            loader.setControllerFactory(SpringContext.getContext()::getBean);

            Parent root = loader.load();


            // --- LA LIGNE INDISPENSABLE ICI ---
            ProjectFormController formController = loader.getController();
            //formController.setProjectService(this.projectService); // On donne le service au formulaire
            // ----------------------------------

            Stage stage = new Stage();
            stage.setTitle("Nouveau Projet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadProjectsFromDatabase(); // Rafraîchit la liste après la fermeture

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refresh() {
        loadProjectsFromDatabase();
    }


}