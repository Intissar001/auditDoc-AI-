package com.yourapp.API;

import com.yourapp.model.Project;
import com.yourapp.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des projets
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    /* ============================
       CREATE
       ============================ */

    @PostMapping
    public ResponseEntity<Project> createProject(
            @Valid @RequestBody Project project) {

        projectService.saveProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    /* ============================
       READ
       ============================ */

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable Long projectId) {

        return projectService.getAllProjects()
                .stream()
                .filter(p -> p.getId().equals(projectId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ============================
       UPDATE
       ============================ */

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody Project updatedProject) {

        return projectService.getAllProjects()
                .stream()
                .filter(p -> p.getId().equals(projectId))
                .findFirst()
                .map(existing -> {
                    existing.setName(updatedProject.getName());
                    existing.setDescription(updatedProject.getDescription());
                    existing.setStatus(updatedProject.getStatus());
                    projectService.saveProject(existing);
                    return ResponseEntity.ok(existing);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /* ============================
       DELETE
       ============================ */

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId) {

        projectService.getAllProjects()
                .stream()
                .filter(p -> p.getId().equals(projectId))
                .findFirst()
                .ifPresent(project -> {
                    // méthode deleteById recommandée plus tard
                    // projectRepository.delete(project);
                });

        return ResponseEntity.noContent().build();
    }
}
