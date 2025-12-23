package com.yourapp.services_UI;

import com.yourapp.dto.AuditCreateRequestDto;
import com.yourapp.dto.AuditResponseDto;
import com.yourapp.services.AuditService; // Import du service backend
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditApiService {

    // Injection directe du service backend
    private final AuditService auditService;

    /**
     * Créer un nouvel audit (appel direct au service)
     */
    public AuditResponseDto createAudit(AuditCreateRequestDto request) {
        log.info("Création d'un audit pour le projet ID: {}", request.getProjectId());

        try {
            AuditResponseDto audit = auditService.createAudit(request);
            log.info("✅ Audit créé avec succès. ID: {}", audit.getId());
            return audit;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de l'audit", e);
            throw new RuntimeException("Impossible de créer l'audit: " + e.getMessage(), e);
        }
    }

    /**
     * Lancer l'analyse d'un audit
     */
    public AuditResponseDto startAnalysis(Long auditId) {
        log.info("Démarrage de l'analyse pour l'audit ID: {}", auditId);

        try {
            AuditResponseDto audit = auditService.startAnalysis(auditId);
            log.info("✅ Analyse lancée avec succès pour l'audit: {}", auditId);
            return audit;
        } catch (Exception e) {
            log.error("❌ Erreur lors du démarrage de l'analyse", e);
            throw new RuntimeException("Impossible de démarrer l'analyse: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer un audit par son ID
     */
    public AuditResponseDto getAuditById(Long auditId) {
        log.info("Récupération de l'audit ID: {}", auditId);

        try {
            AuditResponseDto audit = auditService.getAuditById(auditId);
            log.info("✅ Audit récupéré: Status = {}", audit.getStatus());
            return audit;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'audit", e);
            throw new RuntimeException("Impossible de récupérer l'audit: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer tous les audits d'un projet
     */
    public List<AuditResponseDto> getAuditsByProject(Long projectId) {
        log.info("Récupération des audits du projet ID: {}", projectId);

        try {
            List<AuditResponseDto> audits = auditService.getAuditsByProject(projectId);
            log.info("✅ {} audits récupérés pour le projet", audits.size());
            return audits;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des audits du projet", e);
            throw new RuntimeException("Impossible de récupérer les audits", e);
        }
    }

    /**
     * Supprimer un audit
     */
    public boolean deleteAudit(Long auditId) {
        log.info("Suppression de l'audit ID: {}", auditId);

        try {
            auditService.deleteAudit(auditId);
            log.info("✅ Audit supprimé avec succès");
            return true;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de l'audit", e);
            return false;
        }
    }

    /**
     * Récupérer les statistiques d'un audit
     */
    public java.util.Map<String, Object> getAuditStatistics(Long auditId) {
        log.info("Récupération des statistiques de l'audit ID: {}", auditId);

        try {
            java.util.Map<String, Object> stats = auditService.getAuditStatistics(auditId);
            log.info("✅ Statistiques récupérées pour l'audit");
            return stats;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des statistiques", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * Polling pour vérifier le statut d'un audit en cours
     */
    public AuditResponseDto pollAuditStatus(Long auditId, int maxAttempts, int intervalSeconds) {
        log.info("Polling du statut de l'audit ID: {}", auditId);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                AuditResponseDto audit = getAuditById(auditId);

                if ("COMPLETED".equals(audit.getStatus()) || "FAILED".equals(audit.getStatus())) {
                    log.info("✅ Audit terminé avec le statut: {}", audit.getStatus());
                    return audit;
                }

                log.info("⏳ Tentative {}/{}: Statut actuel = {}", attempt, maxAttempts, audit.getStatus());

                if (attempt < maxAttempts) {
                    Thread.sleep(intervalSeconds * 1000L);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("❌ Polling interrompu", e);
                throw new RuntimeException("Polling interrompu", e);
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors du polling (tentative {}): {}", attempt, e.getMessage());
            }
        }

        log.warn("⚠️ Timeout: L'audit n'a pas terminé dans le délai imparti");
        throw new RuntimeException("Timeout: L'audit n'a pas terminé dans le délai imparti");
    }
}