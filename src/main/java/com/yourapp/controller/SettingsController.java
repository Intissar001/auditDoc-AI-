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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    private boolean initializing = true; // 🔐 important

    // ===================== INIT =====================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadCurrentUser();
            setupUserProfile();

            loadUserSettings();
            setupToggleButtons();

            loadTemplates();
        } catch (Exception e) {
            System.err.println("❌ Error initializing SettingsController");
            e.printStackTrace();

            currentUser = new User("Admin", "admin@example.com", "ADMIN");
            setupUserProfile();
        } finally {
            initializing = false;
        }
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

        ButtonType createBtnType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        TextField name = new TextField();
        name.setPromptText("Nom du modèle");

        TextField org = new TextField();
        org.setPromptText("Organisation");

        TextArea desc = new TextArea();
        desc.setPromptText("Description");

        VBox box = new VBox(12, name, org, desc);
        box.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(box);

        Button createBtn = (Button) dialog.getDialogPane().lookupButton(createBtnType);
        createBtn.setDisable(true);

        name.textProperty().addListener((o, oldV, newV) ->
                createBtn.setDisable(newV.trim().isEmpty())
        );

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
        showAlert("Information", "Fonctionnalité d'invitation à venir.", Alert.AlertType.INFORMATION);
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

        System.out.println(success
                ? "✅ Settings saved"
                : "❌ Settings save failed");
    }

    // ===================== UTILS =====================

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
