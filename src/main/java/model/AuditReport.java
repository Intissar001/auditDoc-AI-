package com.auditdocai.model;

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

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(UUID auditRequestId) { this.auditRequestId = auditRequestRequestId; }

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
}
