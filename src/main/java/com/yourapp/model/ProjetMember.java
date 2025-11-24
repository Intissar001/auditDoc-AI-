package com.yourapp.model;

import java.util.UUID;
import java.time.ZonedDateTime;

public class ProjetMember {

    private UUID id;
    private UUID projectId;
    private UUID userId;
    private String projectRole;  // owner, contributor, viewer...

    private ZonedDateTime addedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getProjectRole() { return projectRole; }
    public void setProjectRole(String projectRole) { this.projectRole = projectRole; }

    public ZonedDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(ZonedDateTime addedAt) { this.addedAt = addedAt; }
}
