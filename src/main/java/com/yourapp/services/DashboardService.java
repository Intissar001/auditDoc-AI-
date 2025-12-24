package com.yourapp.services;

import com.yourapp.DAO.AuditRepository;
import com.yourapp.DAO.ProjectRepository;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.UserRepository;
import com.yourapp.model.Audit;
import com.yourapp.model.Project;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


     //Récupérer les statistiques du dashboard

    public DashboardStats getDashboardStats(Long userId) {
        log.info("Récupération des stats dashboard pour user {}", userId);

        DashboardStats stats = new DashboardStats();

        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        stats.setUserName(user.getFullName());

        // Total audits
        List<Audit> allAudits = auditRepository.findAll();
        stats.setTotalAudits(allAudits.size());

        // Audits du mois en cours
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        long auditsThisMonth = allAudits.stream()
                .filter(a -> a.getAuditDate() != null && !a.getAuditDate().isBefore(firstDayOfMonth))
                .count();
        stats.setAuditsThisMonth((int) auditsThisMonth);

        // Total projets
        List<Project> allProjects = projectRepository.findAll();
        stats.setTotalProjects(allProjects.size());

        // Projets de cette semaine
        LocalDate weekAgo = LocalDate.now().minusWeeks(1);
        long projectsThisWeek = allProjects.stream()
                .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(weekAgo))
                .count();
        stats.setProjectsThisWeek((int) projectsThisWeek);

        // Audits conformes et non-conformes
        long conformeCount = allAudits.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) &&
                        a.getProblemsCount() != null && a.getProblemsCount() < 5)
                .count();
        stats.setAuditsConforme((int) conformeCount);

        long nonConformeCount = allAudits.stream()
                .filter(a -> a.getProblemsCount() != null && a.getProblemsCount() >= 5)
                .count();
        stats.setAuditsNonConforme((int) nonConformeCount);

        // Score global de conformité
        int globalScore = calculateGlobalScore(allAudits);
        stats.setGlobalScore(globalScore);
        stats.setComplianceStatus(getComplianceStatus(globalScore));

        return stats;
    }


     // Récupérer les projets avec progression

    public List<ProjectProgress> getProjectsProgress() {
        log.info("Récupération de la progression des projets");

        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(this::mapToProjectProgress)
                .sorted((p1, p2) -> Double.compare(p2.getProgress(), p1.getProgress()))
                .limit(5)
                .collect(Collectors.toList());
    }


     // Récupérer les activités récentes

    public List<RecentActivity> getRecentActivities() {
        log.info("Récupération des activités récentes");

        List<RecentActivity> activities = new ArrayList<>();

        // Audits récents complétés
        List<Audit> recentAudits = auditRepository.findAll().stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .sorted((a1, a2) -> {
                    LocalDateTime d1 = a1.getUpdatedAt();
                    LocalDateTime d2 = a2.getUpdatedAt();
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return d2.compareTo(d1);
                })
                .limit(3)
                .collect(Collectors.toList());

        for (Audit audit : recentAudits) {
            RecentActivity activity = new RecentActivity();
            activity.setIcon("✓");
            activity.setTitle("Audit complété pour \"" + audit.getProjectName() + "\"");
            activity.setTime(formatTime(audit.getUpdatedAt()));
            activity.setType("AUDIT_COMPLETED");
            activities.add(activity);
        }

        // Documents récemment importés
        List<AuditDocument> recentDocs = documentRepository.findAll().stream()
                .sorted((d1, d2) -> {
                    LocalDateTime t1 = d1.getUploadedAt();
                    LocalDateTime t2 = d2.getUploadedAt();
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                })
                .limit(2)
                .collect(Collectors.toList());

        for (AuditDocument doc : recentDocs) {
            RecentActivity activity = new RecentActivity();
            activity.setIcon("📄");
            String projectName = doc.getAudit() != null ?
                    doc.getAudit().getProjectName() : "Projet inconnu";
            activity.setTitle("Document importé pour \"" + projectName + "\"");
            activity.setTime(formatTime(doc.getUploadedAt()));
            activity.setType("DOCUMENT_UPLOADED");
            activities.add(activity);
        }

        // Nouveaux projets créés
        List<Project> recentProjects = projectRepository.findAll().stream()
                .sorted((p1, p2) -> {
                    LocalDate d1 = p1.getStartDate();
                    LocalDate d2 = p2.getStartDate();
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return d2.compareTo(d1);
                })
                .limit(2)
                .collect(Collectors.toList());

        for (Project project : recentProjects) {
            RecentActivity activity = new RecentActivity();
            activity.setIcon("📂");
            activity.setTitle("Nouveau projet créé: \"" + project.getName() + "\"");
            activity.setTime(formatTime(project.getStartDate()));
            activity.setType("PROJECT_CREATED");
            activities.add(activity);
        }

        return activities.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // =============== Méthodes privées ===============

    private int calculateGlobalScore(List<Audit> audits) {
        if (audits.isEmpty()) return 0;

        // Calculer la moyenne des scores d'audits complétés
        List<Audit> completedAudits = audits.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) && a.getScore() != null)
                .collect(Collectors.toList());

        if (completedAudits.isEmpty()) return 0;

        int totalScore = completedAudits.stream()
                .mapToInt(Audit::getScore)
                .sum();

        return totalScore / completedAudits.size();
    }

    private String getComplianceStatus(int score) {
        if (score >= 50) {
            return "Excellent! Continuez comme ça";
        } else {
            return "Insuffisant. Des efforts sont nécessaires.";
        }
    }

    private ProjectProgress mapToProjectProgress(Project project) {
        ProjectProgress pp = new ProjectProgress();
        pp.setProjectName(project.getName());
        pp.setProgress(project.getProgress() / 100.0);
        return pp;
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Date inconnue";

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 60) {
            return "Il y a " + minutes + " minute(s)";
        } else if (minutes < 1440) { // moins de 24h
            return "Il y a " + (minutes / 60) + " heure(s)";
        } else {
            return dateTime.toLocalDate().toString();
        }
    }

    private String formatTime(LocalDate date) {
        if (date == null) return "Date inconnue";
        return date.toString();
    }

    // =============== Classes internes ===============

    public static class DashboardStats {
        private String userName;
        private int totalAudits;
        private int auditsThisMonth;
        private int totalProjects;
        private int projectsThisWeek;
        private int auditsConforme;
        private int auditsNonConforme;
        private int globalScore;
        private String complianceStatus;

        // Getters et Setters
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public int getTotalAudits() { return totalAudits; }
        public void setTotalAudits(int totalAudits) { this.totalAudits = totalAudits; }

        public int getAuditsThisMonth() { return auditsThisMonth; }
        public void setAuditsThisMonth(int auditsThisMonth) { this.auditsThisMonth = auditsThisMonth; }

        public int getTotalProjects() { return totalProjects; }
        public void setTotalProjects(int totalProjects) { this.totalProjects = totalProjects; }

        public int getProjectsThisWeek() { return projectsThisWeek; }
        public void setProjectsThisWeek(int projectsThisWeek) { this.projectsThisWeek = projectsThisWeek; }

        public int getAuditsConforme() { return auditsConforme; }
        public void setAuditsConforme(int auditsConforme) { this.auditsConforme = auditsConforme; }

        public int getAuditsNonConforme() { return auditsNonConforme; }
        public void setAuditsNonConforme(int auditsNonConforme) { this.auditsNonConforme = auditsNonConforme; }

        public int getGlobalScore() { return globalScore; }
        public void setGlobalScore(int globalScore) { this.globalScore = globalScore; }

        public String getComplianceStatus() { return complianceStatus; }
        public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    }

    public static class ProjectProgress {
        private String projectName;
        private double progress;

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
    }

    public static class RecentActivity {
        private String icon;
        private String title;
        private String time;
        private String type;

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}