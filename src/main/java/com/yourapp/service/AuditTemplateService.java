package com.yourapp.service;

import com.google.gson.reflect.TypeToken;
import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.CreateTemplateRequest;

import java.util.ArrayList;
import java.util.List;

public class AuditTemplateService {

    public static class AuditTemplate {
        private Long id;
        private String name;
        private String organization;
        private String description;
        private Integer ruleCount;

        public AuditTemplate(Long id, String name, String organization,
                             String description, Integer ruleCount) {
            this.id = id;
            this.name = name;
            this.organization = organization;
            this.description = description;
            this.ruleCount = ruleCount;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getOrganization() { return organization; }
        public String getDescription() { return description; }
        public Integer getRuleCount() { return ruleCount; }
    }

    public static List<AuditTemplate> getAllTemplates() {
        try {
            List<AuditTemplateDTO> dtos = ApiClient.getList(
                    "/settings/templates",
                    new TypeToken<List<AuditTemplateDTO>>(){}.getType()
            );

            List<AuditTemplate> templates = new ArrayList<>();
            for (AuditTemplateDTO dto : dtos) {
                templates.add(new AuditTemplate(
                        dto.getId(),
                        dto.getName(),
                        dto.getOrganization(),
                        dto.getDescription(),
                        dto.getRuleCount()
                ));
            }
            System.out.println("✅ Loaded " + templates.size() + " templates from API");
            return templates;

        } catch (Exception e) {
            System.err.println("❌ Error loading templates: " + e.getMessage());
            e.printStackTrace();
            // Return mock data as fallback
            return getMockTemplates();
        }
    }

    public static boolean createTemplate(String name, String description, String organization) {
        try {
            CreateTemplateRequest request = new CreateTemplateRequest(name, organization, description);
            ApiClient.post("/settings/templates", request, AuditTemplateDTO.class);
            System.out.println("✅ Template created: " + name);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error creating template: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteTemplate(Long id) {
        try {
            boolean success = ApiClient.delete("/settings/templates/" + id);
            if (success) {
                System.out.println("✅ Template deleted: " + id);
            }
            return success;
        } catch (Exception e) {
            System.err.println("❌ Error deleting template: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Fallback mock data
    private static List<AuditTemplate> getMockTemplates() {
        List<AuditTemplate> templates = new ArrayList<>();
        templates.add(new AuditTemplate(1L, "Template AFD",
                "Agence Française de Développement",
                "Normes d'audit selon les exigences AFD", 15));
        templates.add(new AuditTemplate(2L, "Template USAID",
                "USAID",
                "USAID Standard Provisions et CFR", 22));
        templates.add(new AuditTemplate(3L, "Template ISO 19011",
                "Standard International",
                "Lignes directrices pour l'audit de systèmes de management", 18));
        System.out.println("⚠️ Using mock data (3 templates)");
        return templates;
    }
}