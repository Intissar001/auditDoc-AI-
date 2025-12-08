package com.yourapp.repository;

import com.yourapp.database.DatabaseManager;
import com.yourapp.model.AuditReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for AuditReport CRUD operations
 */
public class AuditRepository {

    private final DatabaseManager dbManager;

    public AuditRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Save audit to database
     * (Called from Audit Page after analysis)
     */
    public AuditReport save(AuditReport audit) {
        EntityManager em = dbManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(audit);
            em.getTransaction().commit();
            System.out.println("✅ Audit saved: " + audit.getProjectName());
            return audit;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Error saving audit: " + e.getMessage());
            throw new RuntimeException("Failed to save audit", e);
        } finally {
            em.close();
        }
    }

    /**
     * Get ALL audits (for History page)
     */
    public List<AuditReport> findAll() {
        EntityManager em = dbManager.getEntityManager();
        try {
            TypedQuery<AuditReport> query = em.createQuery(
                    "SELECT a FROM AuditReport a WHERE a.status = 'FINAL' ORDER BY a.createdAt DESC",
                    AuditReport.class
            );
            List<AuditReport> audits = query.getResultList();
            System.out.println("✅ Found " + audits.size() + " audits in database");
            return audits;
        } catch (Exception e) {
            System.err.println("❌ Error fetching audits: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Count total audits
     */
    public long count() {
        EntityManager em = dbManager.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM AuditReport a WHERE a.status = 'FINAL'",
                    Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("❌ Error counting audits: " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Get all unique project names (for partner filter in History)
     */
    public List<String> findAllProjectNames() {
        EntityManager em = dbManager.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT DISTINCT a.projectName FROM AuditReport a WHERE a.projectName IS NOT NULL AND a.status = 'FINAL' ORDER BY a.projectName",
                    String.class
            );
            List<String> projectNames = query.getResultList();
            System.out.println("✅ Found " + projectNames.size() + " unique project names");
            return projectNames;
        } catch (Exception e) {
            System.err.println("❌ Error fetching project names: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Get all unique partner names (for partner filter in History)
     */
    public List<String> findAllPartnerNames() {
        EntityManager em = dbManager.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT DISTINCT a.partnerName FROM AuditReport a WHERE a.partnerName IS NOT NULL AND a.status = 'FINAL' ORDER BY a.partnerName",
                    String.class
            );
            List<String> partnerNames = query.getResultList();
            System.out.println("✅ Found " + partnerNames.size() + " unique partner names");
            return partnerNames;
        } catch (Exception e) {
            System.err.println("❌ Error fetching partner names: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}