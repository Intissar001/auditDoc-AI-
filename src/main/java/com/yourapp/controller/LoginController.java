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
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button connexionButton;

    @FXML
    private Hyperlink creerCompteLink;

    @FXML
    private Hyperlink motDePasseOublieLink;

    @FXML
    public void initialize() {
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        connexionButton.setOnAction(event -> handleConnexion());
        creerCompteLink.setOnAction(event -> handleCreerCompte());
        motDePasseOublieLink.setOnAction(event -> handleMotDePasseOublie());
    }

    @FXML
    private void handleConnexion() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Email invalide!");
            return;
        }

        System.out.println("Connexion...");
        System.out.println("Email: " + email);

        showAlert("Succès", "Connexion réussie!");
    }

    @FXML
    private void handleCreerCompte() {
        try {
            // Charger la page de création de compte
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/signup.fxml"));

            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) creerCompteLink.getScene().getWindow();

            // Créer et définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Créer un compte");
            stage.show();

            System.out.println("Redirection vers la page de création de compte...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de création de compte.");
        }
    }

    @FXML
    private void handleMotDePasseOublie() {
        try {
            // Charger la page PswdForgot
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/pswdforgot.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) motDePasseOublieLink.getScene().getWindow();

            // Créer une nouvelle scène avec la page PswdForgot
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Mot de passe oublié");
            stage.show();

            System.out.println("Redirection vers la page de récupération de mot de passe...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de récupération de mot de passe.");
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
        emailField.clear();
        passwordField.clear();
    }
}
