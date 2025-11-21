// UserOrganization Entity
// -----------------------------
public class UserOrganization {
private UUID id;
private UUID userId;
private UUID organizationId;
private UUID roleId;
private ZonedDateTime createdAt;


public UUID getId() { return id; }
public void setId(UUID id) { this.id = id; }


public UUID getUserId() { return userId; }
public void setUserId(UUID userId) { this.userId = userId; }


public UUID getOrganizationId() { return organizationId; }
public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }


public UUID getRoleId() { return roleId; }
public void setRoleId(UUID roleId) { this.roleId = roleId; }


public ZonedDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}