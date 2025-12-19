// 📁 src/main/java/com/yourapp/services/HistoryService.java
package com.yourapp.services;

import com.yourapp.DAO.AuditReportRepository;
import com.yourapp.model.AuditReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class HistoryService {

    @Autowired
    private AuditReportRepository auditReportRepository;

    /**
     * Récupérer tous les rapports d'audit avec leurs relations
     */
    public List<AuditReport> getAllAuditReports() {
        System.out.println("📊 HistoryService: Chargement des rapports...");

        try {
            List<AuditReport> reports = auditReportRepository.findAllWithAuditAndRelations();
            System.out.println("✅ HistoryService: " + reports.size() + " rapports trouvés");
            return reports;
        } catch (Exception e) {
            System.err.println("❌ HistoryService erreur: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Compter le nombre total d'audits
     */
    public long getAuditCount() {
        return auditReportRepository.count();
    }

    /**
     * Sauvegarder un nouveau rapport
     */
    public AuditReport saveReport(AuditReport report) {
        return auditReportRepository.save(report);
    }

    /**
     * Trouver un rapport par ID
     */
    public AuditReport getReportById(Integer id) {
        return auditReportRepository.findById(id).orElse(null);
    }

    /**
     * Rafraîchir les données (pour tests)
     */
    public void refreshData() {
        System.out.println("🔄 HistoryService: Rafraîchissement des données...");
        // Force le rechargement
        auditReportRepository.findAll();
    }
}