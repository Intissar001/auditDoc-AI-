package com.yourapp.DAO;

import com.yourapp.model.AuditIssue;
import com.yourapp.model.Audit;
import com.yourapp.model.AuditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AuditIssueRepository extends JpaRepository<AuditIssue, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM AuditIssue ai WHERE ai.document.id = :documentId")
    void deleteByDocumentId(@Param("documentId") Long documentId);

    // 🔹 Toutes les issues d’un audit
    List<AuditIssue> findByAudit(Audit audit);

    // 🔹 Toutes les issues d’un document précis
    List<AuditIssue> findByDocument(AuditDocument document);

    // 🔹 Issues d’un audit avec un status donné (Open, Closed, InProgress…)
    List<AuditIssue> findByAuditAndStatus(Audit audit, String status);

    // 🔹 Issues par type (ex: "Non conformité", "Manque de preuve")
    List<AuditIssue> findByIssueType(String issueType);

    // 🔹 Issues ouvertes uniquement
    List<AuditIssue> findByStatus(String status);

    // 🔹 Issues d’une page précise dans un document
    List<AuditIssue> findByDocumentAndPageNumber(AuditDocument document, Integer pageNumber);
}
