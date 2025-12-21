package com.yourapp.services;

import com.yourapp.dto.AuditCreateRequestDto;
import com.yourapp.dto.AuditResponseDto;
import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.model.Audit;
import com.yourapp.model.Project;
import com.yourapp.model.AuditTemplate;
import com.yourapp.model.AuditDocument;
import com.yourapp.DAO.AuditRepository;
import com.yourapp.DAO.ProjectRepository;
import com.yourapp.DAO.AuditTemplateRepository;
import com.yourapp.DAO.AuditDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsable de la gestion complète des audits
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;
    private final ProjectRepository projectRepository;
    private final AuditTemplateRepository templateRepository;
    private final AuditDocumentRepository auditDocumentRepository;
    private final AiAuditService aiAuditService;
    private final AuditIssueService auditIssueService;

    /**
     * Créer un nouvel audit
     */
    @Transactional
    public AuditResponseDto createAudit(AuditCreateRequestDto request) {
        log.info("Création d'un nouvel audit pour le projet {} avec le modèle {}",
                request.getProjectId(), request.getModelId());

        // Vérifier l'existence du projet
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID: " + request.getProjectId()));

        // Vérifier l'existence du template (modèle)
        AuditTemplate template = templateRepository.findById(request.getModelId())
                .orElseThrow(() -> new RuntimeException("Modèle introuvable avec l'ID: " + request.getModelId()));

        // Créer l'instance d'audit
        Audit audit = new Audit();
        audit.setProjectId(project.getId());
        audit.setProjectName(project.getName());
        audit.setModelId(template.getId());
        audit.setModelName(template.getName());
        audit.setOrganization(template.getOrganization());
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("PENDING");
        audit.setProblemsCount(0);

        audit = auditRepository.save(audit);

        // Associer les documents à l'audit
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            final Audit finalAudit = audit;
            request.getDocumentIds().forEach(docId -> {
                AuditDocument document = auditDocumentRepository.findById(docId)
                        .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + docId));
                document.setAudit(finalAudit);
                auditDocumentRepository.save(document);
            });
        }

        log.info("Audit créé avec succès: {}", audit.getId());
        return mapToResponseDto(audit);
    }

    /**
     * Démarrer l'analyse IA d'un audit
     */
    @Transactional
    public AuditResponseDto startAnalysis(Long auditId) {
        log.info("Démarrage de l'analyse pour l'audit {}", auditId);

        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        // Vérifier que l'audit n'est pas déjà en cours ou terminé
        if ("IN_PROGRESS".equals(audit.getStatus()) || "COMPLETED".equals(audit.getStatus())) {
            throw new RuntimeException("L'audit est déjà en cours ou terminé");
        }

        // Mettre à jour le statut
        audit.setStatus("IN_PROGRESS");
        audit = auditRepository.save(audit);

        // Lancer l'analyse IA de manière asynchrone
        try {
            aiAuditService.analyzeAudit(audit);

            audit.setStatus("COMPLETED");

            // Compter les problèmes détectés
            int problemsCount = auditIssueService.countByAudit(audit);
            audit.setProblemsCount(problemsCount);

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse de l'audit {}", auditId, e);
            audit.setStatus("FAILED");
        }

        audit = auditRepository.save(audit);

        log.info("Analyse terminée pour l'audit {}", auditId);
        return mapToResponseDto(audit);
    }

    /**
     * Récupérer un audit par son ID
     */
    public AuditResponseDto getAuditById(Long auditId) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));
        return mapToResponseDto(audit);
    }

    /**
     * Récupérer tous les audits d'un projet
     */
    public List<AuditResponseDto> getAuditsByProject(Long projectId) {
        List<Audit> audits = auditRepository.findByProjectId(projectId);
        return audits.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les audits avec pagination
     */
    public Page<AuditResponseDto> getAllAudits(Pageable pageable) {
        return auditRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Mettre à jour le statut d'un audit
     */
    @Transactional
    public AuditResponseDto updateStatus(Long auditId, String status) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        audit.setStatus(status);

        audit = auditRepository.save(audit);
        return mapToResponseDto(audit);
    }

    /**
     * Supprimer un audit
     */
    @Transactional
    public void deleteAudit(Long auditId) {
        log.info("Suppression de l'audit {}", auditId);

        if (!auditRepository.existsById(auditId)) {
            throw new RuntimeException("Audit introuvable avec l'ID: " + auditId);
        }

        Audit audit = auditRepository.findById(auditId).orElseThrow();

        // Supprimer les documents, issues et rapports associés (cascade)
        auditRepository.deleteById(auditId);
        log.info("Audit {} supprimé avec succès", auditId);
    }

    /**
     * Obtenir les statistiques d'un audit
     */
    public java.util.Map<String, Object> getAuditStatistics(Long auditId) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        int totalIssues = auditIssueService.countByAudit(audit);
        int documentsCount = (int) auditDocumentRepository.findByAuditId(audit.getId()).size();

        return java.util.Map.of(
                "auditId", auditId,
                "status", audit.getStatus(),
                "totalIssues", totalIssues,
                "documentsAnalyzed", documentsCount,
                "auditDate", audit.getAuditDate(),
                "score", audit.getScore() != null ? audit.getScore() : 0
        );
    }

    /**
     * Mapper une entité Audit vers AuditResponseDto
     */
    private AuditResponseDto mapToResponseDto(Audit audit) {
        // Récupérer les documents
        List<AuditDocument> documents = auditDocumentRepository.findByAuditId(audit.getId());
        List<AuditDocumentDto> documentDtos = documents.stream()
                .map(this::mapDocumentToDto)
                .collect(Collectors.toList());

        return AuditResponseDto.builder()
                .id(audit.getId())
                .projectId(audit.getProjectId())
                .projectName(audit.getProjectName())
                .modelId(audit.getModelId())
                .modelName(audit.getModelName())
                .status(audit.getStatus())
                .createdAt(audit.getCreatedAt() != null ? audit.getCreatedAt() : null)
                .score(audit.getScore())
                .problemsCount(audit.getProblemsCount())
                .comments(audit.getComments())
                .documents(documentDtos)
                .build();
    }

    /**
     * Mapper AuditDocument vers AuditDocumentDto
     */
    private AuditDocumentDto mapDocumentToDto(AuditDocument doc) {
        return AuditDocumentDto.builder()
                .id(doc.getId())
                .auditId(doc.getAudit() != null ? doc.getAudit().getId() : null)
                .fileName(doc.getDocumentName())
                .storagePath(doc.getDocumentPath())
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .analyzedAt(doc.getAnalyzedAt())
                .errorMessage(doc.getErrorMessage())
                .issuesCount(doc.getIssuesCount())
                .build();
    }
}