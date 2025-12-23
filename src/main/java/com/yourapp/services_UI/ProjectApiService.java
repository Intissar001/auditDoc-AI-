package com.yourapp.services_UI;

import com.yourapp.model.Project;
import com.yourapp.DAO.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service UI pour la gestion des projets
 * Intermédiaire entre JavaFX et le repository backend
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectApiService {

    private final ProjectRepository projectRepository;

    /**
     * Récupérer tous les projets disponibles
     */
    public List<Project> getAllProjects() {
        log.info("📥 Récupération de tous les projets");

        try {
            List<Project> projects = projectRepository.findAll();
            log.info("✅ {} projets récupérés avec succès", projects.size());
            return projects;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des projets", e);
            throw new RuntimeException("Impossible de récupérer les projets: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer un projet par son ID
     */
    public Project getProjectById(Long projectId) {
        log.info("📥 Récupération du projet ID: {}", projectId);

        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID: " + projectId));

            log.info("✅ Projet récupéré: {}", project.getName());
            return project;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du projet", e);
            throw new RuntimeException("Impossible de récupérer le projet: " + e.getMessage(), e);
        }
    }

    /**
     * Créer un nouveau projet
     */
    public Project createProject(Project project) {
        log.info("➕ Création d'un nouveau projet: {}", project.getName());

        try {
            Project saved = projectRepository.save(project);
            log.info("✅ Projet créé avec succès: ID={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du projet", e);
            throw new RuntimeException("Impossible de créer le projet: " + e.getMessage(), e);
        }
    }

    /**
     * Mettre à jour un projet
     */
    public Project updateProject(Long projectId, Project project) {
        log.info("🔄 Mise à jour du projet ID: {}", projectId);

        try {
            if (!projectRepository.existsById(projectId)) {
                throw new RuntimeException("Projet introuvable avec l'ID: " + projectId);
            }

            project.setId(projectId);
            Project updated = projectRepository.save(project);
            log.info("✅ Projet mis à jour avec succès");
            return updated;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour du projet", e);
            throw new RuntimeException("Impossible de mettre à jour le projet: " + e.getMessage(), e);
        }
    }

    /**
     * Supprimer un projet
     */
    public void deleteProject(Long projectId) {
        log.info("🗑️ Suppression du projet ID: {}", projectId);

        try {
            if (!projectRepository.existsById(projectId)) {
                throw new RuntimeException("Projet introuvable avec l'ID: " + projectId);
            }

            projectRepository.deleteById(projectId);
            log.info("✅ Projet supprimé avec succès");
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression du projet", e);
            throw new RuntimeException("Impossible de supprimer le projet: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifier si un projet existe
     */
    public boolean projectExists(Long projectId) {
        return projectRepository.existsById(projectId);
    }
}