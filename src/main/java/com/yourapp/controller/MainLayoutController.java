package com.yourapp.controller;

import com.yourapp.services.HistoryService;
import com.yourapp.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private TopbarController topbarController;

    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        }

        if (topbarController != null) {
            topbarController.setMainController(this);
        }

        loadView("Dashboard.fxml");
    }

    public void setSpringContext(ApplicationContext context) {
        this.springContext = context;
    }

    public void loadView(String fxmlFile) {
        try {
            contentArea.getChildren().clear();

            String fxmlPath = "/views/fxml/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if ("History.fxml".equals(fxmlFile)) {
                HistoryController historyController = loader.getController();
                HistoryService historyService = springContext.getBean(HistoryService.class);
                historyController.setHistoryService(historyService);
            }

            // --- TON NOUVEAU BLOC À AJOUTER ---
            if ("ProjetsView.fxml".equals(fxmlFile)) {
                ProjetsController projetsController = loader.getController();
                // On va chercher le service dans Spring
                ProjectService projectService = springContext.getBean(ProjectService.class);
                // On le donne à ton contrôleur
                projetsController.setProjectService(projectService);
            }

            contentArea.getChildren().add(view);

        } catch (IOException e) {
            showErrorView("Impossible de charger la page: " + fxmlFile);
        } catch (Exception e) {
            showErrorView("Erreur inattendue: " + e.getMessage());
        }
    }

    public void updateSidebarActive(String menuName) {
        if (sidebarController != null) {
            sidebarController.setActiveMenuItemByName(menuName);
        }
    }

    private void showErrorView(String errorMessage) {
        contentArea.getChildren().clear();
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(errorMessage);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        contentArea.getChildren().add(errorLabel);
    }
}
