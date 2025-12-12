package com.yourapp.service;

import com.yourapp.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service class for system settings database operations.
 */
public class SettingsService {

    /**
     * System settings model.
     */
    public static class SystemSettings {
        private boolean localStorage;
        private boolean cloudBackup;
        private boolean emailAlerts;
        private boolean auditReminders;
        private String ocrProvider;

        public boolean isLocalStorage() { return localStorage; }
        public void setLocalStorage(boolean localStorage) { this.localStorage = localStorage; }

        public boolean isCloudBackup() { return cloudBackup; }
        public void setCloudBackup(boolean cloudBackup) { this.cloudBackup = cloudBackup; }

        public boolean isEmailAlerts() { return emailAlerts; }
        public void setEmailAlerts(boolean emailAlerts) { this.emailAlerts = emailAlerts; }

        public boolean isAuditReminders() { return auditReminders; }
        public void setAuditReminders(boolean auditReminders) { this.auditReminders = auditReminders; }

        public String getOcrProvider() { return ocrProvider; }
        public void setOcrProvider(String ocrProvider) { this.ocrProvider = ocrProvider; }
    }

    /**
     * Loads system settings from database.
     * @return SystemSettings object, or null if loading fails
     */
    public static SystemSettings loadSettings() {
        String sql = "SELECT stockage_local, sauvegarde_cloud, alertes_email, rappels_audit, fournisseur_ocr " +
                     "FROM system_settings LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return null;
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SystemSettings settings = new SystemSettings();
                    settings.setLocalStorage(rs.getInt("stockage_local") == 1);
                    settings.setCloudBackup(rs.getInt("sauvegarde_cloud") == 1);
                    settings.setEmailAlerts(rs.getInt("alertes_email") == 1);
                    settings.setAuditReminders(rs.getInt("rappels_audit") == 1);
                    settings.setOcrProvider(rs.getString("fournisseur_ocr"));
                    return settings;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves basic system settings to database.
     * @param localStorage local storage enabled
     * @param cloudBackup cloud backup enabled
     * @param emailAlerts email alerts enabled
     * @param auditReminders audit reminders enabled
     * @return true if successful, false otherwise
     */
    public static boolean saveBasicSettings(boolean localStorage, boolean cloudBackup, 
                                          boolean emailAlerts, boolean auditReminders) {
        // First check if settings exist
        String checkSql = "SELECT COUNT(*) FROM system_settings";
        String updateSql = "UPDATE system_settings SET stockage_local = ?, sauvegarde_cloud = ?, " +
                          "alertes_email = ?, rappels_audit = ?";
        String insertSql = "INSERT INTO system_settings (stockage_local, sauvegarde_cloud, " +
                          "alertes_email, rappels_audit) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            
            // Check if settings exist
            boolean settingsExist = false;
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    settingsExist = true;
                }
            }
            
            // Update or insert
            try (PreparedStatement pstmt = conn.prepareStatement(
                    settingsExist ? updateSql : insertSql)) {
                pstmt.setInt(1, localStorage ? 1 : 0);
                pstmt.setInt(2, cloudBackup ? 1 : 0);
                pstmt.setInt(3, emailAlerts ? 1 : 0);
                pstmt.setInt(4, auditReminders ? 1 : 0);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

