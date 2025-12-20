package com.yourapp.controller;

import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.UserSettingsDTO;
import com.yourapp.model.User;
import com.yourapp.service.SettingsClient;
import com.yourapp.service.UserClient;
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

    // ===== User Profile =====
    @FXML private Label userNameLabel;
    @FXML private Label userNameDisplayLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label userRoleLabel;

    // ===== Templates =====
    @FXML private VBox templatesContainer;

    // ===== System Settings =====
    @FXML private ToggleButton emailAlertToggle;
    @FXML private ToggleButton auditReminderToggle;

    private User currentUser;
    private Long currentUserId = 1L;
    private boolean initializing = true;

    // ===================== INIT =====================

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // ===================== LOAD DATA =====================

    private void loadCurrentUser() {
        currentUser = UserClient.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            currentUserId = currentUser.getId();
        }
    }

    private void loadUserSettings() {
        if (emailAlertToggle == null || auditReminderToggle == null) return;

        UserSettingsDTO settings = SettingsClient.getUserSettings(currentUserId);

        if (settings != null) {
            emailAlertToggle.setSelected(settings.getEmailAlerts());
            auditReminderToggle.setSelected(settings.getAuditReminders());
        } else {
            emailAlertToggle.setSelected(true);
            auditReminderToggle.setSelected(true);
        }
    }

    private void loadTemplates() {
        if (templatesContainer == null) return;

        templatesContainer.getChildren().clear();
        List<AuditTemplateDTO> templates = SettingsClient.getAllTemplates();

        if (templates == null || templates.isEmpty()) {
            Label emptyLabel = new Label("Aucun modèle disponible");
            emptyLabel.getStyleClass().add("empty-state-label");
            templatesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (AuditTemplateDTO template : templates) {
            templatesContainer.getChildren().add(createTemplateCard(template));
        }
    }

    // ===================== TEMPLATE UI =====================

    private VBox createTemplateCard(AuditTemplateDTO template) {
        VBox card = new VBox(8);
        card.getStyleClass().add("template-card");
        card.setPadding(new Insets(16, 20, 16, 20));

        Label title = new Label(template.getName());
        title.getStyleClass().add("template-title");

        Label org = new Label(template.getOrganization());
        org.getStyleClass().add("template-description");

        Label desc = null;
        if (template.getDescription() != null && !template.getDescription().isBlank()) {
            desc = new Label(template.getDescription());
            desc.getStyleClass().add("template-description");
        }

        Label badge = new Label(template.getRuleCount() + " règles");
        badge.getStyleClass().add("template-badge");

        Button editBtn = new Button("✎");
        editBtn.getStyleClass().add("template-action-btn");
        editBtn.setOnAction(e -> handleEditTemplate(template));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("template-action-btn");
        deleteBtn.setOnAction(e -> handleDeleteTemplate(template));

        HBox actions = new HBox(8, editBtn, deleteBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actions, javafx.scene.layout.Priority.ALWAYS);

        HBox bottom = new HBox(8, badge, actions);
        bottom.setAlignment(Pos.CENTER_LEFT);

        if (desc != null) {
            card.getChildren().addAll(title, org, desc, bottom);
        } else {
            card.getChildren().addAll(title, org, bottom);
        }

        return card;
    }

    // ===================== PROFILE =====================

    private void setupUserProfile() {
        if (currentUser == null) return;

        String initial = (currentUser.getName() != null && !currentUser.getName().isBlank())
                ? currentUser.getName().substring(0, 1).toUpperCase()
                : "U";

        userNameLabel.setText(initial);
        userNameDisplayLabel.setText(currentUser.getName());
        userEmailLabel.setText(currentUser.getEmail());
        userRoleLabel.setText(
                currentUser.getRole() != null
                        ? currentUser.getRole().toUpperCase()
                        : "USER"
        );
    }

    // ===================== TOGGLES =====================

    private void setupToggleButtons() {
        if (emailAlertToggle == null || auditReminderToggle == null) return;

        emailAlertToggle.selectedProperty().addListener((o, oldV, newV) -> saveSettings());
        auditReminderToggle.selectedProperty().addListener((o, oldV, newV) -> saveSettings());
    }

    // ===================== ACTIONS =====================

    private void handleEditTemplate(AuditTemplateDTO template) {
        showAlert("Information", "Fonctionnalité d'édition à venir.", Alert.AlertType.INFORMATION);
    }

    private void handleDeleteTemplate(AuditTemplateDTO template) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Supprimer le modèle '" + template.getName() + "' ?");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean success = SettingsClient.deleteTemplate(template.getId());
                if (success) {
                    loadTemplates();
                    showAlert("Succès", "Modèle supprimé.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Suppression échouée.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleCreateModel() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Créer un Modèle d'Audit");
        dialog.getDialogPane().getStyleClass().add("invite-user-dialog");

        ButtonType createBtnType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        Label nameLabel = new Label("Nom");
        nameLabel.getStyleClass().add("dialog-label");

        TextField name = new TextField();
        name.setPromptText("Nom du modèle");
        name.getStyleClass().add("dialog-textfield");
        name.setPrefWidth(400);

        Label orgLabel = new Label("Organisation");
        orgLabel.getStyleClass().add("dialog-label");

        TextField org = new TextField();
        org.setPromptText("Organisation");
        org.getStyleClass().add("dialog-textfield");
        org.setPrefWidth(400);

        Label descLabel = new Label("Description");
        descLabel.getStyleClass().add("dialog-label");

        TextArea desc = new TextArea();
        desc.setPromptText("Description");
        desc.setPrefRowCount(3);
        desc.getStyleClass().add("dialog-textarea");
        desc.setPrefWidth(400);

        VBox box = new VBox(12, nameLabel, name, orgLabel, org, descLabel, desc);
        box.setPadding(new Insets(24));
        box.getStyleClass().add("invite-user-form");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(450);

        Button createBtn = (Button) dialog.getDialogPane().lookupButton(createBtnType);
        createBtn.getStyleClass().add("btn-send");
        createBtn.setDisable(true);

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.getStyleClass().add("btn-cancel");

        name.textProperty().addListener((o, oldV, newV) ->
                createBtn.setDisable(newV.trim().isEmpty())
        );

        dialog.setOnShown(e -> name.requestFocus());

        dialog.setResultConverter(btn -> {
            if (btn == createBtnType) {
                boolean success = SettingsClient.createTemplate(
                        name.getText().trim(),
                        org.getText().trim(),
                        desc.getText().trim()
                );

                if (success) {
                    loadTemplates();
                    showAlert("Succès", "Modèle créé.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Création échouée.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleInviteUser() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Inviter un Utilisateur");
        dialog.getDialogPane().getStyleClass().add("invite-user-dialog");

        ButtonType sendBtnType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendBtnType, ButtonType.CANCEL);

        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("dialog-label");

        TextField emailField = new TextField();
        emailField.setPromptText("utilisateur@example.com");
        emailField.getStyleClass().add("dialog-textfield");
        emailField.setPrefWidth(400);

        Label roleLabel = new Label("Rôle");
        roleLabel.getStyleClass().add("dialog-label");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Administrateur", "Chargé de Projet", "Lecteur");
        roleCombo.setPromptText("Sélectionner le Statut");
        roleCombo.getStyleClass().add("dialog-combobox");
        roleCombo.setPrefWidth(400);

        Label projectLabel = new Label("Accès au projet");
        projectLabel.getStyleClass().add("dialog-label");

        ComboBox<String> projectCombo = new ComboBox<>();
        projectCombo.getItems().addAll("Tous les projets", "Projet A", "Projet B");
        projectCombo.setPromptText("Sélectionner le Projet");
        projectCombo.getStyleClass().add("dialog-combobox");
        projectCombo.setPrefWidth(400);

        VBox form = new VBox(12, emailLabel, emailField, roleLabel, roleCombo,
                projectLabel, projectCombo);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("invite-user-form");
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        Button sendBtn = (Button) dialog.getDialogPane().lookupButton(sendBtnType);
        sendBtn.getStyleClass().add("btn-send");
        sendBtn.setDisable(true);

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.getStyleClass().add("btn-cancel");

        emailField.textProperty().addListener((o, oldV, newV) ->
                sendBtn.setDisable(!isValidEmail(newV) || roleCombo.getValue() == null)
        );

        roleCombo.valueProperty().addListener((o, oldV, newV) ->
                sendBtn.setDisable(!isValidEmail(emailField.getText()) || newV == null)
        );

        dialog.setOnShown(e -> emailField.requestFocus());

        dialog.setResultConverter(btn -> {
            if (btn == sendBtnType) {
                showAlert("Succès", "Invitation envoyée à " + emailField.getText(),
                        Alert.AlertType.INFORMATION);
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ===================== SAVE SETTINGS =====================

    @FXML
    private void saveSettings() {
        if (initializing) return;

        boolean success = SettingsClient.updateUserSettings(
                currentUserId,
                emailAlertToggle.isSelected(),
                auditReminderToggle.isSelected()
        );

        System.out.println(success ? "✅ Settings saved" : "❌ Settings save failed");
    }

    // ===================== UTILS =====================

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void refresh() {
        try {
            loadCurrentUser();
            setupUserProfile();
            loadUserSettings();
            setupToggleButtons();
            loadTemplates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
