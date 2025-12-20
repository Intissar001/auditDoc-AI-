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
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
@Component
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
    
    // FXML UI Elements - System Settings (Notifications only)
    @FXML private ToggleButton emailAlertToggle;
    @FXML private ToggleButton auditReminderToggle;

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
                    String role = currentUser.getRole() != null ? currentUser.getRole() : "";
                    // Display role in uppercase to match screenshot design
                    userRoleLabel.setText(role.toUpperCase());
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
     * Creates a template card UI element matching the Figma design exactly.
     */
    private VBox createTemplateCard(AuditTemplateService.AuditTemplate template) {
        VBox card = new VBox(8);
        card.getStyleClass().add("template-card");
        card.setPadding(new Insets(16, 20, 16, 20));

        // Title (e.g., "Template AFD")
        Label titleLabel = new Label(template.getName());
        titleLabel.getStyleClass().add("template-title");

        // Organization/Subtitle (e.g., "Agence Française de Développement")
        Label orgLabel = new Label(template.getOrganization());
        orgLabel.getStyleClass().add("template-description");

        // Description (e.g., "Normes d'audit selon les exigences AFD")
        Label descLabel = null;
        if (template.getDescription() != null && !template.getDescription().isEmpty()) {
            descLabel = new Label(template.getDescription());
            descLabel.getStyleClass().add("template-description");
        }

        // Bottom row with badge and action buttons
        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setSpacing(8);
        HBox.setHgrow(bottomRow, javafx.scene.layout.Priority.ALWAYS);

        // Rule count badge (e.g., "15 règles")
        Label badgeLabel = new Label(template.getRuleCount() + " règles");
        badgeLabel.getStyleClass().add("template-badge");

        // Action buttons (Edit and Delete) on the right
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

        // Add all elements to card
        if (descLabel != null) {
            card.getChildren().addAll(titleLabel, orgLabel, descLabel, bottomRow);
        } else {
            card.getChildren().addAll(titleLabel, orgLabel, bottomRow);
        }
        
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
     * Loads and displays notification settings (in-memory only, no database).
     */
    private void setupSystemSettings() {
        try {
            systemSettings = SettingsService.loadSettings();
            
            if (systemSettings != null) {
                if (emailAlertToggle != null) {
                    emailAlertToggle.setSelected(systemSettings.isEmailAlerts());
                }
                if (auditReminderToggle != null) {
                    auditReminderToggle.setSelected(systemSettings.isAuditReminders());
                }
            } else {
                // Default values
                if (emailAlertToggle != null) {
                    emailAlertToggle.setSelected(true);
                }
                if (auditReminderToggle != null) {
                    auditReminderToggle.setSelected(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up system settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up toggle button styling and behavior (dynamic interactions).
     */
    private void setupToggleButtons() {
        // Add listeners to update toggle button state dynamically
        if (emailAlertToggle != null) {
            emailAlertToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != oldVal) {
                    saveSettings();
                }
            });
        }
        
        if (auditReminderToggle != null) {
            auditReminderToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != oldVal) {
                    saveSettings();
                }
            });
        }
    }

    /**
     * Handles the invite user button action.
     * Opens a dialog to invite a new user matching Figma design exactly.
     */
    @FXML
    private void handleInviteUser() {
        Dialog<VBox> dialog = new Dialog<>();
        dialog.setTitle("Inviter un Utilisateur");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStyleClass().add("invite-user-dialog");

        // Set button types
        ButtonType sendButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, cancelButtonType);

        // Create form fields matching screenshot design
        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("dialog-label");
        
        TextField emailField = new TextField();
        emailField.setPromptText("utilisateur@example.com");
        emailField.getStyleClass().add("dialog-textfield");
        emailField.setPrefWidth(400);

        Label roleLabel = new Label("Rôle");
        roleLabel.getStyleClass().add("dialog-label");
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Administrateur", "Chargé de Projet", "Lecteur");
        roleComboBox.setPromptText("Sélectionner le Statut");
        roleComboBox.getStyleClass().add("dialog-combobox");
        roleComboBox.setPrefWidth(400);

        Label projectLabel = new Label("Accès au projet");
        projectLabel.getStyleClass().add("dialog-label");
        
        ComboBox<String> projectComboBox = new ComboBox<>();
        projectComboBox.getItems().addAll("Tous les projets", "Projet A", "Projet B", "Projet C");
        projectComboBox.setPromptText("Sélectionner le Projet");
        projectComboBox.getStyleClass().add("dialog-combobox");
        projectComboBox.setPrefWidth(400);

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("invite-user-form");
        form.getChildren().addAll(
            emailLabel, emailField,
            roleLabel, roleComboBox,
            projectLabel, projectComboBox
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        // Style buttons dynamically
        Button sendButton = (Button) dialog.getDialogPane().lookupButton(sendButtonType);
        sendButton.getStyleClass().add("btn-send");
        sendButton.setDisable(true);

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("btn-cancel");

        // Dynamic validation
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
     * Opens a dialog matching the Invite User design style.
     */
    @FXML
    private void handleCreateModel() {
        Dialog<VBox> dialog = new Dialog<>();
        dialog.setTitle("Créer un Modèle d'Audit");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStyleClass().add("invite-user-dialog");

        // Set button types
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create input fields matching Invite User dialog style
        Label nameLabel = new Label("Nom");
        nameLabel.getStyleClass().add("dialog-label");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Nom du modèle");
        nameField.getStyleClass().add("dialog-textfield");
        nameField.setPrefWidth(400);
        
        Label orgLabel = new Label("Organisation");
        orgLabel.getStyleClass().add("dialog-label");
        
        TextField orgField = new TextField();
        orgField.setPromptText("Organisation");
        orgField.getStyleClass().add("dialog-textfield");
        orgField.setPrefWidth(400);
        
        Label descLabel = new Label("Description");
        descLabel.getStyleClass().add("dialog-label");
        
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("dialog-textarea");
        descField.setPrefWidth(400);

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("invite-user-form");
        form.getChildren().addAll(
            nameLabel, nameField,
            orgLabel, orgField,
            descLabel, descField
        );
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        // Style buttons to match Invite User dialog
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.getStyleClass().add("btn-send");
        createButton.setDisable(true);

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("btn-cancel");

        // Dynamic validation
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
                        setupTemplates(); // Refresh templates list dynamically
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
     * Saves notification settings (in-memory only, no database).
     * Called when any toggle button is changed (dynamic behavior).
     */
    @FXML
    private void saveSettings() {
        if (emailAlertToggle == null || auditReminderToggle == null) {
            return;
        }
        
        boolean emailAlerts = emailAlertToggle.isSelected();
        boolean auditReminders = auditReminderToggle.isSelected();

        // Save settings (in-memory only)
        boolean success = SettingsService.saveNotificationSettings(emailAlerts, auditReminders);
        
        if (success) {
            // Update local settings object
            if (systemSettings != null) {
                systemSettings.setEmailAlerts(emailAlerts);
                systemSettings.setAuditReminders(auditReminders);
            }
            System.out.println("Notification settings updated: Email=" + emailAlerts + ", Reminders=" + auditReminders);
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
