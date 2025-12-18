package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditdocument")
public class AuditDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @Column(name = "document_name", nullable = false, length = 255)
    private String documentName;

    @Column(name = "document_path", nullable = false, length = 512)
    private String documentPath;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    // ========================
    // Constructeurs
    // ========================

    public AuditDocument() {
    }

    // ========================
    // Lifecycle callback
    // ========================

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // ========================
    // Getters & Setters
    // ========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
