package com.yourapp.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditReport {
    private UUID id;
    private UUID auditRequestId;  // links to Module 5

    private String title;         // "Audit Final Report – Project X"
    private String summary;       // Executive summary (AI generated)
    private String status;        // "DRAFT", "FINAL"

    private ZonedDateTime createdAt;
    private ZonedDateTime finalizedAt;

    // ============================================
    // NEW FIELDS FOR HISTORY TABLE DISPLAY
    // ============================================
    private String projectName;      // Nom du projet (ex: "Projet Éducation Rurale")
    private Integer score;           // Score en pourcentage 0-100 (ex: 87)
    private String complianceStatus; // "Conforme" ou "Non-Conforme"
    private Integer problemsCount;   // Nombre de problèmes trouvés (ex: 3)

    // ============================================
    // EXISTING GETTERS AND SETTERS
    // ============================================
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(UUID auditRequestId) { this.auditRequestId = auditRequestId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getFinalizedAt() { return finalizedAt; }
    public void setFinalizedAt(ZonedDateTime finalizedAt) { this.finalizedAt = finalizedAt; }

    // ============================================
    // NEW GETTERS AND SETTERS
    // ============================================
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public Integer getProblemsCount() {
        return problemsCount;
    }

    public void setProblemsCount(Integer problemsCount) {
        this.problemsCount = problemsCount;
    }
}