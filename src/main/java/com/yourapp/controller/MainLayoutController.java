package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
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
    }

    public void setSpringContext(ApplicationContext context) {
        this.springContext = context;
    }

    public void loadView(String fxmlFile) {
        try {
            if (springContext == null) {
                throw new IllegalStateException("SpringContext non initialisé !");
            }

            contentArea.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/" + fxmlFile)
            );

            loader.setControllerFactory(springContext::getBean);

            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof ProjetsController pc) {
                pc.refresh();
            }

            if (controller instanceof SettingsController sc) {
                sc.refresh();
            }

            contentArea.getChildren().add(view);

        } catch (Exception e) {
            showErrorView(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateSidebarActive(String menuName) {
        if (sidebarController != null) {
            sidebarController.setActiveMenuItemByName(menuName);
        }
    }

    private void showErrorView(String errorMessage) {
        contentArea.getChildren().clear();
        Label errorLabel = new Label(errorMessage);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        contentArea.getChildren().add(errorLabel);
    }
}
