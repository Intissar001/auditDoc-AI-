package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField associationField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button creerCompteButton;

    @FXML
    private Hyperlink dejaCompteLink;

    @FXML
    public void initialize() {
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        creerCompteButton.setOnAction(event -> handleCreerCompte());
        dejaCompteLink.setOnAction(event -> handleDejaCompte());
    }

    @FXML
    private void handleCreerCompte() {
        String nom = nomField.getText();
        String association = associationField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (nom.isEmpty() || association.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Email invalide!");
            return;
        }

        System.out.println("Création du compte...");
        System.out.println("Nom: " + nom);
        System.out.println("Association: " + association);
        System.out.println("Email: " + email);

        // Afficher le message de succès
        showAlert("Succès", "Compte créé avec succès!");

        // Rediriger vers la page de login
        redirectToLogin();
    }

    @FXML
    private void handleDejaCompte() {
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            // Charger la page de login
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/login.fxml"));

            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) creerCompteButton.getScene().getWindow();

            // Créer et définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("Redirection vers la page de connexion...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearFields() {
        nomField.clear();
        associationField.clear();
        emailField.clear();
        passwordField.clear();
    }
}