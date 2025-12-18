/*package com.yourapp.model;

import java.time.ZonedDateTime;
import java.util.UUID;

// User Entity
// -----------------------------
public class User {
private UUID id;
private String email;
private String fullName;
private String passwordHash;
private boolean isActive;
private ZonedDateTime createdAt;
private ZonedDateTime lastLoginAt;
private String phone;
private String locale;


public UUID getId() { return id; }
public void setId(UUID id) { this.id = id; }


public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }


public String getFullName() { return fullName; }
public void setFullName(String fullName) { this.fullName = fullName; }


public String getPasswordHash() { return passwordHash; }
public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }


public boolean isActive() { return isActive; }
public void setActive(boolean active) { isActive = active; }


public ZonedDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }


public ZonedDateTime getLastLoginAt() { return lastLoginAt; }
public void setLastLoginAt(ZonedDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }


public String getPhone() { return phone; }
public void setPhone(String phone) { this.phone = phone; }


public String getLocale() { return locale; }
public void setLocale(String locale) { this.locale = locale; }
}*/
package com.yourapp.model;

public class User {

    private String fullName;
    private String email;
    private String role;

    // Constructor with fullName and role (for backward compatibility)
    public User(String fullName, String role) {
        this.fullName = fullName;
        this.role = role;
        this.email = "";
    }

    // Constructor with fullName, email, and role
    public User(String fullName, String email, String role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public String getName() { return fullName; } // Alias for getFullName()
    public String getEmail() { return email != null ? email : ""; }
    public String getRole() { return role; }
    
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
////////////////////////////
//user classes
@Column(name = "email_alerts", nullable = false)
private Boolean emailAlerts = true;

@Column(name = "audit_reminders", nullable = false)
private Boolean auditReminders = true;

@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@Column(name = "last_login")
private LocalDateTime lastLogin;

// getters setters
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

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public LocalDateTime getLastLogin() {
    return lastLogin;
}

public void setLastLogin(LocalDateTime lastLogin) {
    this.lastLogin = lastLogin;
}

// أضف طريقة دورة الحياة (إن وجدت) أو أنشئها
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
}


