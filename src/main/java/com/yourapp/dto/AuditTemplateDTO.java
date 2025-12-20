package com.yourapp.dto;

public class AuditTemplateDTO {

    private Long id;
    private String name;
    private String organization;
    private String description;
    private Integer ruleCount;

    public AuditTemplateDTO() {}

    public AuditTemplateDTO(Long id, String name, String organization, String description, Integer ruleCount) {
        this.id = id;
        this.name = name;
        this.organization = organization;
        this.description = description;
        this.ruleCount = ruleCount;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRuleCount() { return ruleCount; }
    public void setRuleCount(Integer ruleCount) { this.ruleCount = ruleCount; }
}