package com.yourapp.dto;

public class UserSettingsDTO {

    private Long userId;
    private String email;
    private Boolean emailAlerts;
    private Boolean auditReminders;

    public UserSettingsDTO() {}

    public UserSettingsDTO(Long userId, String email, Boolean emailAlerts, Boolean auditReminders) {
        this.userId = userId;
        this.email = email;
        this.emailAlerts = emailAlerts;
        this.auditReminders = auditReminders;
    }

    // Getters et Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailAlerts() {
        return emailAlerts;
    }

    public void setEmailAlerts(Boolean emailAlerts) {
        this.emailAlerts = emailAlerts;
    }

    public Boolean getAuditReminders() {
        return auditReminders;
    }

    public void setAuditReminders(Boolean auditReminders) {
        this.auditReminders = auditReminders;
    }
}