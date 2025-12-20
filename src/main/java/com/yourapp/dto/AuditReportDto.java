package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO pour le rapport d'audit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditReportDto {

    private Long id;

    private Long auditId;

    private String reportTitle;

    private String summary;

    private String conclusions;

    private String recommendations;

    private Integer totalIssuesFound;

    private Integer criticalIssuesCount;

    private Integer warningIssuesCount;

    private Integer infoIssuesCount;

    private Integer documentsAnalyzed;

    private Map<String, Integer> issuesByCategory;

    private Map<String, Integer> issuesByType;

    private String reportFilePath;

    private String reportFormat; // PDF, HTML, JSON

    private LocalDateTime generatedAt;

    private String generatedBy;

    private String status; // GENERATING, COMPLETED, FAILED
}