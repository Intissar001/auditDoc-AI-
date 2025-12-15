package com.yourapp.model;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Model class pour les rapports d'audit
 *
 * ⚠️  NOTE POUR L'ÉQUIPE:
 * Cette classe est un POJO simple sans persistence.
 * Pour ajouter la persistence database:
 * 1. Ajouter les imports JPA: import jakarta.persistence.*;
 * 2. Ajouter @Entity sur la classe
 * 3. Ajouter @Table(name = "audit_reports")
 * 4. Ajouter @Id sur le champ id
 * 5. Ajouter @Column(...) sur chaque champ
 */
public class AuditReport {

    // Identifiants
    private String id;
    private String auditRequestId;

    // Informations de base
    private String title;
    private String summary;
    private String status; // "DRAFT", "FINAL"

    // Dates
    private ZonedDateTime createdAt;
    private ZonedDateTime finalizedAt;

    // Informations pour l'affichage dans History
    private String projectName;
    private String partnerName;

    // Résultats de l'audit
    private Integer score;
    private String complianceStatus; // "Conforme" / "Non-Conforme"
    private Integer problemsCount;

    // Fichier PDF
    private String pdfPath;

    /**
     * Constructeur par défaut
     */
    public AuditReport() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * Constructeur pour création rapide
     */
    public AuditReport(String projectName, Integer score, String complianceStatus, Integer problemsCount) {
        this();
        this.projectName = projectName;
        this.score = score;
        this.complianceStatus = complianceStatus;
        this.problemsCount = problemsCount;
        this.status = "FINAL";
    }

    // ========================================
    //            GETTERS & SETTERS
    // ========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuditRequestId() {
        return auditRequestId;
    }

    public void setAuditRequestId(String auditRequestId) {
        this.auditRequestId = auditRequestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(ZonedDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public Integer getProblemsCount() {
        return problemsCount;
    }

    public void setProblemsCount(Integer problemsCount) {
        this.problemsCount = problemsCount;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    @Override
    public String toString() {
        return "AuditReport{" +
                "id='" + id + '\'' +
                ", projectName='" + projectName + '\'' +
                ", score=" + score +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}