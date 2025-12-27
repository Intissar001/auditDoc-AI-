package com.yourapp.utils;

import com.yourapp.dto.*;
import com.yourapp.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Mapper pour convertir les entités en DTOs pour le dashboard
 */
@Component
public class DashboardMapper {

    public ProjectProgressDto toProjectProgressDto(Project project) {
        return ProjectProgressDto.builder()
                .projectId(project.getId())
                .projectName(project.getName())
                .progress(project.getProgress() / 100.0)
                .status(project.getStatus())
                .build();
    }

    public RecentActivityDto toActivityDto(Audit audit, String activityType) {
        String icon = getIconForActivityType(activityType);
        String title = getTitleForAudit(audit, activityType);
        LocalDateTime timestamp = getTimestampForAudit(audit, activityType);

        return RecentActivityDto.builder()
                .icon(icon)
                .title(title)
                .time(formatTime(timestamp))
                .type(activityType)
                .timestamp(timestamp)
                .build();
    }

    public RecentActivityDto toActivityDto(AuditDocument document) {
        return RecentActivityDto.builder()
                .icon("📄")
                .title("Document importé pour \"" +
                        (document.getAudit() != null ?
                                document.getAudit().getProjectName() : "Projet inconnu") + "\"")
                .time(formatTime(document.getUploadedAt()))
                .type("DOCUMENT_UPLOADED")
                .timestamp(document.getUploadedAt())
                .build();
    }

    public RecentActivityDto toActivityDto(Project project) {
        LocalDateTime timestamp = project.getStartDate().atStartOfDay();
        return RecentActivityDto.builder()
                .icon("📂")
                .title("Nouveau projet créé: \"" + project.getName() + "\"")
                .time(formatTime(timestamp))
                .type("PROJECT_CREATED")
                .timestamp(timestamp)
                .build();
    }

    private String getIconForActivityType(String type) {
        return switch (type) {
            case "AUDIT_COMPLETED" -> "✓";
            case "AUDIT_IN_PROGRESS" -> "⏳";
            case "AUDIT_FAILED" -> "❌";
            default -> "•";
        };
    }

    private String getTitleForAudit(Audit audit, String activityType) {
        String projectName = audit.getProjectName() != null ?
                audit.getProjectName() : "Projet inconnu";

        return switch (activityType) {
            case "AUDIT_COMPLETED" -> "Audit complété pour \"" + projectName + "\"";
            case "AUDIT_IN_PROGRESS" -> "Audit en cours pour \"" + projectName + "\"";
            case "AUDIT_FAILED" -> "Échec de l'audit pour \"" + projectName + "\"";
            default -> "Activité pour \"" + projectName + "\"";
        };
    }

    private LocalDateTime getTimestampForAudit(Audit audit, String activityType) {
        if ("AUDIT_COMPLETED".equals(activityType) && audit.getUpdatedAt() != null) {
            return audit.getUpdatedAt();
        }
        return audit.getCreatedAt() != null ? audit.getCreatedAt() : LocalDateTime.now();
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Date inconnue";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) {
            return "À l'instant";
        } else if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else if (minutes < 10080) {
            long days = minutes / 1440;
            return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
        } else {
            return dateTime.toLocalDate().toString();
        }
    }
}