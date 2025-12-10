package com.yourapp.controller;

import com.yourapp.database.DatabaseConnection;
import com.yourapp.database.DatabaseSetup;
import com.yourapp.model.User;
import com.yourapp.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Login page.
 * Handles user authentication and session management.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database if not already done
        if (DatabaseConnection.testConnection()) {
            DatabaseSetup.setupDatabase();
        }
    }

    @FXML
    public void onLoginClick() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur de connexion", "Veuillez remplir tous les champs.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur de connexion", "Adresse email invalide.");
            return;
        }

        // Authenticate user
        User user = UserService.authenticateUser(email, password);

        if (user != null) {
            // Set current user in session
            UserService.setCurrentUser(user);
            System.out.println("Login successful for: " + user.getEmail());
            
            // TODO: Navigate to main dashboard/application window
            showAlert("Connexion réussie", "Bienvenue, " + user.getName() + "!");
        } else {
            showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");
            passwordField.clear();
        }
    }

    /**
     * Validates email address format.
     * @param email email address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Shows an alert dialog.
     * @param title alert title
     * @param content alert content
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
