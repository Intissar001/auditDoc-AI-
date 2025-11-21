package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ReportExport {
    private UUID id;
    private UUID reportId;     // AuditReport.id

    private String format;     // "PDF", "DOCX", "HTML"
    private String filePath;   // storage location
    private ZonedDateTime generatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReportId() { return reportId; }
    public void setReportId(UUID reportId) { this.reportId = reportId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public ZonedDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(ZonedDateTime generatedAt) { this.generatedAt = generatedAt; }
}
