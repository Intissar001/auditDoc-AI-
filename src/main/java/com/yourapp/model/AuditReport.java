package com.yourapp.model;

import java.time.ZonedDateTime;
import java.util.UUID;



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




    public AuditReport() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = ZonedDateTime.now();
    }



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

/*
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_report")
public class AuditReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "audit_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_auditreport_audit")
    )
    private Audit audit;


    @Column(
            name = "generated_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime generatedAt;


    @Column(
            name = "report_path",
            nullable = false,
            length = 512
    )
    private String reportPath;


    @Column(
            name = "report_summary",
            columnDefinition = "TEXT"
    )
    private String reportSummary;


    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }


    public AuditReport() {}

    public AuditReport(Audit audit, String reportPath, String reportSummary) {
        this.audit = audit;
        this.reportPath = reportPath;
        this.reportSummary = reportSummary;
    }



    public Integer getId() {
        return id;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getReportSummary() {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary) {
        this.reportSummary = reportSummary;
    }
}
*/