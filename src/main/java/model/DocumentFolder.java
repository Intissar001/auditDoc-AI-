package com.auditdocai.model;

import java.util.UUID;
import java.time.ZonedDateTime;

public class DocumentFolder {

    private UUID id;
    private UUID organizationId;
    private UUID parentFolderId; // nullable
    private String name;

    private ZonedDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(UUID parentFolderId) { this.parentFolderId = parentFolderId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
