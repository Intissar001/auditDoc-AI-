package com.yourapp.controller;

import com.yourapp.dto.AuthResponseDto;
import com.yourapp.dto.SignUpRequestDto;
import com.yourapp.services.AuthenticationService;
import javafx.application.Platform;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
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

    // Inject authentication service
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        setupEventHandlers();
        System.out.println("✅ SignUpController initialized with authentication service");
    }

    private void setupEventHandlers() {
        creerCompteButton.setOnAction(event -> handleCreerCompte());
        dejaCompteLink.setOnAction(event -> handleDejaCompte());
    }

    @FXML
    private void handleCreerCompte() {
        String nom = nomField.getText().trim();
        String association = associationField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le nom,l'email et le mot de passe sont obligatoires!");
            return;
        }
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email invalide!");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le mot de passe doit contenir au moins 8 caractères!");
            return;
        }

        // Password strength validation
        if (!authenticationService.isPasswordStrong(password)) {
            showAlert(Alert.AlertType.WARNING, "Mot de passe faible",
                    "Le mot de passe doit contenir au moins:\n" +
                            "- Une lettre majuscule\n" +
                            "- Une lettre minuscule\n" +
                            "- Un chiffre");
            return;// warn user and dont allow to continue
        }

        // Call authentication service
        creerCompteButton.setDisable(true);
        creerCompteButton.setText("Création en cours...");

        // Run registration in background thread
        new Thread(() -> {
            try {
                SignUpRequestDto request = new SignUpRequestDto(
                        nom, email, password, association
                );
                AuthResponseDto response = authenticationService.register(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    creerCompteButton.setDisable(false);
                    creerCompteButton.setText("Créer un compte");

                    if (response.isSuccess()) {
                        System.out.println("✅ Registration successful: " + email);

                        showAlert(Alert.AlertType.INFORMATION, "Succès",
                                "Compte créé avec succès!\nVous pouvez maintenant vous connecter.");

                        // Redirect to login
                        redirectToLogin();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Échec de création",
                                response.getMessage());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    creerCompteButton.setDisable(false);
                    creerCompteButton.setText("Créer un compte");
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur s'est produite lors de la création du compte");
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleDejaCompte() {
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage stage = (Stage) creerCompteButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("Redirection vers la page de connexion...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de connexion.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
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