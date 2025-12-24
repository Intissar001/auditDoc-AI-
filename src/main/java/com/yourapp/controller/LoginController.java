package com.yourapp.controller;

import com.yourapp.dto.AuthResponseDto;
import com.yourapp.dto.LoginRequestDto;
import com.yourapp.model.User;
import com.yourapp.services.AuthenticationService;
import com.yourapp.services.UserService;
import com.yourapp.utils.SessionManager;
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
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button connexionButton;
    @FXML private Hyperlink creerCompteLink;
    @FXML private Hyperlink motDePasseOublieLink;

    // 🔐 NEW: Inject authentication service
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        setupEventHandlers();
        System.out.println("✅ LoginController initialized with authentication service");
    }

    private void setupEventHandlers() {
        connexionButton.setOnAction(event -> handleConnexion());
        creerCompteLink.setOnAction(event -> handleCreerCompte());
        motDePasseOublieLink.setOnAction(event -> handleMotDePasseOublie());
    }

    @FXML
    private void handleConnexion() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email invalide!");
            return;
        }

        // 🔐 NEW: Call authentication service
        connexionButton.setDisable(true);
        connexionButton.setText("Connexion en cours...");

        // Run authentication in background thread
        new Thread(() -> {
            try {
                LoginRequestDto request = new LoginRequestDto(email, password);
                AuthResponseDto response = authenticationService.login(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    connexionButton.setDisable(false);
                    connexionButton.setText("Se connecter");

                    if (response.isSuccess()) {
                        // Get full user details
                        User user = userService.getUserById(response.getUserId());

                        // Store user in session
                        SessionManager.getInstance().setCurrentUser(user);

                        System.out.println("✅ Login successful: " + email);

                        // Navigate to main application
                        navigateToMainApp();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Échec de connexion", response.getMessage());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    connexionButton.setDisable(false);
                    connexionButton.setText("Se connecter");
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur s'est produite lors de la connexion");
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void navigateToMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/MainLayout.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Get the main controller and initialize
            MainLayoutController mainController = loader.getController();
            mainController.setSpringContext(springContext);
            mainController.loadView("Dashboard.fxml");

            Stage stage = (Stage) connexionButton.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Dashboard");
            stage.show();

            System.out.println("✅ Navigated to main application");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger l'application principale");
        }
    }

    @FXML
    private void handleCreerCompte() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/fxml/signup.fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage stage = (Stage) creerCompteLink.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Créer un compte");
            stage.show();

            System.out.println("Redirection vers la page de création de compte...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de création de compte.");
        }
    }

    @FXML
    private void handleMotDePasseOublie() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/fxml/pswdforgot.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) motDePasseOublieLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Audit Doc AI - Mot de passe oublié");
            stage.show();

            System.out.println("Redirection vers la page de récupération de mot de passe...");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de récupération de mot de passe.");
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
        emailField.clear();
        passwordField.clear();
    }
}