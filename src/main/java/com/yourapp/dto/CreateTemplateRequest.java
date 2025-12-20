package com.yourapp.dto;

public class CreateTemplateRequest {

    private String name;
    private String organization;
    private String description;

    public CreateTemplateRequest() {}

    public CreateTemplateRequest(String name, String organization, String description) {
        this.name = name;
        this.organization = organization;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}