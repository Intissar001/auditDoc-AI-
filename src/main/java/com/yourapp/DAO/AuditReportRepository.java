package com.yourapp.DAO;

import com.yourapp.model.AuditReport;
import com.yourapp.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Integer> {

    /**
     * Trouver tous les rapports d'un audit
     */
    List<AuditReport> findByAudit(Audit audit);

    /**
     * Trouver tous les rapports par ID d'audit
     */
    List<AuditReport> findByAuditId(Long auditId);

    /**
     * Trouver le dernier rapport généré pour un audit
     */
    Optional<AuditReport> findTopByAuditIdOrderByGeneratedAtDesc(Long auditId);

    /**
     * Vérifier si un audit possède au moins un rapport
     */
    boolean existsByAuditId(Long auditId);

    // ========== MÉTHODE POUR HISTORY CONTROLLER ==========

    /**
     * OPTIMISÉ: Charger les rapports avec audit (sans issues pour éviter N+1)
     * ⬅️ Version optimisée pour éviter les problèmes de performance
     */
    @Query("SELECT DISTINCT ar FROM AuditReport ar " +
            "LEFT JOIN FETCH ar.audit a " +           // 👈 Charger l'audit
            "LEFT JOIN FETCH a.documents " +          // 👈 Charger les documents
            "LEFT JOIN FETCH a.issues " +           // 👈 DÉCOMMENTER SI BESOIN DES ISSUES
            "ORDER BY ar.generatedAt DESC")
    List<AuditReport> findAllWithAuditAndRelations();

    /**
     * OPTION: Version plus légère sans les documents
     * ⬅️ Pour les cas où on a besoin seulement des infos de base
     */
    @Query("SELECT ar FROM AuditReport ar " +
            "JOIN FETCH ar.audit " +                  // 👈 JOIN au lieu de LEFT JOIN
            "ORDER BY ar.generatedAt DESC")
    List<AuditReport> findAllWithAudit();            // 👈 AJOUTER CETTE MÉTHODE
}