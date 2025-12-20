package com.yourapp.services;

import com.yourapp.model.Audit;
import com.yourapp.DAO.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public Audit createAudit(Audit audit) {
        new Audit();   // NOUVEL objet à chaque clic
        audit.setAuditDate(LocalDate.from(LocalDateTime.now()));
        return auditRepository.save(audit);
    }
}
