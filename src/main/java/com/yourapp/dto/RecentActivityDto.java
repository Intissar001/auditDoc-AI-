package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO pour les activités récentes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityDto {
    private String icon;
    private String title;
    private String time;
    private String type; // AUDIT_COMPLETED, DOCUMENT_UPLOADED, PROJECT_CREATED
    private LocalDateTime timestamp; // Pour le tri
}