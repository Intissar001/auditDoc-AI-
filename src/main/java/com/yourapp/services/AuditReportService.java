package com.yourapp.services;

import com.yourapp.dto.AuditReportDto;
import com.yourapp.model.Audit;
import com.yourapp.model.AuditReport;
import com.yourapp.model.AuditIssue;
import com.yourapp.DAO.AuditReportRepository;
import com.yourapp.DAO.AuditRepository;
import com.yourapp.DAO.AuditIssueRepository;
import com.yourapp.DAO.AuditDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsable de la génération et gestion des rapports d'audit
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditReportService {

    private final AuditReportRepository reportRepository;
    private final AuditRepository auditRepository;
    private final AuditIssueRepository issueRepository;
    private final AuditDocumentRepository documentRepository;

    private final String reportsDir = "reports/";

    /**
     * Générer un rapport pour un audit
     */
    @Transactional
    public AuditReportDto generateReport(Long auditId, String format) {
        log.info("Génération du rapport pour l'audit {} au format {}", auditId, format);

        // Vérifier que l'audit existe
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        // Vérifier que l'audit est terminé
        if (!"COMPLETED".equals(audit.getStatus())) {
            throw new RuntimeException("L'audit doit être terminé avant de générer un rapport");
        }

        // Récupérer tous les problèmes de l'audit
        List<AuditIssue> issues = issueRepository.findByAudit(audit);

        // Calculer les statistiques
        Map<String, Integer> issuesByCategory = calculateIssuesByCategory(issues);
        Map<String, Integer> issuesByType = calculateIssuesByType(issues);

        int totalIssues = issues.size();
        int documentsCount = (int) documentRepository.findByAuditId(auditId).size();

        // Générer le contenu du rapport
        String summary = generateSummary(audit, issues, documentsCount);

        // Calculer le score (exemple : 100 - nombre de problèmes)
        int score = Math.max(0, 100 - totalIssues * 5);

        try {
            // Générer le fichier du rapport
            String reportPath = generateReportFile(audit, issues, summary, score);

            // Créer l'entité AuditReport
            AuditReport report = new AuditReport(audit, reportPath, summary, score, totalIssues);
            report = reportRepository.save(report);

            // Mettre à jour l'audit avec le score et le nombre de problèmes
            audit.setScore(score);
            audit.setProblemsCount(totalIssues);
            auditRepository.save(audit);

            log.info("Rapport généré avec succès pour l'audit {}", auditId);
            return mapToDto(report, issuesByCategory, issuesByType);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport", e);
            throw new RuntimeException("Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }

    /**
     * Récupérer un rapport par son ID
     */
    public AuditReportDto getReportById(Long reportId) {
        AuditReport report = reportRepository.findById(reportId.intValue())
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'ID: " + reportId));
        return mapToDto(report, null, null);
    }

    /**
     * Récupérer le rapport d'un audit
     */
    public AuditReportDto getReportByAudit(Long auditId) {
        AuditReport report = reportRepository.findTopByAuditIdOrderByGeneratedAtDesc(auditId)
                .orElseThrow(() -> new RuntimeException("Aucun rapport trouvé pour l'audit: " + auditId));
        return mapToDto(report, null, null);
    }

    /**
     * Récupérer tous les rapports d'un projet
     */
    public List<AuditReportDto> getReportsByProject(Long projectId) {
        // Récupérer tous les audits du projet
        List<Audit> audits = auditRepository.findByProjectId(projectId);

        return audits.stream()
                .flatMap(audit -> reportRepository.findByAudit(audit).stream())
                .map(report -> mapToDto(report, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Télécharger un rapport
     */
    public Resource downloadReport(Long reportId) {
        log.info("Téléchargement du rapport {}", reportId);

        AuditReport report = reportRepository.findById(reportId.intValue())
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'ID: " + reportId));

        try {
            Path filePath = Paths.get(report.getReportPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier de rapport");
            }
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du rapport {}", reportId, e);
            throw new RuntimeException("Erreur lors du téléchargement du rapport: " + e.getMessage());
        }
    }

    /**
     * Supprimer un rapport
     */
    @Transactional
    public void deleteReport(Long reportId) {
        log.info("Suppression du rapport {}", reportId);

        AuditReport report = reportRepository.findById(reportId.intValue())
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'ID: " + reportId));

        try {
            // Supprimer le fichier physique
            if (report.getReportPath() != null) {
                Path filePath = Paths.get(report.getReportPath());
                Files.deleteIfExists(filePath);
            }

            // Supprimer l'entrée en base de données
            reportRepository.deleteById(reportId.intValue());

            log.info("Rapport {} supprimé avec succès", reportId);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du rapport {}", reportId, e);
            throw new RuntimeException("Erreur lors de la suppression du rapport: " + e.getMessage());
        }
    }

    /**
     * Regénérer un rapport existant
     */
    @Transactional
    public AuditReportDto regenerateReport(Long reportId, String format) {
        log.info("Regénération du rapport {}", reportId);

        AuditReport existingReport = reportRepository.findById(reportId.intValue())
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'ID: " + reportId));

        Long auditId = existingReport.getAudit().getId();

        // Supprimer l'ancien rapport
        deleteReport(reportId);

        // Générer un nouveau rapport
        return generateReport(auditId, format);
    }

    /**
     * Obtenir le statut de génération d'un rapport
     */
    public String getReportStatus(Long reportId) {
        AuditReport report = reportRepository.findById(reportId.intValue())
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'ID: " + reportId));
        return "COMPLETED"; // Les rapports sont générés de manière synchrone
    }

    /**
     * Générer le résumé du rapport
     */
    private String generateSummary(Audit audit, List<AuditIssue> issues, int documentsCount) {
        int totalIssues = issues.size();

        return String.format(
                "Rapport d'audit pour le projet '%s' effectué le %s. " +
                        "L'analyse a porté sur %d document(s) et a révélé %d problème(s). " +
                        "Modèle utilisé: '%s'.",
                audit.getProjectName(),
                audit.getAuditDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                documentsCount,
                totalIssues,
                audit.getModelName()
        );
    }

    /**
     * Générer le fichier du rapport
     */
    private String generateReportFile(Audit audit, List<AuditIssue> issues, String summary, int score)
            throws IOException {

        // Créer le répertoire s'il n'existe pas
        Path reportsPath = Paths.get(reportsDir);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }

        // Générer le nom du fichier
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("audit_report_%d_%s.txt", audit.getId(), timestamp);
        Path filePath = reportsPath.resolve(fileName);

        // Générer le contenu
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(80)).append("\n");
        content.append("RAPPORT D'AUDIT - ").append(audit.getProjectName()).append("\n");
        content.append("=".repeat(80)).append("\n\n");
        content.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Score: ").append(score).append("/100\n");
        content.append("Problèmes détectés: ").append(issues.size()).append("\n\n");
        content.append("RÉSUMÉ\n").append("-".repeat(80)).append("\n");
        content.append(summary).append("\n\n");

        if (!issues.isEmpty()) {
            content.append("PROBLÈMES DÉTECTÉS\n").append("-".repeat(80)).append("\n");
            for (AuditIssue issue : issues) {
                content.append("• ").append(issue.getIssueType()).append("\n");
                content.append("  ").append(issue.getDescription()).append("\n");
                if (issue.getSuggestion() != null) {
                    content.append("  Suggestion: ").append(issue.getSuggestion()).append("\n");
                }
                content.append("\n");
            }
        }

        // Écrire le fichier
        Files.writeString(filePath, content.toString());

        return filePath.toString();
    }

    /**
     * Calculer le nombre de problèmes par catégorie
     */
    private Map<String, Integer> calculateIssuesByCategory(List<AuditIssue> issues) {
        return issues.stream()
                .collect(Collectors.groupingBy(
                        issue -> issue.getIssueType() != null ? issue.getIssueType() : "OTHER",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    /**
     * Calculer le nombre de problèmes par type
     */
    private Map<String, Integer> calculateIssuesByType(List<AuditIssue> issues) {
        Map<String, Integer> result = new HashMap<>();
        result.put("TOTAL", issues.size());
        return result;
    }

    /**
     * Mapper une entité AuditReport vers AuditReportDto
     */
    private AuditReportDto mapToDto(AuditReport report, Map<String, Integer> issuesByCategory,
                                    Map<String, Integer> issuesByType) {
        return AuditReportDto.builder()
                .id(report.getId().longValue())
                .auditId(report.getAudit().getId())
                .reportTitle("Rapport d'audit - " + report.getAudit().getProjectName())
                .summary(report.getReportSummary())
                .totalIssuesFound(report.getProblemsCount() != null ? report.getProblemsCount() : 0)
                .documentsAnalyzed((int) documentRepository.findByAuditId(report.getAudit().getId()).size())
                .issuesByCategory(issuesByCategory)
                .issuesByType(issuesByType)
                .reportFilePath(report.getReportPath())
                .reportFormat("TXT")
                .generatedAt(report.getGeneratedAt())
                .generatedBy("System")
                .status("COMPLETED")
                .build();
    }
}