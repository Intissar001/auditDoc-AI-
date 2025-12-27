package com.yourapp.services;

import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.AuditTemplateRepository;
import com.yourapp.DAO.ProjectRepository;
import com.yourapp.dto.ProjectDto;
import com.yourapp.utils.DashboardRefreshEvent;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.AuditTemplate;
import com.yourapp.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuditDocumentRepository auditDocumentRepository;
    private final AuditTemplateRepository auditTemplateRepository;
    private final ApplicationEventPublisher eventPublisher; // ✅ AJOUTÉ

    // =============== MÉTHODES POUR LES TEMPLATES ===============

    public List<AuditTemplate> getAllTemplates() {
        return auditTemplateRepository.findAll();
    }

    // =============== MÉTHODES POUR LES PROJETS (VERSION DTO) ===============

    /**
     * ✅ Créer un nouveau projet et publier un événement
     */
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        log.info("📁 Création d'un nouveau projet: {}", projectDto.getName());

        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate() != null ?
                projectDto.getStartDate() : LocalDate.now());
        project.setEndDate(projectDto.getEndDate());
        project.setProchainAuditDate(projectDto.getProchainAuditDate());
        project.setPartner(projectDto.getPartner());
        project.setStatus(projectDto.getStatus() != null ?
                projectDto.getStatus() : "Actif");
        project.setProgress(projectDto.getProgress());

        Project savedProject = projectRepository.save(project);

        // ✅ PUBLIER L'ÉVÉNEMENT
        eventPublisher.publishEvent(
                new DashboardRefreshEvent(this, "Nouveau projet créé: " + savedProject.getName())
        );

        log.info("✅ Projet créé avec ID: {}", savedProject.getId());

        return toDto(savedProject);
    }

    /**
     * ✅ Mettre à jour un projet et publier un événement
     */
    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        log.info("📝 Mise à jour du projet ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + id));

        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setProchainAuditDate(projectDto.getProchainAuditDate());
        project.setPartner(projectDto.getPartner());
        project.setStatus(projectDto.getStatus());
        project.setProgress(projectDto.getProgress());

        Project updatedProject = projectRepository.save(project);

        // ✅ PUBLIER L'ÉVÉNEMENT
        eventPublisher.publishEvent(
                new DashboardRefreshEvent(this, "Projet mis à jour: " + updatedProject.getName())
        );

        log.info("✅ Projet mis à jour: {}", updatedProject.getId());

        return toDto(updatedProject);
    }

    /**
     * ✅ Supprimer un projet et publier un événement
     */
    @Transactional
    public void deleteProject(Long id) {
        log.info("🗑️ Suppression du projet ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + id));

        String projectName = project.getName();
        projectRepository.deleteById(id);

        // ✅ PUBLIER L'ÉVÉNEMENT
        eventPublisher.publishEvent(
                new DashboardRefreshEvent(this, "Projet supprimé: " + projectName)
        );

        log.info("✅ Projet supprimé: {}", projectName);
    }

    /**
     * ✅ Récupérer tous les projets (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectsDto() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Récupérer un projet par ID (DTO)
     */
    @Transactional(readOnly = true)
    public ProjectDto getProjectDtoById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + id));
        return toDto(project);
    }

    /**
     * ✅ Convertir une entité Project en DTO
     */
    private ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .prochainAuditDate(project.getProchainAuditDate())
                .partner(project.getPartner())
                .status(project.getStatus())
                .progress(project.getProgress())
                .build();
    }

    // =============== MÉTHODES POUR LES PROJETS (VERSION ENTITY - Rétrocompatibilité) ===============

    /**
     * Récupérer tous les projets (entités)
     * @deprecated Utiliser getAllProjectsDto() à la place
     */
    @Deprecated
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Sauvegarder un projet (entité)
     * @deprecated Utiliser createProject() ou updateProject() à la place
     */
    @Deprecated
    @Transactional
    public void saveProject(Project project) {
        boolean isNewProject = project.getId() == null;
        Project savedProject = projectRepository.save(project);

        // ✅ PUBLIER L'ÉVÉNEMENT même pour l'ancienne méthode
        if (isNewProject) {
            eventPublisher.publishEvent(
                    new DashboardRefreshEvent(this, "Nouveau projet créé: " + savedProject.getName())
            );
        } else {
            eventPublisher.publishEvent(
                    new DashboardRefreshEvent(this, "Projet mis à jour: " + savedProject.getName())
            );
        }
    }

    // =============== MÉTHODES POUR LES DOCUMENTS ===============

    public List<AuditDocument> getDocumentsByProjectId(Long projectId) {
        return auditDocumentRepository.findByProjectId(projectId);
    }

    @Transactional
    public void deleteDocument(AuditDocument doc) {
        auditDocumentRepository.delete(doc);
    }
}