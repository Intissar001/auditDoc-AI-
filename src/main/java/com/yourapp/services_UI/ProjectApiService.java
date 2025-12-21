package com.yourapp.services_UI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourapp.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service JavaFX pour communiquer avec l'API REST des projets
 */
@Service
@Slf4j
public class ProjectApiService {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProjectApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Pour LocalDate
    }

    /**
     * Récupérer tous les projets disponibles
     */
    public List<Project> getAllProjects() {
        log.info("Récupération de tous les projets depuis l'API");

        try {
            String url = baseUrl + "/api/projects";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<Project> projects = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<Project>>() {}
                );

                log.info("✅ {} projets récupérés avec succès", projects.size());
                return projects;
            } else {
                log.error("❌ Erreur API: Code {}", response.statusCode());
                throw new RuntimeException("Erreur lors de la récupération des projets: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la communication avec l'API projets", e);
            throw new RuntimeException("Impossible de récupérer les projets: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer un projet par son ID
     */
    public Project getProjectById(Long projectId) {
        log.info("Récupération du projet ID: {}", projectId);

        try {
            String url = baseUrl + "/api/projects/" + projectId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Project project = objectMapper.readValue(
                        response.body(),
                        Project.class
                );

                log.info("✅ Projet récupéré: {}", project.getName());
                return project;
            } else {
                log.error("❌ Projet introuvable: {}", projectId);
                throw new RuntimeException("Projet introuvable avec l'ID: " + projectId);
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du projet", e);
            throw new RuntimeException("Impossible de récupérer le projet: " + e.getMessage(), e);
        }
    }

    /**
     * Rechercher des projets par nom
     */
    public List<Project> searchProjects(String query) {
        log.info("Recherche de projets: {}", query);

        try {
            String url = baseUrl + "/api/projects/search?query=" + query;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<Project> projects = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<Project>>() {}
                );

                log.info("✅ {} projets trouvés", projects.size());
                return projects;
            } else {
                log.warn("⚠️ Aucun projet trouvé pour: {}", query);
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la recherche de projets", e);
            return new ArrayList<>();
        }
    }

    /**
     * Vérifier la disponibilité de l'API
     */
    public boolean isApiAvailable() {
        try {
            String url = baseUrl + "/api/projects";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;

        } catch (Exception e) {
            log.warn("⚠️ API non disponible: {}", e.getMessage());
            return false;
        }
    }
}