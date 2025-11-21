package com.auditdocai.model;

import java.util.UUID;

public class ComplianceScore {
    private UUID id;
    private UUID reportId;      // AuditReport.id

    private String category;    // "QUALITY", "SECURITY", "COMPLIANCE"
    private int score;          // 0–100
    private String comment;     // Optional AI explanation

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReportId() { return reportId; }
    public void setReportId(UUID reportId) { this.reportId = reportId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
