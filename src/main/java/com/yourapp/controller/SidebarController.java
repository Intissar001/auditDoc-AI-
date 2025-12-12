package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController {

    @FXML private VBox sidebar;
    @FXML private VBox menuContainer;
    @FXML private Button toggleSidebarBtn;
    @FXML private HBox footer;

    private boolean collapsed = false;

    private static final double EXPANDED = 250;
    private static final double COLLAPSED = 72;

    @FXML
    public void initialize() {

        toggleSidebarBtn.setOnAction(e -> toggle());

        if (footer != null) footer.setAlignment(Pos.CENTER_LEFT);
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
