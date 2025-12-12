package com.yourapp.service;

import com.yourapp.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing audit templates.
 */
public class AuditTemplateService {

    /**
     * Audit template model.
     */
    public static class AuditTemplate {
        private Integer id;
        private String name;
        private String description;
        private String organization;
        private Integer ruleCount;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }

        public Integer getRuleCount() { return ruleCount; }
        public void setRuleCount(Integer ruleCount) { this.ruleCount = ruleCount; }
    }

    /**
     * Retrieves all audit templates from the database.
     * @return List of AuditTemplate objects
     */
    public static List<AuditTemplate> getAllTemplates() {
        List<AuditTemplate> templates = new ArrayList<>();
        String sql = "SELECT id, nom, description, organisation, nombre_regles FROM audit_templates ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return templates;
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditTemplate template = new AuditTemplate();
                    template.setId(rs.getInt("id"));
                    template.setName(rs.getString("nom"));
                    template.setDescription(rs.getString("description"));
                    template.setOrganization(rs.getString("organisation"));
                    template.setRuleCount(rs.getInt("nombre_regles"));
                    templates.add(template);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all templates: " + e.getMessage());
            e.printStackTrace();
        }
        return templates;
    }

    /**
     * Creates a new audit template.
     * @param name template name
     * @param description template description
     * @param organization organization name
     * @return true if successful, false otherwise
     */
    public static boolean createTemplate(String name, String description, String organization) {
        String sql = "INSERT INTO audit_templates (nom, description, organisation, nombre_regles) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return false;
            }
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, organization);
            pstmt.setInt(4, 0); // Default rule count
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating template: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an audit template by ID.
     * @param id template ID
     * @return true if successful, false otherwise
     */
    public static boolean deleteTemplate(Integer id) {
        String sql = "DELETE FROM audit_templates WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return false;
            }
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting template: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

