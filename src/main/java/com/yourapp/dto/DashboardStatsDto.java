package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO pour les statistiques du dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private String userName;
    private int totalAudits;
    private int auditsThisMonth;
    private int totalProjects;
    private int projectsThisWeek;
    private int auditsConforme;
    private int auditsNonConforme;
    private int globalScore;
    private String complianceStatus;
}