package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class NavbarController {

    @FXML private VBox navbarContainer;
    @FXML private HBox logoContainer;
    @FXML private Label appLogoLabel;

    // Menu items
    @FXML private HBox dashboardBtn;
    @FXML private HBox auditBtn;
    @FXML private HBox projectsBtn;
    @FXML private HBox historyBtn;
    @FXML private HBox settingsBtn;

    // Icons
    @FXML private Label dashboardIcon;
    @FXML private Label auditIcon;
    @FXML private Label projectsIcon;
    @FXML private Label historyIcon;
    @FXML private Label settingsIcon;

    // Labels
    @FXML private Label dashboardLabel;
    @FXML private Label auditLabel;
    @FXML private Label projectsLabel;
    @FXML private Label historyLabel;
    @FXML private Label settingsLabel;

    // Active indicator
    @FXML private Circle auditActiveCircle;

    // Footer
    @FXML private HBox footerContainer;
    @FXML private Label collapseIcon;
    @FXML private Label collapseLabel;

    private boolean collapsed = false;
    private HBox currentActive = null;

    @FXML
    public void initialize() {
        System.out.println("✅ NavbarController initialized");

        // Set Audit as default active
        currentActive = auditBtn;

        // Setup click handlers for all menu items
        setupNavigationHandlers();
    }

    /**
     * Setup click handlers for navigation
     */
    private void setupNavigationHandlers() {
        if (dashboardBtn != null) {
            dashboardBtn.setOnMouseClicked(e -> {
                setActiveMenuItem(dashboardBtn);
                loadView("Dashboard.fxml");
            });
        }

        if (auditBtn != null) {
            auditBtn.setOnMouseClicked(e -> {
                setActiveMenuItem(auditBtn);
                loadView("Audit.fxml");
            });
        }

        if (projectsBtn != null) {
            projectsBtn.setOnMouseClicked(e -> {
                setActiveMenuItem(projectsBtn);
                loadView("Project.fxml");
            });
        }

        if (historyBtn != null) {
            historyBtn.setOnMouseClicked(e -> {
                System.out.println("🔄 History button clicked!");
                setActiveMenuItem(historyBtn);
                loadView("history.fxml");
            });
        }

        if (settingsBtn != null) {
            settingsBtn.setOnMouseClicked(e -> {
                setActiveMenuItem(settingsBtn);
                loadView("settings.fxml");
            });
        }
    }

    /**
     * Set active menu item styling
     */
    private void setActiveMenuItem(HBox menuItem) {
        // Remove active class from all
        if (dashboardBtn != null) dashboardBtn.getStyleClass().remove("menu-item-active");
        if (auditBtn != null) auditBtn.getStyleClass().remove("menu-item-active");
        if (projectsBtn != null) projectsBtn.getStyleClass().remove("menu-item-active");
        if (historyBtn != null) historyBtn.getStyleClass().remove("menu-item-active");
        if (settingsBtn != null) settingsBtn.getStyleClass().remove("menu-item-active");

        // Add to all
        if (dashboardBtn != null) dashboardBtn.getStyleClass().add("menu-item");
        if (auditBtn != null) auditBtn.getStyleClass().add("menu-item");
        if (projectsBtn != null) projectsBtn.getStyleClass().add("menu-item");
        if (historyBtn != null) historyBtn.getStyleClass().add("menu-item");
        if (settingsBtn != null) settingsBtn.getStyleClass().add("menu-item");

        // Set active
        if (menuItem != null) {
            menuItem.getStyleClass().remove("menu-item");
            menuItem.getStyleClass().add("menu-item-active");
        }

        currentActive = menuItem;
    }

    /**
     * Load view in MainLayout's contentArea
     */
    private void loadView(String fxmlFile) {
        try {
            System.out.println("🔄 Loading view: " + fxmlFile);

            // Build full path
            String fullPath = "/views/fxml/" + fxmlFile;

            // Check if file exists
            if (getClass().getResource(fullPath) == null) {
                System.err.println("❌ FXML file NOT FOUND: " + fullPath);
                System.err.println("📂 Make sure file exists in: src/main/resources/views/fxml/");
                return;
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            Parent content = loader.load();
            System.out.println("✅ FXML loaded successfully!");

            // Find contentArea in MainLayout
            StackPane contentArea = findContentArea();
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
                System.out.println("✅ View loaded in contentArea!");
            } else {
                System.err.println("❌ contentArea NOT FOUND in MainLayout!");
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find MainLayout's contentArea (StackPane in center)
     */
    private StackPane findContentArea() {
        try {
            // Start from navbarContainer and go up to BorderPane (MainLayout)
            javafx.scene.Node node = navbarContainer;

            while (node != null) {
                if (node instanceof javafx.scene.layout.BorderPane) {
                    javafx.scene.layout.BorderPane borderPane =
                            (javafx.scene.layout.BorderPane) node;

                    // contentArea is in center of BorderPane
                    if (borderPane.getCenter() instanceof StackPane) {
                        System.out.println("✅ Found contentArea!");
                        return (StackPane) borderPane.getCenter();
                    }
                }
                node = node.getParent();
            }

            System.err.println("❌ Could not find BorderPane or contentArea");

        } catch (Exception e) {
            System.err.println("❌ Error finding contentArea: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Toggle sidebar collapse
     */
    @FXML
    private void toggleSidebar() {
        collapsed = !collapsed;

        if (collapsed) {
            navbarContainer.setPrefWidth(60);
            navbarContainer.setMinWidth(60);

            // Hide labels
            if (dashboardLabel != null) dashboardLabel.setVisible(false);
            if (auditLabel != null) auditLabel.setVisible(false);
            if (projectsLabel != null) projectsLabel.setVisible(false);
            if (historyLabel != null) historyLabel.setVisible(false);
            if (settingsLabel != null) settingsLabel.setVisible(false);
            if (collapseLabel != null) collapseLabel.setVisible(false);

            if (collapseIcon != null) collapseIcon.setText("⮞");

        } else {
            navbarContainer.setPrefWidth(250);
            navbarContainer.setMinWidth(250);

            // Show labels
            if (dashboardLabel != null) dashboardLabel.setVisible(true);
            if (auditLabel != null) auditLabel.setVisible(true);
            if (projectsLabel != null) projectsLabel.setVisible(true);
            if (historyLabel != null) historyLabel.setVisible(true);
            if (settingsLabel != null) settingsLabel.setVisible(true);
            if (collapseLabel != null) collapseLabel.setVisible(true);

            if (collapseIcon != null) collapseIcon.setText("<");
        }
    }

    /**
     * Handle navigation (called from FXML onMouseClicked)
     */
    @FXML
    private void handleNavigation(javafx.scene.input.MouseEvent event) {
        HBox clickedItem = (HBox) event.getSource();

        if (clickedItem == dashboardBtn) {
            setActiveMenuItem(dashboardBtn);
            loadView("Dashboard.fxml");
        } else if (clickedItem == auditBtn) {
            setActiveMenuItem(auditBtn);
            loadView("Audit.fxml");
        } else if (clickedItem == projectsBtn) {
            setActiveMenuItem(projectsBtn);
            loadView("Project.fxml");
        } else if (clickedItem == historyBtn) {
            setActiveMenuItem(historyBtn);
            loadView("history.fxml");
        } else if (clickedItem == settingsBtn) {
            setActiveMenuItem(settingsBtn);
            loadView("settings.fxml");
        }
    }
}