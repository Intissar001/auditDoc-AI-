package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {

    private Long id;
    
    private String name;
    
    private String description;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private LocalDate prochainAuditDate;
    
    private String partner;
    
    private String status;
    
    private int progress;
    
    // Liste des documents d'audit associés à ce projet
    private List<AuditDocumentDto> auditDocuments;
}
