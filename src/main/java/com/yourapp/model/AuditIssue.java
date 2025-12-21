package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_issue")
public class AuditIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private AuditDocument document;

    // ===== Colonnes =====

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "paragraph_number")
    private Integer paragraphNumber;

    @Column(name = "issue_type", length = 100)
    private String issueType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    @Column(length = 50)
    private String status = "Open";

    @Column(name = "reported_at", updatable = false)
    private LocalDateTime reportedAt;

    // ===== Hooks Hibernate =====

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public AuditDocument getDocument() {
        return document;
    }

    public void setDocument(AuditDocument document) {
        this.document = document;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(Integer paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }
}
