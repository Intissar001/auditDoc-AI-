package com.yourapp.model;

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
    private int simpleId;
    private String role;
    public User(int id, String name, String email, String role) {
        this.simpleId = id;
        this.fullName = name;
        this.email = email;
        this.role = role;}
    public User() {
        };



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
}
    public int getSimpleId() { return simpleId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return fullName; }