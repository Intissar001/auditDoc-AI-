package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Relations =====
    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    // ===== Fields =====
    @Column(name = "project_name", length = 255)  // ⬅️ AJOUTÉ
    private String projectName;
    @Column(name = "model_id", nullable = false)
    private Long modelId;

    @Column(name = "model_name", length = 255)
    private String modelName;
    @Column(name = "audit_date", nullable = false)
    private LocalDate auditDate;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "score")  // ⬅️ AJOUTÉ
    private Integer score;

    @Column(name = "problems_count")  // ⬅️ AJOUTÉ
    private Integer problemsCount;

    @Column(columnDefinition = "TEXT")
    private String comments;

    // ===== Timestamps =====
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Bidirectional Relationships =====
    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuditDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuditIssue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuditReport> reports = new ArrayList<>();

    // ===== Auto timestamps =====
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getProjectName() { return projectName; }  // ⬅️ AJOUTÉ
    public void setProjectName(String projectName) { this.projectName = projectName; }  // ⬅️ AJOUTÉ
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getScore() { return score; }  // ⬅️ AJOUTÉ
    public void setScore(Integer score) { this.score = score; }  // ⬅️ AJOUTÉ

    public Integer getProblemsCount() { return problemsCount; }  // ⬅️ AJOUTÉ
    public void setProblemsCount(Integer problemsCount) { this.problemsCount = problemsCount; }  // ⬅️ AJOUTÉ

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public List<AuditDocument> getDocuments() { return documents; }
    public void setDocuments(List<AuditDocument> documents) { this.documents = documents; }

    public List<AuditIssue> getIssues() { return issues; }
    public void setIssues(List<AuditIssue> issues) { this.issues = issues; }

    public List<AuditReport> getReports() { return reports; }
    public void setReports(List<AuditReport> reports) { this.reports = reports; }
}