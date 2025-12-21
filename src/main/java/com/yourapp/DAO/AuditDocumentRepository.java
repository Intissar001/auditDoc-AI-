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
        extends JpaRepository<AuditDocument, Long>,
        JpaSpecificationExecutor<AuditDocument> {
    List<AuditDocument> findByAudit(Audit audit);

    List<AuditDocument> findByAuditId(Long auditId);

    Optional<AuditDocument> findByDocumentNameAndAuditId(
            String documentName,
            Long auditId
    );

    boolean existsByDocumentNameAndAuditId(
            String documentName,
            Long auditId
    );

    List<AuditDocument> findByAuditIdOrderByUploadedAtDesc(Long auditId);

    void deleteByAuditId(Long auditId);
}
