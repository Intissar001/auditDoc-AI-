package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO pour les problèmes détectés lors de l'audit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditIssueDto {

    private Long id;

    private Long auditId;

    private Long documentId;

    private String documentName;

    private String issueType; // CRITICAL, WARNING, INFO, SUGGESTION

    private String category; // FORMATTING, CONTENT, COMPLIANCE, STRUCTURE, etc.

    private String title;

    private String description;

    private String location; // Page, section, ligne

    private String detectedText;

    private String suggestion;

    private String severity; // HIGH, MEDIUM, LOW

    private Boolean resolved;

    private String resolvedBy;

    private LocalDateTime resolvedAt;

    private LocalDateTime detectedAt;

    private String additionalContext;
}