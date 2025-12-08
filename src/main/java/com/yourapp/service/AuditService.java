package com.yourapp.service;

import com.yourapp.model.AuditReport;
import com.yourapp.repository.AuditRepository;

import java.util.List;

/**
 * Service layer for Audit operations
 */
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService() {
        this.auditRepository = new AuditRepository();
    }

    /**
     * Get all audits (for History page)
     */
    public List<AuditReport> getAllAudits() {
        try {
            return auditRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error in service layer: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Save audit (called from Audit Page)
     */
    public AuditReport saveAudit(AuditReport audit) {
        try {
            return auditRepository.save(audit);
        } catch (Exception e) {
            System.err.println("Error saving audit: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get audit count
     */
    public long getAuditCount() {
        try {
            return auditRepository.count();
        } catch (Exception e) {
            System.err.println("Error counting audits: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all unique project names (for filter dropdown)
     */
    public List<String> getAllProjectNames() {
        try {
            return auditRepository.findAllProjectNames();
        } catch (Exception e) {
            System.err.println("Error fetching project names: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all unique partner names (for partner filter dropdown)
     */
    public List<String> getAllPartnerNames() {
        try {
            return auditRepository.findAllPartnerNames();
        } catch (Exception e) {
            System.err.println("Error fetching partner names: " + e.getMessage());
            return List.of();
        }
    }
}