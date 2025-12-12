package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    public void onLoginClick() {
        System.out.println("=== LOGIN PRESSED ===");
        System.out.println("Email: " + emailField.getText());
        System.out.println("Password: " + passwordField.getText());
    }
}
