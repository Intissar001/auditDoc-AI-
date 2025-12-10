package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        System.out.println("MainController initialized - contentArea: " + (contentArea != null));
    }
}