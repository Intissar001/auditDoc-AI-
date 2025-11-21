package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditRunMetadata {
    private UUID id;
    private UUID auditRequestId;
    private String aiModel;        // e.g., "GPT-5.1-AUDIT"
    private long processingTimeMs;
    private int tokensUsed;
    private ZonedDateTime executedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuditRequestId() { return auditRequestId; }
    public void setAuditRequestId(UUID auditRequestId) { this.auditRequestId = auditRequestId; }

    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }

    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }

    public ZonedDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(ZonedDateTime executedAt) { this.executedAt = executedAt; }
}
