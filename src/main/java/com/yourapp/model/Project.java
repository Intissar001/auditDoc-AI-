package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_audit_date")
    private LocalDate prochainAuditDate;

    private String partner;

    @Column(nullable = false)
    private String status; // "Actif", "Clôturé"

    private int progress; // ex: 87

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 🔗 Notifications liées à ce projet
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Notification> notifications;

    // ========= CONSTRUCTEURS =========

    public Project() {
        this.createdAt = LocalDateTime.now();
    }

    public Project(
            String name,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String partner,
            String status,
            int progress,
            LocalDate prochainAuditDate
    ) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.partner = partner;
        this.status = status;
        this.progress = progress;
        this.prochainAuditDate = prochainAuditDate;
        this.createdAt = LocalDateTime.now();
    }

    // ========= GETTERS & SETTERS =========

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getProchainAuditDate() {
        return prochainAuditDate;
    }

    public void setProchainAuditDate(LocalDate prochainAuditDate) {
        this.prochainAuditDate = prochainAuditDate;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
