package com.yourapp.model;

import java.util.UUID;

public class DocumentAccessControl {

    private UUID id;
    private UUID documentId;

    private UUID userId;      // nullable
    private UUID roleId;      // nullable

    private boolean canView;
    private boolean canEdit;
    private boolean canDelete;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }

    public boolean isCanView() { return canView; }
    public void setCanView(boolean canView) { this.canView = canView; }

    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }

    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
}
