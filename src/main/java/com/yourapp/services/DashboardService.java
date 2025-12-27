package com.yourapp.services;

import com.yourapp.DAO.AuditRepository;
import com.yourapp.DAO.ProjectRepository;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.UserRepository;
import com.yourapp.dto.DashboardStatsDto;
import com.yourapp.dto.ProjectProgressDto;
import com.yourapp.dto.RecentActivityDto;
import com.yourapp.utils.DashboardMapper;
import com.yourapp.model.Audit;
import com.yourapp.model.Project;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AuditRepository auditRepository;
    private final ProjectRepository projectRepository;
    private final AuditDocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;

    /**
     * ✅ OPTIMISÉ: Récupérer les statistiques du dashboard avec données en temps réel
     * Une seule transaction pour toutes les données
     */
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(Long userId) {
        long startTime = System.currentTimeMillis();
        log.info("📊 Récupération des stats dashboard pour user {}", userId);

        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));

        // ✅ UNE SEULE requête pour TOUTES les données
        List<Audit> allAudits = auditRepository.findAll();
        List<Project> allProjects = projectRepository.findAll();

        // Calculer les statistiques en mémoire (très rapide)
        int totalAudits = allAudits.size();
        int auditsThisMonth = calculateAuditsThisMonth(allAudits);
        int totalProjects = allProjects.size();
        int projectsThisWeek = calculateProjectsThisWeek(allProjects);
        int auditsConforme = calculateConformeAudits(allAudits);
        int auditsNonConforme = calculateNonConformeAudits(allAudits);
        int globalScore = calculateGlobalScore(allAudits);
        String complianceStatus = getComplianceStatus(globalScore, totalAudits);

        long duration = System.currentTimeMillis() - startTime;
        log.info("✅ Stats calculées en {}ms: {} audits, {} projets, score: {}%",
                duration, totalAudits, totalProjects, globalScore);

        return DashboardStatsDto.builder()
                .userName(user.getFullName())
                .totalAudits(totalAudits)
                .auditsThisMonth(auditsThisMonth)
                .totalProjects(totalProjects)
                .projectsThisWeek(projectsThisWeek)
                .auditsConforme(auditsConforme)
                .auditsNonConforme(auditsNonConforme)
                .globalScore(globalScore)
                .complianceStatus(complianceStatus)
                .build();
    }

    /**
     * ✅ OPTIMISÉ: Récupérer les projets avec progression (top 5)
     */
    @Transactional(readOnly = true)
    public List<ProjectProgressDto> getProjectsProgress() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(dashboardMapper::toProjectProgressDto)
                .sorted((p1, p2) -> Double.compare(p2.getProgress(), p1.getProgress()))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * ✅ OPTIMISÉ: Récupérer les activités récentes (dernières 10)
     */
    @Transactional(readOnly = true)
    public List<RecentActivityDto> getRecentActivities() {
        List<RecentActivityDto> activities = new ArrayList<>();

        // 1. Audits récents complétés (derniers 3)
        List<Audit> recentCompletedAudits = auditRepository.findAll().stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .sorted(Comparator.comparing(Audit::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .collect(Collectors.toList());

        recentCompletedAudits.forEach(audit ->
                activities.add(dashboardMapper.toActivityDto(audit, "AUDIT_COMPLETED"))
        );

        // 2. Documents récemment importés (derniers 2)
        List<AuditDocument> recentDocs = documentRepository.findAll().stream()
                .sorted(Comparator.comparing(AuditDocument::getUploadedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(2)
                .collect(Collectors.toList());

        recentDocs.forEach(doc ->
                activities.add(dashboardMapper.toActivityDto(doc))
        );

        // 3. Nouveaux projets créés (derniers 2)
        List<Project> recentProjects = projectRepository.findAll().stream()
                .sorted(Comparator.comparing(Project::getStartDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(2)
                .collect(Collectors.toList());

        recentProjects.forEach(project ->
                activities.add(dashboardMapper.toActivityDto(project))
        );

        // Trier par timestamp et limiter à 10
        return activities.stream()
                .sorted(Comparator.comparing(RecentActivityDto::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    // =============== Méthodes de calcul privées ===============

    private int calculateAuditsThisMonth(List<Audit> audits) {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        return (int) audits.stream()
                .filter(a -> a.getAuditDate() != null &&
                        !a.getAuditDate().isBefore(firstDayOfMonth))
                .count();
    }

    private int calculateProjectsThisWeek(List<Project> projects) {
        LocalDate weekAgo = LocalDate.now().minusWeeks(1);

        return (int) projects.stream()
                .filter(p -> p.getStartDate() != null &&
                        !p.getStartDate().isBefore(weekAgo))
                .count();
    }

    private int calculateConformeAudits(List<Audit> audits) {
        return (int) audits.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .filter(a -> a.getProblemsCount() != null && a.getProblemsCount() < 5)
                .count();
    }

    private int calculateNonConformeAudits(List<Audit> audits) {
        return (int) audits.stream()
                .filter(a -> a.getProblemsCount() != null && a.getProblemsCount() >= 5)
                .count();
    }

    /**
     * ✅ CORRIGÉ: Calculer le score global avec gestion du cas 0 audit
     */
    private int calculateGlobalScore(List<Audit> audits) {
        List<Audit> completedAudits = audits.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .filter(a -> a.getScore() != null && a.getScore() > 0)
                .collect(Collectors.toList());

        if (completedAudits.isEmpty()) {
            // ✅ Si aucun audit complété, retourner 0 au lieu de calculer
            return 0;
        }

        int totalScore = completedAudits.stream()
                .mapToInt(Audit::getScore)
                .sum();

        return totalScore / completedAudits.size();
    }

    /**
     * ✅ CORRIGÉ: Message adapté quand aucun audit n'existe
     */
    private String getComplianceStatus(int score, int totalAudits) {
        // Si aucun audit, message spécifique
        if (totalAudits == 0) {
            return "Aucun audit disponible. Créez votre premier audit ! 🚀";
        }

        // Si des audits existent mais aucun complété
        if (score == 0) {
            return "En attente d'audits complétés pour calculer le score";
        }

        // Sinon, message basé sur le score
        if (score >= 80) {
            return "Excellent ! Continuez comme ça 🎉";
        } else if (score >= 60) {
            return "Bon niveau de conformité ✓";
        } else if (score >= 40) {
            return "Conforme mais des améliorations possibles";
        } else {
            return "Insuffisant. Des efforts sont nécessaires ⚠️";
        }
    }
}