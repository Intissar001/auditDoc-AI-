package com.yourapp.controller;

import com.yourapp.dto.AuthResponseDto;
import com.yourapp.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordController {

    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    private void handleResetPassword() {
        String token = codeField.getText().trim();
        String password = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (token.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            // FIX: The service returns AuthResponseDto, not a boolean
            AuthResponseDto response = authenticationService.resetPassword(token, password);

            // We check the message or a success flag within the DTO if you have one.
            // Usually, if it doesn't throw an exception, it's successful.
            showAlert(Alert.AlertType.INFORMATION, "Succès", response.getMessage());
            handleBackToLogin();

        } catch (Exception e) {
            // If the service throws an exception (e.g., token invalid/expired)
            showAlert(Alert.AlertType.ERROR, "Erreur", "Code invalide ou expiré.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}