package com.yourapp.model;
import java.util.UUID;
import java.time.ZonedDateTime;
// UserOrganization Entity
// -----------------------------
public class UserOrganisation {
private UUID id;
private UUID userId;
private UUID organizationId;
private UUID roleId;
private ZonedDateTime createdAt;


public UUID getId() { return id; }
public void setId(UUID id) { this.id = id; }


public UUID getUserId() { return userId; }
public void setUserId(UUID userId) { this.userId = userId; }


public UUID getOrganisationId() { return organizationId; }
public void setOrganisationId(UUID organizationId) { this.organizationId = organizationId; }


public UUID getRoleId() { return roleId; }
public void setRoleId(UUID roleId) { this.roleId = roleId; }


public ZonedDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}