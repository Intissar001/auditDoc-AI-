package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditTask {
    private UUID id;
    private UUID auditRequestId;   // AuditRequest.id

    private String taskType;       // "OCR", "TEXT_EXTRACTION", "NLP_ANALYSIS", "VALIDATION"
    private String status;         // "PENDING", "RUNNING", "DONE", "ERROR"
    private String errorMessage;

    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(UUID auditRequestId) { this.auditRequestId = auditRequestId; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public ZonedDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(ZonedDateTime startedAt) { this.startedAt = startedAt; }

    public ZonedDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(ZonedDateTime finishedAt) { this.finishedAt = finishedAt; }
}
