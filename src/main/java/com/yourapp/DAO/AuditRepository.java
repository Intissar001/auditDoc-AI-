package com.yourapp.DAO;

import com.yourapp.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // ===== CRUD déjà fournis par JpaRepository =====
    // save(Audit audit)
    // findById(Long id)
    // findAll()
    // deleteById(Long id)
    // count()

    // ===== Requêtes métier personnalisées =====

    // Trouver tous les audits d’un projet
    List<Audit> findByProjectId(Long projectId);

    // Trouver tous les audits d’un auditeur
    List<Audit> findByAuditorId(Long auditorId);

    // Trouver les audits par statut
    List<Audit> findByStatus(String status);

    // Trouver les audits d’un projet avec un statut précis
    List<Audit> findByProjectIdAndStatus(Long projectId, String status);

    // Trouver les audits effectués à une date précise
    List<Audit> findByAuditDate(LocalDate auditDate);

    // Trouver les audits dans une période
    List<Audit> findByAuditDateBetween(LocalDate startDate, LocalDate endDate);

    // Vérifier s’il existe un audit pour un projet à une date donnée
    boolean existsByProjectIdAndAuditDate(Long projectId, LocalDate auditDate);
}
