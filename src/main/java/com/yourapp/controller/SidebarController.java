package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class SidebarController {

    @FXML private VBox sidebar;
    @FXML private VBox menuContainer;
    @FXML private Button toggleSidebarBtn;
    @FXML private HBox footer;

    @FXML private HBox dashboardBtn;
    @FXML private HBox auditBtn;
    @FXML private HBox projetsBtn;
    @FXML private HBox historiqueBtn;
    @FXML private HBox settingsBtn;

    private boolean collapsed = false;
    private MainLayoutController mainController;

    private static final double EXPANDED = 250;
    private static final double COLLAPSED = 72;

    @FXML
    public void initialize() {
        toggleSidebarBtn.setOnAction(e -> toggle());
        if (footer != null) footer.setAlignment(Pos.CENTER_LEFT);

        // Set dashboard as active by default
        setActiveMenuItem(dashboardBtn);
    }

    public void setMainController(MainLayoutController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleDashboard(MouseEvent event) {
        setActiveMenuItem(dashboardBtn);
        if (mainController != null) {
            mainController.loadView("Dashboard.fxml");
        }
    }

    @FXML
    private void handleAudit(MouseEvent event) {
        setActiveMenuItem(auditBtn);
        if (mainController != null) {
            mainController.loadView("Audit.fxml");
        }
    }

    @FXML
    private void handleProjets(MouseEvent event) {
        setActiveMenuItem(projetsBtn);
        if (mainController != null) {
            mainController.loadView("ProjetsView.fxml");
        }
    }

    @FXML
    private void handleHistorique(MouseEvent event) {
        setActiveMenuItem(historiqueBtn);
        if (mainController != null) {
            mainController.loadView("history.fxml");
        }
    }

    @FXML
    private void handleSettings(MouseEvent event) {
        setActiveMenuItem(settingsBtn);
        if (mainController != null) {
            mainController.loadView("settings.fxml");
        }
    }

    private void setActiveMenuItem(HBox selectedItem) {
        // Remove active class from all menu items
        menuContainer.getChildren().forEach(item -> {
            if (item instanceof HBox) {
                item.getStyleClass().remove("active");
            }
        });

        // Add active class to selected item
        if (!selectedItem.getStyleClass().contains("active")) {
            selectedItem.getStyleClass().add("active");
        }
    }

    public void setActiveMenuItemByName(String menuName) {
        switch (menuName.toLowerCase()) {
            case "dashboard":
                setActiveMenuItem(dashboardBtn);
                break;
            case "audit":
                setActiveMenuItem(auditBtn);
                break;
            case "projects":
            case "projets":
                setActiveMenuItem(projetsBtn);
                break;
            case "history":
            case "historique":
                setActiveMenuItem(historiqueBtn);
                break;
            case "settings":
            case "paramètres":
                setActiveMenuItem(settingsBtn);
                break;
        }
    }

    private void toggle() {
        collapsed = !collapsed;

        sidebar.setPrefWidth(collapsed ? COLLAPSED : EXPANDED);
        sidebar.setMinWidth(collapsed ? COLLAPSED : EXPANDED);
        sidebar.setMaxWidth(collapsed ? COLLAPSED : EXPANDED);

        if (collapsed) {
            if (!sidebar.getStyleClass().contains("reduced"))
                sidebar.getStyleClass().add("reduced");
        } else {
            sidebar.getStyleClass().remove("reduced");
        }

        // Menu icon & label alignment
        menuContainer.getChildren().forEach(item -> {
            if (item instanceof HBox h) {
                // hide/show labels
                if (h.getChildren().size() > 1) {
                    Label label = (Label) h.getChildren().get(1);
                    label.setVisible(!collapsed);
                    label.setManaged(!collapsed);
                }

                // center icons in collapsed mode
                h.setAlignment(collapsed ? Pos.CENTER : Pos.CENTER_LEFT);
            }
        });

        // footer alignment
        if (footer != null)
            footer.setAlignment(collapsed ? Pos.CENTER : Pos.CENTER_LEFT);

        toggleSidebarBtn.setText(collapsed ? "⮞" : "⮜");
    }
}