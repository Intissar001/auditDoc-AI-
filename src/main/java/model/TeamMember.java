package com.auditdocai.model;

import java.util.UUID;
import java.time.ZonedDateTime;

public class TeamMember {

    private UUID id;
    private UUID teamId;
    private UUID userId;
    private String teamRole; // optional

    private ZonedDateTime addedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getTeamRole() { return teamRole; }
    public void setTeamRole(String teamRole) { this.teamRole = teamRole; }

    public ZonedDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(ZonedDateTime addedAt) { this.addedAt = addedAt; }
}
