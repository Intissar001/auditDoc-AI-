package com.yourapp.DAO;

import com.yourapp.model.AuditDocument;
import com.yourapp.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditDocumentRepository
        extends JpaRepository<AuditDocument, Integer>,
        JpaSpecificationExecutor<AuditDocument> {

    // =========================
    // Basic queries
    // =========================

    List<AuditDocument> findByAudit(Audit audit);

    List<AuditDocument> findByAuditId(Integer auditId);

    Optional<AuditDocument> findByDocumentNameAndAuditId(
            String documentName,
            Integer auditId
    );

    boolean existsByDocumentNameAndAuditId(
            String documentName,
            Integer auditId
    );

    // =========================
    // Ordered queries
    // =========================

    List<AuditDocument> findByAuditIdOrderByUploadedAtDesc(Integer auditId);

    // =========================
    // Delete operations
    // =========================

    void deleteByAuditId(Integer auditId);
}
