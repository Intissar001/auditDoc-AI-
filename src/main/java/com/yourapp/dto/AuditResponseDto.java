package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la réponse d'un audit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResponseDto {

    private Long id;

    private Long projectId;

    private String projectName;

    private Long modelId;

    private String modelName;

    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    private String description;

    private String additionalNotes;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private List<AuditDocumentDto> documents;

    private List<AuditIssueDto> issues;

    private AuditReportDto report;

    private Integer totalIssues;

    private Integer criticalIssues;

    private Integer warningIssues;

    private Integer infoIssues;
}