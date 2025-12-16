package com.yourapp;

import com.yourapp.model.Audit;
import com.yourapp.DAO.AuditRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuditDocAiApplication {

    @Autowired
    private AuditRepository auditRepository;

    public static void main(String[] args) {
        SpringApplication.run(AuditDocAiApplication.class, args);
    }

    @PostConstruct
    public void testDatabaseConnection() {
        Audit audit = new Audit();
        audit.setStatus("TEST_OK");
        auditRepository.save(audit);

        System.out.println("✅ Connexion PostgreSQL OK + INSERT réussi");
    }
}
