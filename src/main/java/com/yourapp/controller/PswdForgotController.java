package com.yourapp.controller;

import com.yourapp.dto.AuthResponseDto;
import com.yourapp.dto.PasswordResetRequestDto;
import com.yourapp.services.AuthenticationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PswdForgotController {

    @FXML private TextField emailField;
    @FXML private Button sendCodeButton;
    @FXML private Hyperlink motDePasseProblemLink;
    @FXML private Hyperlink creerCompteLink;
    @FXML private Button retourConnexionButton;

    // 🔐 NEW: Inject authentication service
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        setupEventHandlers();
        System.out.println("✅ PswdForgotController initialized with authentication service");
    }

    private void setupEventHandlers() {
        sendCodeButton.setOnAction(event -> handleSendCode());
        motDePasseProblemLink.setOnAction(event -> handleMotDePasseProblem());
        creerCompteLink.setOnAction(event -> handleCreerCompte());
        retourConnexionButton.setOnAction(event -> handleRetourConnexion());
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer votre adresse email!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email invalide!");
            return;
        }

        // 🔐 NEW: Call authentication service
        sendCodeButton.setDisable(true);
        sendCodeButton.setText("Envoi en cours...");

        // Run password reset request in background thread
        new Thread(() -> {
            try {
                PasswordResetRequestDto request = new PasswordResetRequestDto(email);
                AuthResponseDto response = authenticationService.requestPasswordReset(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    sendCodeButton.setDisable(false);
                    sendCodeButton.setText("Envoyer le code de connexion");

                    // Always show success for security (don't reveal if email exists)
                    showAlert(Alert.AlertType.INFORMATION, "Email envoyé", response.getMessage());

                    System.out.println("✅ Password reset email sent to: " + email);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    sendCodeButton.setDisable(false);
                    sendCodeButton.setText("Envoyer le code de connexion");
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur s'est produite lors de l'envoi du code");
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleMotDePasseProblem() {
        showAlert(Alert.AlertType.INFORMATION, "Aide",
                "Pour plus d'assistance, veuillez contacter le support à:\n" +
                        "support_auditdocai@gmail.com");
    }

    @FXML
    private void handleCreerCompte() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/signup.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) creerCompteLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Créer un Compte");
            stage.show();

            System.out.println("Redirection vers la page de création de compte...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de création de compte.");
        }
    }

    @FXML
    private void handleRetourConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/login.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) retourConnexionButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Connexion");
            stage.show();

            System.out.println("Retour à la page de connexion...");
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
}