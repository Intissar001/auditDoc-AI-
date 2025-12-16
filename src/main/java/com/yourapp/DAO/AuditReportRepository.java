package com.yourapp.DAO;

import com.yourapp.model.AuditReport;
import com.yourapp.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Integer> {

    /**
     * Trouver tous les rapports d’un audit
     */
    List<AuditReport> findByAudit(Audit audit);

    /**
     * Trouver tous les rapports par ID d’audit
     */
    List<AuditReport> findByAuditId(Integer auditId);

    /**
     * Trouver le dernier rapport généré pour un audit
     */
    Optional<AuditReport> findTopByAuditIdOrderByGeneratedAtDesc(Integer auditId);

    /**
     * Vérifier si un audit possède au moins un rapport
     */
    boolean existsByAuditId(Integer auditId);
}
