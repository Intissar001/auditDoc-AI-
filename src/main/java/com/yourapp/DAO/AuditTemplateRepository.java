package com.yourapp.DAO;

import com.yourapp.model.AuditTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTemplateRepository extends JpaRepository<AuditTemplate, Long> {

    List<AuditTemplate> findAllByOrderByCreatedAtDesc();

    List<AuditTemplate> findByNameContainingIgnoreCase(String name);

    List<AuditTemplate> findByOrganization(String organization);
}