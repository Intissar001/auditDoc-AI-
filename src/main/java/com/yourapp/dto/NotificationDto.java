package com.yourapp.dto;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private Long userId;
    private Long projectId;
    private Long auditId;
    private String type;
    private String userName;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    // Project and Audit names for display
    private String projectName;
    private String auditName;

    // Constructors
    public NotificationDto() {}

    public NotificationDto(Long id, String message, String type, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    // Helper method to get notification icon based on type
    public String getIcon() {
        if (type == null) return "ℹ️";

        switch (type) {
            case "AUDIT_COMPLETED":
                return "✅";
            case "AUDIT_FAILED":
                return "❌";
            case "UPCOMING_AUDIT":
                return "⏰";
            case "MISSED_AUDIT":
                return "⚠️";
            case "PROJECT_UPDATED":
                return "📝";
            case "REPORT_GENERATED":
                return "📄";
            default:
                return "🔔";
        }
    }

    // Helper method to get relative time
    public String getRelativeTime() {
        if (createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (minutes < 1) return "À l'instant";
        if (minutes < 60) return minutes + " min";
        if (minutes < 1440) return (minutes / 60) + " h";
        return (minutes / 1440) + " j";
    }


}