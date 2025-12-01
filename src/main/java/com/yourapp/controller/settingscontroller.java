package com.yourapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.yourapp.model.User;
import com.yourapp.database.DatabaseConnection;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.scene.control.Alert.AlertType;

public class settingscontroller implements Initializable {

    // ============ FXML Components ============
    @FXML private TabPane settingsTabPane;

    // System Settings Tab
    @FXML private TextField appNameField;
    @FXML private TextField appVersionField;
    @FXML private TextField adminEmailField;
    @FXML private TextField auditRetentionField;
    @FXML private Button saveSystemBtn;

    // User Management Tab
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserEmail;
    @FXML private TableColumn<User, String> colUserRole;
    @FXML private TextField inviteEmailField;
    @FXML private ComboBox<String> inviteRoleCombo;
    @FXML private Button inviteUserBtn;

    // Role Management Tab
    @FXML private ListView<String> roleListView;
    @FXML private CheckBox permAuditCreate;
    @FXML private CheckBox permAuditView;
    @FXML private CheckBox permAuditEdit;
    @FXML private CheckBox permAuditDelete;
    @FXML private CheckBox permUserManage;
    @FXML private CheckBox permSettings;
    @FXML private Button saveRoleBtn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private Connection conn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            conn = DatabaseConnection.getConnection();
            initializeDatabase();
            loadSystemSettings();
            loadUsers();
            loadRoles();
            setupListeners();
        } catch (Exception e) {
            showError("Erreur d'initialisation", e.getMessage());
        }
    }

    // ============ DATABASE INITIALIZATION ============
    private void initializeDatabase() throws SQLException {
        Statement stmt = conn.createStatement();

        // Create settings table
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS settings (" +
                        "setting_key VARCHAR(50) PRIMARY KEY, " +
                        "setting_value TEXT, " +
                        "category VARCHAR(30))"
        );

        // Create users table
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "email VARCHAR(100) UNIQUE, " +
                        "role VARCHAR(50), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        );

        // Create roles table
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS roles (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(50) UNIQUE, " +
                        "permissions TEXT)"
        );

        // Insert default settings if not exists
        insertDefaultSettings();
        insertDefaultRoles();

        stmt.close();
    }

    private void insertDefaultSettings() throws SQLException {
        String[] defaults = {
                "INSERT IGNORE INTO settings VALUES ('app_name', 'AuditDoc AI', 'system')",
                "INSERT IGNORE INTO settings VALUES ('app_version', '1.0.0', 'system')",
                "INSERT IGNORE INTO settings VALUES ('admin_email', 'admin@audit.com', 'system')",
                "INSERT IGNORE INTO settings VALUES ('audit_retention', '90', 'audit')"
        };

        Statement stmt = conn.createStatement();
        for (String sql : defaults) {
            stmt.execute(sql);
        }
        stmt.close();
    }

    private void insertDefaultRoles() throws SQLException {
        String[] roles = {
                "INSERT IGNORE INTO roles (name, permissions) VALUES " +
                        "('Administrateur', '{\"audit_create\":true,\"audit_view\":true,\"audit_edit\":true,\"audit_delete\":true,\"user_manage\":true,\"settings\":true}')",

                "INSERT IGNORE INTO roles (name, permissions) VALUES " +
                        "('Chargé de Projet', '{\"audit_create\":true,\"audit_view\":true,\"audit_edit\":true,\"audit_delete\":false,\"user_manage\":false,\"settings\":false}')",

                "INSERT IGNORE INTO roles (name, permissions) VALUES " +
                        "('Lecteur', '{\"audit_create\":false,\"audit_view\":true,\"audit_edit\":false,\"audit_delete\":false,\"user_manage\":false,\"settings\":false}')"
        };

        Statement stmt = conn.createStatement();
        for (String sql : roles) {
            stmt.execute(sql);
        }
        stmt.close();
    }

    // ============ LOAD DATA ============
    private void loadSystemSettings() {
        try {
            String sql = "SELECT setting_key, setting_value FROM settings WHERE category = 'system' OR category = 'audit'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String key = rs.getString("setting_key");
                String value = rs.getString("setting_value");

                switch (key) {
                    case "app_name":
                        appNameField.setText(value);
                        break;
                    case "app_version":
                        appVersionField.setText(value);
                        break;
                    case "admin_email":
                        adminEmailField.setText(value);
                        break;
                    case "audit_retention":
                        auditRetentionField.setText(value);
                        break;
                }
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            userList.clear();
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                userList.add(user);
            }

            userTable.setItems(userList);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    private void loadRoles() {
        try {
            ObservableList<String> roles = FXCollections.observableArrayList();
            String sql = "SELECT name FROM roles";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                roles.add(rs.getString("name"));
            }

            roleListView.setItems(roles);
            inviteRoleCombo.setItems(roles);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    // ============ SETUP LISTENERS ============
    private void setupListeners() {
        // Save system settings
        saveSystemBtn.setOnAction(e -> saveSystemSettings());

        // Invite user
        inviteUserBtn.setOnAction(e -> inviteUser());

        // Role selection
        roleListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        loadRolePermissions(newVal);
                    }
                }
        );

        // Save role permissions
        saveRoleBtn.setOnAction(e -> saveRolePermissions());

        // Setup table columns
        if (colUserName != null) {
            colUserName.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        }
        if (colUserEmail != null) {
            colUserEmail.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        }
        if (colUserRole != null) {
            colUserRole.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        }
    }

    // ============ SAVE FUNCTIONS ============
    @FXML
    private void saveSystemSettings() {
        try {
            String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Update each setting
            String[][] updates = {
                    {appNameField.getText(), "app_name"},
                    {appVersionField.getText(), "app_version"},
                    {adminEmailField.getText(), "admin_email"},
                    {auditRetentionField.getText(), "audit_retention"}
            };

            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.executeUpdate();
            }

            pstmt.close();
            showSuccess("Paramètres sauvegardés avec succès!");
        } catch (SQLException e) {
            showError("Erreur de sauvegarde", e.getMessage());
        }
    }

    @FXML
    private void inviteUser() {
        String email = inviteEmailField.getText().trim();
        String role = inviteRoleCombo.getValue();

        if (email.isEmpty() || role == null) {
            showWarning("Attention", "Veuillez remplir tous les champs");
            return;
        }

        try {
            String sql = "INSERT INTO users (name, email, role) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.split("@")[0]); // Use email prefix as name
            pstmt.setString(2, email);
            pstmt.setString(3, role);

            pstmt.executeUpdate();
            pstmt.close();

            inviteEmailField.clear();
            inviteRoleCombo.setValue(null);
            loadUsers();
            showSuccess("Utilisateur invité avec succès!");
        } catch (SQLException e) {
            showError("Erreur d'invitation", e.getMessage());
        }
    }

    private void loadRolePermissions(String roleName) {
        try {
            String sql = "SELECT permissions FROM roles WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roleName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String perms = rs.getString("permissions");
                // Simple parsing (you can use JSON library for better parsing)
                permAuditCreate.setSelected(perms.contains("\"audit_create\":true"));
                permAuditView.setSelected(perms.contains("\"audit_view\":true"));
                permAuditEdit.setSelected(perms.contains("\"audit_edit\":true"));
                permAuditDelete.setSelected(perms.contains("\"audit_delete\":true"));
                permUserManage.setSelected(perms.contains("\"user_manage\":true"));
                permSettings.setSelected(perms.contains("\"settings\":true"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    private void saveRolePermissions() {
        String selectedRole = roleListView.getSelectionModel().getSelectedItem();
        if (selectedRole == null) {
            showWarning("Attention", "Veuillez sélectionner un rôle");
            return;
        }

        try {
            // Build JSON permissions
            String perms = String.format(
                    "{\"audit_create\":%b,\"audit_view\":%b,\"audit_edit\":%b," +
                            "\"audit_delete\":%b,\"user_manage\":%b,\"settings\":%b}",
                    permAuditCreate.isSelected(),
                    permAuditView.isSelected(),
                    permAuditEdit.isSelected(),
                    permAuditDelete.isSelected(),
                    permUserManage.isSelected(),
                    permSettings.isSelected()
            );

            String sql = "UPDATE roles SET permissions = ? WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, perms);
            pstmt.setString(2, selectedRole);
            pstmt.executeUpdate();
            pstmt.close();

            showSuccess("Permissions mises à jour!");
        } catch (SQLException e) {
            showError("Erreur de sauvegarde", e.getMessage());
        }
    }

    // ============ UTILITY METHODS ============
    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
