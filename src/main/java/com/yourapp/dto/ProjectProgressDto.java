package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO pour la progression d'un projet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressDto {
    private Long projectId;
    private String projectName;
    private double progress; // 0.0 à 1.0
    private String status;
}