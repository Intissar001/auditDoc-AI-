package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditdocument")
public class AuditDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @Column(name = "document_name", nullable = false, length = 255)
    private String documentName;

    @Column(name = "document_path", nullable = false, length = 512)
    private String documentPath;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "status", length = 50)
    private String status; // UPLOADED, PROCESSING, ANALYZED, ERROR

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "issues_count")
    private Integer issuesCount;


    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // ========================
    // Getters & Setters
    // ========================

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }

    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    public String getErrorMessage() { return errorMessage; }

    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getIssuesCount() { return issuesCount; }

    public void setIssuesCount(Integer issuesCount) { this.issuesCount = issuesCount; }
}

