package com.yourapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_report")
public class AuditReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "audit_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_auditreport_audit")
    )
    private Audit audit;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "report_path", nullable = false, length = 512)
    private String reportPath;

    @Column(name = "report_summary", columnDefinition = "TEXT")
    private String reportSummary;

    @Column(name = "score")  // ⬅️ AJOUTÉ
    private Integer score;

    @Column(name = "problems_count")  // ⬅️ AJOUTÉ
    private Integer problemsCount;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    public AuditReport() {}

    // Constructeur amélioré
    public AuditReport(Audit audit, String reportPath, String reportSummary, Integer score, Integer problemsCount) {
        this.audit = audit;
        this.reportPath = reportPath;
        this.reportSummary = reportSummary;
        this.score = score;
        this.problemsCount = problemsCount;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Audit getAudit() { return audit; }
    public void setAudit(Audit audit) { this.audit = audit; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public String getReportPath() { return reportPath; }
    public void setReportPath(String reportPath) { this.reportPath = reportPath; }

    public String getReportSummary() { return reportSummary; }
    public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }

    public Integer getScore() { return score; }  // ⬅️ AJOUTÉ
    public void setScore(Integer score) { this.score = score; }  // ⬅️ AJOUTÉ

    public Integer getProblemsCount() { return problemsCount; }  // ⬅️ AJOUTÉ
    public void setProblemsCount(Integer problemsCount) { this.problemsCount = problemsCount; }  // ⬅️ AJOUTÉ
}