package com.yourapp.services;

import com.yourapp.DAO.AuditReportRepository;
import com.yourapp.model.AuditReport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final AuditReportRepository auditReportRepository;

    public HistoryService(AuditReportRepository auditReportRepository) {
        this.auditReportRepository = auditReportRepository;
    }

    public List<AuditReport> getHistory() {
        return auditReportRepository.findAllWithAuditAndRelations();
    }
}
