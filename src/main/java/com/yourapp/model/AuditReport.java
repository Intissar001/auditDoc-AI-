package com.yourapp.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_reports")
public class AuditReport {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "audit_request_id", length = 36)
    private String auditRequestId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "status", length = 50)
    private String status; // "DRAFT", "FINAL"

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "finalized_at")
    private ZonedDateTime finalizedAt;

    // Fields for History display
    @Column(name = "project_name", length = 255)
    private String projectName;

    @Column(name = "partner_name", length = 255)
    private String partnerName;  // NEW: Partner name

    @Column(name = "score")
    private Integer score;

    @Column(name = "compliance_status", length = 50)
    private String complianceStatus; // "Conforme" / "Non-Conforme"

    @Column(name = "problems_count")
    private Integer problemsCount;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    // Default Constructor
    public AuditReport() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = ZonedDateTime.now();
    }

    // Constructor for easy creation
    public AuditReport(String projectName, Integer score, String complianceStatus, Integer problemsCount) {
        this();
        this.projectName = projectName;
        this.score = score;
        this.complianceStatus = complianceStatus;
        this.problemsCount = problemsCount;
        this.status = "FINAL";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(String auditRequestId) { this.auditRequestId = auditRequestId; }

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

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }

    public Integer getProblemsCount() { return problemsCount; }
    public void setProblemsCount(Integer problemsCount) { this.problemsCount = problemsCount; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
}