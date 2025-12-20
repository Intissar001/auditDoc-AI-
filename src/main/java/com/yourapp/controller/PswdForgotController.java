package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class PswdForgotController {

    @FXML
    private TextField emailField;

    @FXML
    private Button sendCodeButton;

    @FXML
    private Hyperlink motDePasseProblemLink;

    @FXML
    private Hyperlink creerCompteLink;

    @FXML
    private Button retourConnexionButton;  // Renommé pour plus de clarté

    @FXML
    public void initialize() {
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        sendCodeButton.setOnAction(event -> handleSendCode());
        motDePasseProblemLink.setOnAction(event -> handleMotDePasseProblem());
        creerCompteLink.setOnAction(event -> handleCreerCompte());
        retourConnexionButton.setOnAction(event -> handleRetourConnexion());
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre adresse email!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Email invalide!");
            return;
        }

        System.out.println("Envoi du code de récupération à : " + email);
        showAlert("Succès", "Un code de connexion a été envoyé à votre adresse email.");
    }

    @FXML
    private void handleMotDePasseProblem() {
        System.out.println("Aide supplémentaire pour la récupération de mot de passe...");
        showAlert("Aide", "Pour plus d'assistance, veuillez contacter le support.");
    }

    @FXML
    private void handleCreerCompte() {
        try {
            // Charger la page de création de compte
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/signup.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) creerCompteLink.getScene().getWindow();

            // Créer une nouvelle scène avec la page d'inscription
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Créer un Compte");
            stage.show();

            System.out.println("Redirection vers la page de création de compte...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de création de compte.");
        }
    }

    @FXML
    private void handleRetourConnexion() {
        try {
            // Charger la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/login.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) retourConnexionButton.getScene().getWindow();

            // Créer une nouvelle scène avec la page de connexion
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Connexion");
            stage.show();

            System.out.println("Retour à la page de connexion...");
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
}
