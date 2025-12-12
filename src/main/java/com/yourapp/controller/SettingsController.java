package com.yourapp.controller;

import com.yourapp.model.User;
import com.yourapp.service.AuditTemplateService;
import com.yourapp.service.SettingsService;
import com.yourapp.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller for the Settings page.
 * Manages user profile, audit templates, and system settings.
 * Designed to match Figma design exactly.
 */
public class SettingsController implements Initializable {

    // FXML UI Elements - User Profile
    @FXML private Label userNameLabel;
    @FXML private Label userNameDisplayLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label userRoleLabel;
    
    // FXML UI Elements - Templates
    @FXML private VBox templatesContainer;
    
    // FXML UI Elements - System Settings
    @FXML private ToggleButton localStoreToggle;
    @FXML private ToggleButton cloudBackupToggle;
    @FXML private ToggleButton emailAlertToggle;
    @FXML private ToggleButton auditReminderToggle;
    @FXML private Label ocrProviderLabel;

    private User currentUser;
    private SettingsService.SystemSettings systemSettings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Load current user from session
            currentUser = UserService.getCurrentUser();
            
            // If no user in session, try to load default user (for testing)
            if (currentUser == null) {
                currentUser = UserService.getUserByEmail("boudifatima450@gmail.com");
                if (currentUser != null) {
                    UserService.setCurrentUser(currentUser);
                } else {
                    // Fallback for testing
                    currentUser = new User("Admin", "boudifatima450@gmail.com", "Administrateur");
                }
            }

            setupUserProfile();
            setupSystemSettings();
            setupTemplates();
            setupToggleButtons();
        } catch (Exception e) {
            System.err.println("Error initializing SettingsController: " + e.getMessage());
            e.printStackTrace();
            // Continue with default values to prevent complete failure
            if (currentUser == null) {
                currentUser = new User("Admin", "admin@example.com", "Administrateur");
            }
        }
    }

    /**
     * Sets up the user profile section with current user data.
     */
    private void setupUserProfile() {
        try {
            if (currentUser != null) {
                // Set user initial (first letter of name)
                String initial = currentUser.getName() != null && !currentUser.getName().isEmpty() 
                    ? currentUser.getName().substring(0, 1).toUpperCase() 
                    : "U";
                if (userNameLabel != null) {
                    userNameLabel.setText(initial);
                }
                
                // Set user name display
                if (userNameDisplayLabel != null) {
                    userNameDisplayLabel.setText(currentUser.getName() != null ? currentUser.getName() : "Utilisateur");
                }
                if (userEmailLabel != null) {
                    userEmailLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
                }
                if (userRoleLabel != null) {
                    userRoleLabel.setText(currentUser.getRole() != null ? currentUser.getRole() : "");
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up user profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays audit templates from database.
     */
    private void setupTemplates() {
        try {
            if (templatesContainer == null) {
                System.err.println("Warning: templatesContainer is null");
                return;
            }
            templatesContainer.getChildren().clear();
            List<AuditTemplateService.AuditTemplate> templates = AuditTemplateService.getAllTemplates();
            
            for (AuditTemplateService.AuditTemplate template : templates) {
                VBox templateCard = createTemplateCard(template);
                templatesContainer.getChildren().add(templateCard);
            }
        } catch (Exception e) {
            System.err.println("Error setting up templates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a template card UI element.
     */
    private VBox createTemplateCard(AuditTemplateService.AuditTemplate template) {
        VBox card = new VBox(8);
        card.getStyleClass().add("template-card");
        card.setPadding(new Insets(16, 20, 16, 20));

        // Title
        Label titleLabel = new Label(template.getName());
        titleLabel.getStyleClass().add("template-title");

        // Organization/Subtitle
        Label orgLabel = new Label(template.getOrganization());
        orgLabel.getStyleClass().add("template-description");

        // Description
        if (template.getDescription() != null && !template.getDescription().isEmpty()) {
            Label descLabel = new Label(template.getDescription());
            descLabel.getStyleClass().add("template-description");
            card.getChildren().add(descLabel);
        }

        // Bottom row with badge and action buttons
        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // Rule count badge
        Label badgeLabel = new Label(template.getRuleCount() + " règles");
        badgeLabel.getStyleClass().add("template-badge");

        // Action buttons (Edit and Delete)
        HBox actionsBox = new HBox(8);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actionsBox, javafx.scene.layout.Priority.ALWAYS);

        Button editBtn = new Button("✎");
        editBtn.getStyleClass().add("template-action-btn");
        editBtn.setOnAction(e -> handleEditTemplate(template));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("template-action-btn");
        deleteBtn.setOnAction(e -> handleDeleteTemplate(template));

        actionsBox.getChildren().addAll(editBtn, deleteBtn);
        bottomRow.getChildren().addAll(badgeLabel, actionsBox);

        card.getChildren().addAll(titleLabel, orgLabel, bottomRow);
        return card;
    }

    /**
     * Handles template edit action.
     */
    private void handleEditTemplate(AuditTemplateService.AuditTemplate template) {
        // TODO: Implement edit template dialog
        showAlert("Information", "Fonctionnalité d'édition à venir.", Alert.AlertType.INFORMATION);
    }

    /**
     * Handles template delete action.
     */
    private void handleDeleteTemplate(AuditTemplateService.AuditTemplate template) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le modèle '" + template.getName() + "' ?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = AuditTemplateService.deleteTemplate(template.getId());
                if (success) {
                    showAlert("Succès", "Modèle supprimé avec succès.", Alert.AlertType.INFORMATION);
                    setupTemplates(); // Refresh templates list
                } else {
                    showAlert("Erreur", "Impossible de supprimer le modèle.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Loads and displays system settings from database.
     */
    private void setupSystemSettings() {
        try {
            systemSettings = SettingsService.loadSettings();
            
            if (systemSettings != null) {
                if (localStoreToggle != null) {
                    localStoreToggle.setSelected(systemSettings.isLocalStorage());
                }
                if (cloudBackupToggle != null) {
                    cloudBackupToggle.setSelected(systemSettings.isCloudBackup());
                }
                if (emailAlertToggle != null) {
                    emailAlertToggle.setSelected(systemSettings.isEmailAlerts());
                }
                if (auditReminderToggle != null) {
                    auditReminderToggle.setSelected(systemSettings.isAuditReminders());
                }
                if (ocrProviderLabel != null) {
                    ocrProviderLabel.setText(systemSettings.getOcrProvider() != null ? systemSettings.getOcrProvider() : "Intégré");
                }
            } else {
                // Default values if loading fails
                if (localStoreToggle != null) {
                    localStoreToggle.setSelected(true);
                }
                if (cloudBackupToggle != null) {
                    cloudBackupToggle.setSelected(false);
                }
                if (emailAlertToggle != null) {
                    emailAlertToggle.setSelected(true);
                }
                if (auditReminderToggle != null) {
                    auditReminderToggle.setSelected(true);
                }
                if (ocrProviderLabel != null) {
                    ocrProviderLabel.setText("Intégré");
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up system settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up toggle button styling and behavior.
     */
    private void setupToggleButtons() {
        // Add listeners to update toggle button state
        localStoreToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != oldVal) {
                saveSettings();
            }
        });
        
        cloudBackupToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != oldVal) {
                saveSettings();
            }
        });
        
        emailAlertToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != oldVal) {
                saveSettings();
            }
        });
        
        auditReminderToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != oldVal) {
                saveSettings();
            }
        });
    }

    /**
     * Handles the invite user button action.
     * Opens a dialog to invite a new user matching Figma design.
     */
    @FXML
    private void handleInviteUser() {
        Dialog<VBox> dialog = new Dialog<>();
        dialog.setTitle("Inviter un Utilisateur");
        dialog.setHeaderText(null);

        // Set button types
        ButtonType sendButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, cancelButtonType);

        // Create form fields
        TextField emailField = new TextField();
        emailField.setPromptText("utilisateur@example.com");
        emailField.setPrefWidth(400);
        emailField.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Administrateur", "Chargé de Projet", "Lecteur");
        roleComboBox.setPromptText("Sélectionner le Statut");
        roleComboBox.setPrefWidth(400);
        roleComboBox.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

        ComboBox<String> projectComboBox = new ComboBox<>();
        projectComboBox.getItems().addAll("Tous les projets", "Projet A", "Projet B", "Projet C");
        projectComboBox.setPromptText("Sélectionner le Projet");
        projectComboBox.setPrefWidth(400);
        projectComboBox.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

        VBox form = new VBox(16);
        form.setPadding(new Insets(20));
        form.getChildren().addAll(
            new Label("Email:"),
            emailField,
            new Label("Rôle:"),
            roleComboBox,
            new Label("Accès au projet:"),
            projectComboBox
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        // Enable/Disable send button based on validation
        Button sendButton = (Button) dialog.getDialogPane().lookupButton(sendButtonType);
        sendButton.setDisable(true);

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateInviteButtonState(sendButton, emailField, roleComboBox);
        });

        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateInviteButtonState(sendButton, emailField, roleComboBox);
        });

        // Request focus on email field
        dialog.setOnShown(e -> emailField.requestFocus());

        // Convert result when send button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                String email = emailField.getText().trim();
                String role = roleComboBox.getValue();
                String project = projectComboBox.getValue();
                
                if (isValidEmail(email) && role != null) {
                    // TODO: Implement actual invitation logic
                    System.out.println("Invitation sent to: " + email + " with role: " + role + " and project: " + project);
                    showAlert("Succès", "Invitation envoyée à " + email + ".", Alert.AlertType.INFORMATION);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Updates the invite button state based on form validation.
     */
    private void updateInviteButtonState(Button button, TextField emailField, ComboBox<String> roleComboBox) {
        boolean isValid = isValidEmail(emailField.getText()) && roleComboBox.getValue() != null;
        button.setDisable(!isValid);
    }

    /**
     * Handles the create audit model button action.
     * Opens a dialog to create a new audit template.
     */
    @FXML
    private void handleCreateModel() {
        Dialog<VBox> dialog = new Dialog<>();
        dialog.setTitle("Créer un Modèle d'Audit");
        dialog.setHeaderText("Entrez les informations du nouveau modèle");

        // Set button types
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Nom du modèle");
        nameField.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");
        
        TextField orgField = new TextField();
        orgField.setPromptText("Organisation");
        orgField.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");
        
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        descField.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.getChildren().addAll(
            new Label("Nom:"), nameField,
            new Label("Organisation:"), orgField,
            new Label("Description:"), descField
        );
        dialog.getDialogPane().setContent(form);

        // Enable/Disable create button based on validation
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue == null || newValue.trim().isEmpty());
        });

        // Request focus on name field
        dialog.setOnShown(e -> nameField.requestFocus());

        // Convert result when create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String name = nameField.getText().trim();
                String org = orgField.getText().trim();
                String desc = descField.getText().trim();
                
                if (!name.isEmpty()) {
                    boolean success = AuditTemplateService.createTemplate(name, desc, org);
                    if (success) {
                        showAlert("Succès", "Modèle d'audit '" + name + "' créé avec succès.", Alert.AlertType.INFORMATION);
                        setupTemplates(); // Refresh templates list
                    } else {
                        showAlert("Erreur", "Impossible de créer le modèle.", Alert.AlertType.ERROR);
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Saves system settings to database.
     * Called when any toggle button is changed.
     */
    @FXML
    private void saveSettings() {
        boolean localStorage = localStoreToggle.isSelected();
        boolean cloudBackup = cloudBackupToggle.isSelected();
        boolean emailAlerts = emailAlertToggle.isSelected();
        boolean auditReminders = auditReminderToggle.isSelected();

        // Validate settings
        if (!localStorage && !cloudBackup) {
            showAlert("Avertissement", 
                "Au moins une option de stockage doit être activée. " +
                "Le stockage local sera activé automatiquement.", 
                Alert.AlertType.WARNING);
            localStoreToggle.setSelected(true);
            localStorage = true;
        }

        // Save to database
        boolean success = SettingsService.saveBasicSettings(localStorage, cloudBackup, emailAlerts, auditReminders);
        
        if (success) {
            // Update local settings object
            if (systemSettings != null) {
                systemSettings.setLocalStorage(localStorage);
                systemSettings.setCloudBackup(cloudBackup);
                systemSettings.setEmailAlerts(emailAlerts);
                systemSettings.setAuditReminders(auditReminders);
            }
            System.out.println("Settings saved: Local=" + localStorage + ", Cloud=" + cloudBackup + 
                             ", Email=" + emailAlerts + ", Reminders=" + auditReminders);
        } else {
            showAlert("Erreur", "Impossible de sauvegarder les paramètres. Veuillez réessayer.", Alert.AlertType.ERROR);
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
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Shows an alert dialog.
     * @param title alert title
     * @param content alert content
     * @param alertType type of alert
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
