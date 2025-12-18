package com.yourapp.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

public class Notification {

    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification(String message) {
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void markAsRead() { this.read = true; }
}
/// /////////// code f



@Entity  //
@Table(name = "notifications")  // ⭐
public class Notification {

    @Id  // ⭐
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ⭐
    private Long id;  // ⭐ل Notification

    // ⭐ :
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id")
    private Audit audit;

    @Column(name = "notification_type")  // ⭐
    private String type; // "UPCOMING_AUDIT", "MISSED_AUDIT", "FAILED_AUDIT"

    private String message;

    @Column(name = "is_read")  //
    private boolean read;

    @Column(name = "created_at", updatable = false)  // ⭐
    private LocalDateTime createdAt;

    ///
    public Notification() {
    }

    public Notification(String message) {
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    // ⭐  User و Type
    public Notification(User user, String message, String type) {
        this.user = user;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void markAsRead() { this.read = true; }

    // ⭐ زد Getters/Setters للحقول الجديدة:

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
