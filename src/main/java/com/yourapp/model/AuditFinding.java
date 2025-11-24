package com.yourapp.model;

import java.util.UUID;

public class AuditFinding {
    private UUID id;
    private UUID auditRequestId;  // AuditRequest
    private String category;      // "RISK", "NON_CONFORMITY", "WARNING"
    private String title;
    private String description;
    private int severityLevel;    // 1 = low, 5 = critical

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(UUID auditRequestId) { this.auditRequestId = auditRequestId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(int severityLevel) { this.severityLevel = severityLevel; }
}
