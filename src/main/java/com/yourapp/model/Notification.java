package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "notification_type")
    private String type; // "AUDIT_COMPLETED", "AUDIT_FAILED", "REPORT_GENERATED"

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ⚠️ SUPPRIMER les relations ManyToOne - elles causent l'erreur
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // private User user;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "project_id")
    // private Project project;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "audit_id")
    // private Audit audit;

    // Constructeurs
    public Notification() {}

    public Notification(String message) {
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(Long userId, String message, String type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
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

    public void markAsRead() {
        this.read = true;
    }

    // ⚠️ SUPPRIMER ces getters et setters
    // public User getUser() { return user; }
    // public void setUser(User user) { this.user = user; }
    // public Project getProject() { return project; }
    // public void setProject(Project project) { this.project = project; }
    // public Audit getAudit() { return audit; }
    // public void setAudit(Audit audit) { this.audit = audit; }
}