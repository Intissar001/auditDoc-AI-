package com.yourapp.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing audit templates.
 */
public class AuditTemplateService {
    
    /**
     * Audit template data class.
     */
    public static class AuditTemplate {
        private int id;
        private String name;
        private String description;
        private String organization;
        private int ruleCount;
        
        public AuditTemplate(int id, String name, String description, String organization) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.organization = organization;
            this.ruleCount = 0; // Default value
        }
        
        public AuditTemplate(int id, String name, String description, String organization, int ruleCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.organization = organization;
            this.ruleCount = ruleCount;
        }
        
        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getOrganization() { return organization; }
        public int getRuleCount() { return ruleCount; }
        
        // Setters
        public void setRuleCount(int ruleCount) { this.ruleCount = ruleCount; }
    }
    
    /**
     * Gets all audit templates (in-memory only, no database).
     * @return List of AuditTemplate objects matching the UI mockup
     */
    public static List<AuditTemplate> getAllTemplates() {
        List<AuditTemplate> templates = new ArrayList<>();
        // Templates matching the Figma design screenshots
        templates.add(new AuditTemplate(1, "Template AFD", 
            "Normes d'audit selon les exigences AFD", 
            "Agence Française de Développement", 15));
        templates.add(new AuditTemplate(2, "Template USAID", 
            "USAID Standard Provisions et CFR", 
            "USAID", 22));
        templates.add(new AuditTemplate(3, "Template ISO 19011", 
            "Lignes directrices pour l'audit de systèmes de management", 
            "Standard International", 18));
        return templates;
    }
    
    /**
     * Creates a new audit template (in-memory only, no database).
     * @param name template name
     * @param description template description
     * @param organization organization name
     * @return true if successful
     */
    public static boolean createTemplate(String name, String description, String organization) {
        System.out.println("Template created (in-memory): " + name + " - " + organization);
        return true;
    }
    
    /**
     * Deletes an audit template (in-memory only, no database).
     * @param templateId template ID
     * @return true if successful
     */
    public static boolean deleteTemplate(int templateId) {
        System.out.println("Template deleted (in-memory): " + templateId);
        return true;
    }
}
