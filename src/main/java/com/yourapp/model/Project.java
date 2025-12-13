package com.yourapp.model;

import java.time.LocalDate;

public class Project {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate prochainAuditDate;
    private String partner;
    private String status; // "Actif", "Clôturé"
    private int progress; // ex: 87

    public Project(String name, String description, LocalDate startDate, LocalDate endDate, String partner, String status, int progress , LocalDate prochainAuditDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prochainAuditDate = prochainAuditDate;
        this.partner = partner;
        this.status = status;
        this.progress = progress;
    }

    // Getters et Setters (Essentiels pour que le controleur y accède)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getProchainAuditDate() { return prochainAuditDate; }
    public void setProchainAuditDate(LocalDate prochainAuditDate) { this.prochainAuditDate = prochainAuditDate; }

    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}