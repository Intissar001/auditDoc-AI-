package com.yourapp.services;

import com.yourapp.dto.AuditIssueDto;
import com.yourapp.model.AuditIssue;
import com.yourapp.model.Audit;
import com.yourapp.model.AuditDocument;
import com.yourapp.DAO.AuditIssueRepository;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsable de la gestion des problèmes d'audit détectés par l'IA
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditIssueService {

    private final AuditIssueRepository issueRepository;
    private final AuditDocumentRepository documentRepository;
    private final AuditRepository auditRepository;

    /**
     * Sauvegarder une liste de problèmes
     */
    @Transactional
    public List<AuditIssue> saveIssues(List<AuditIssue> issues) {
        log.info("Sauvegarde de {} problèmes d'audit", issues.size());
        return issueRepository.saveAll(issues);
    }

    /**
     * Sauvegarder un problème unique
     */
    @Transactional
    public AuditIssueDto saveIssue(AuditIssue issue) {
        log.info("Sauvegarde d'un problème d'audit pour l'audit {}", issue.getAudit().getId());
        AuditIssue saved = issueRepository.save(issue);
        return mapToDto(saved);
    }

    /**
     * Récupérer un problème par son ID
     */
    public AuditIssueDto getIssueById(Long issueId) {
        AuditIssue issue = issueRepository.findById(issueId.intValue())
                .orElseThrow(() -> new RuntimeException("Problème introuvable avec l'ID: " + issueId));
        return mapToDto(issue);
    }

    /**
     * Récupérer tous les problèmes d'un audit
     */
    public List<AuditIssueDto> getIssuesByAudit(Long auditId) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> issues = issueRepository.findByAudit(audit);
        return issues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les problèmes d'un audit avec pagination
     */
    public Page<AuditIssueDto> getIssuesByAudit(Long auditId, Pageable pageable) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> allIssues = issueRepository.findByAudit(audit);
        List<AuditIssueDto> issueDtos = allIssues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), issueDtos.size());

        return new PageImpl<>(issueDtos.subList(start, end), pageable, issueDtos.size());
    }

    /**
     * Récupérer tous les problèmes d'un document
     */
    public List<AuditIssueDto> getIssuesByDocument(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        List<AuditIssue> issues = issueRepository.findByDocument(document);
        return issues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtrer les problèmes selon plusieurs critères
     */
    public List<AuditIssueDto> filterIssues(Long auditId, String issueType,
                                            String severity, String category, Boolean resolved) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> issues = issueRepository.findByAudit(audit);

        return issues.stream()
                .filter(issue -> issueType == null || issue.getIssueType().equals(issueType))
                .filter(issue -> {
                    if (resolved != null) {
                        boolean isResolved = "Closed".equals(issue.getStatus()) || "Resolved".equals(issue.getStatus());
                        return isResolved == resolved;
                    }
                    return true;
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Marquer un problème comme résolu
     */
    @Transactional
    public AuditIssueDto resolveIssue(Long issueId, String resolvedBy) {
        log.info("Résolution du problème {} par {}", issueId, resolvedBy);

        AuditIssue issue = issueRepository.findById(issueId.intValue())
                .orElseThrow(() -> new RuntimeException("Problème introuvable avec l'ID: " + issueId));

        issue.setStatus("Resolved");

        issue = issueRepository.save(issue);
        return mapToDto(issue);
    }

    /**
     * Marquer un problème comme non résolu
     */
    @Transactional
    public AuditIssueDto unresolveIssue(Long issueId) {
        log.info("Marquage du problème {} comme non résolu", issueId);

        AuditIssue issue = issueRepository.findById(issueId.intValue())
                .orElseThrow(() -> new RuntimeException("Problème introuvable avec l'ID: " + issueId));

        issue.setStatus("Open");

        issue = issueRepository.save(issue);
        return mapToDto(issue);
    }

    /**
     * Supprimer un problème
     */
    @Transactional
    public void deleteIssue(Long issueId) {
        log.info("Suppression du problème {}", issueId);

        if (!issueRepository.existsById(issueId.intValue())) {
            throw new RuntimeException("Problème introuvable avec l'ID: " + issueId);
        }

        issueRepository.deleteById(issueId.intValue());
    }

    /**
     * Supprimer tous les problèmes d'un audit
     */
    @Transactional
    public void deleteByAudit(Audit audit) {
        log.info("Suppression de tous les problèmes de l'audit {}", audit.getId());
        List<AuditIssue> issues = issueRepository.findByAudit(audit);
        issueRepository.deleteAll(issues);
    }

    /**
     * Compter le nombre de problèmes d'un audit
     */
    public int countByAudit(Audit audit) {
        return issueRepository.findByAudit(audit).size();
    }

    /**
     * Obtenir les statistiques des problèmes d'un audit
     */
    public Map<String, Object> getIssueStatistics(Long auditId) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> issues = issueRepository.findByAudit(audit);

        int totalIssues = issues.size();
        long resolvedIssues = issues.stream()
                .filter(i -> "Resolved".equals(i.getStatus()) || "Closed".equals(i.getStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIssues", totalIssues);
        stats.put("resolvedIssues", resolvedIssues);
        stats.put("unresolvedIssues", totalIssues - resolvedIssues);
        stats.put("resolutionRate", totalIssues > 0 ? (resolvedIssues * 100.0 / totalIssues) : 0);

        return stats;
    }

    /**
     * Obtenir le nombre de problèmes par catégorie
     */
    public Map<String, Integer> getIssuesByCategory(Long auditId) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> issues = issueRepository.findByAudit(audit);

        return issues.stream()
                .collect(Collectors.groupingBy(
                        issue -> issue.getIssueType() != null ? issue.getIssueType() : "OTHER",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    /**
     * Obtenir le nombre de problèmes par sévérité
     */
    public Map<String, Integer> getIssuesBySeverity(Long auditId) {
        // Récupérer l'audit complet depuis la base
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        List<AuditIssue> issues = issueRepository.findByAudit(audit);

        Map<String, Integer> result = new HashMap<>();
        result.put("HIGH", 0);
        result.put("MEDIUM", 0);
        result.put("LOW", 0);
        result.put("TOTAL", issues.size());

        return result;
    }

    /**
     * Mapper une entité AuditIssue vers AuditIssueDto
     */
    private AuditIssueDto mapToDto(AuditIssue issue) {
        String documentName = null;
        Long documentId = null;

        if (issue.getDocument() != null) {
            documentName = issue.getDocument().getDocumentName();
            documentId = issue.getDocument().getId();
        }

        // Déterminer la localisation
        String location = "";
        if (issue.getPageNumber() != null) {
            location += "Page " + issue.getPageNumber();
        }
        if (issue.getParagraphNumber() != null) {
            if (!location.isEmpty()) location += ", ";
            location += "Paragraphe " + issue.getParagraphNumber();
        }

        boolean isResolved = "Resolved".equals(issue.getStatus()) || "Closed".equals(issue.getStatus());

        return AuditIssueDto.builder()
                .id(issue.getId().longValue())
                .auditId(issue.getAudit() != null ? issue.getAudit().getId() : null)
                .documentId(documentId)
                .documentName(documentName)
                .issueType(issue.getIssueType())
                .title(issue.getIssueType()) // Utiliser issueType comme titre si pas de champ title
                .description(issue.getDescription())
                .location(location)
                .suggestion(issue.getSuggestion())
                .resolved(isResolved)
                .detectedAt(issue.getReportedAt())
                .build();
    }
}