package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditRequest {
    private UUID id;
    private UUID projectId;      // Comes from Module 4
    private UUID documentId;     // Comes from Module 3
    private UUID requestedBy;    // User.id from Module 1

    private String auditType;    // "COMPLIANCE", "QUALITY", "SECURITY", etc.
    private String status;       // "PENDING", "RUNNING", "COMPLETED", "FAILED"

    private ZonedDateTime createdAt;
    private ZonedDateTime completedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }

    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }

    public String getAuditType() { return auditType; }
    public void setAuditType(String auditType) { this.auditType = auditType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(ZonedDateTime completedAt) { this.completedAt = completedAt; }
}
