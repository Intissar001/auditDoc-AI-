package com.auditdocai.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConfigChangeLog {
    private UUID id;
    private UUID userId;   // who made the change
    private UUID configId; // which AppConfig
    private String oldValue;
    private String newValue;
    private String description; // optional
    private ZonedDateTime timestamp;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ZonedDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
}
