package com.yourapp.model;

import java.util.UUID;

public class RolePermission {

    private UUID id;
    private UUID roleId;
    private UUID permissionId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }

    public UUID getPermissionId() { return permissionId; }
    public void setPermissionId(UUID permissionId) { this.permissionId = permissionId; }
}
