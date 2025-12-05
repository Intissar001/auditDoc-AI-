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
}