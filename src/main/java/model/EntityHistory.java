package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class EntityHistory {
    private UUID id;
    private String entityType;  // "Document", "AuditReport", etc.
    private UUID entityId;
    private String entitySnapshot; // JSON representation of the entity
    private ZonedDateTime recordedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }

    public String getEntitySnapshot() { return entitySnapshot; }
    public void setEntitySnapshot(String entitySnapshot) { this.entitySnapshot = entitySnapshot; }

    public ZonedDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(ZonedDateTime recordedAt) { this.recordedAt = recordedAt; }
}
