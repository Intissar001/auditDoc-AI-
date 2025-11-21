package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ChangeLog {
    private UUID id;
    private UUID userId;        // who performed the action
    private String entityType;  // "Document", "AuditReport", etc.
    private UUID entityId;      // which entity
    private String action;      // "CREATE", "UPDATE", "DELETE"
    private String description; // optional detail
    private ZonedDateTime timestamp;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ZonedDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
}
