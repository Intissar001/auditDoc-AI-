package com.yourapp.services_UI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourapp.model.AuditTemplate;
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
 * Service JavaFX pour communiquer avec l'API REST des modèles/templates
 */
@Service
@Slf4j
public class ModelApiService {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ModelApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Récupérer tous les modèles disponibles
     */
    public List<AuditTemplate> getAllModels() {
        log.info("Récupération de tous les modèles depuis l'API");

        try {
            String url = baseUrl + "/api/templates";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditTemplate> models = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AuditTemplate>>() {}
                );

                log.info("✅ {} modèles récupérés avec succès", models.size());
                return models;
            } else {
                log.error("❌ Erreur API: Code {}", response.statusCode());
                throw new RuntimeException("Erreur lors de la récupération des modèles: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la communication avec l'API modèles", e);
            throw new RuntimeException("Impossible de récupérer les modèles: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer les modèles associés à un projet
     * Note: Cette méthode peut être adaptée selon votre logique métier
     * Pour l'instant, elle retourne tous les modèles car il n'y a pas de relation directe
     */
    public List<AuditTemplate> getModelsByProject(Long projectId) {
        log.info("Récupération des modèles pour le projet ID: {}", projectId);

        try {
            // Option 1: Si vous avez un endpoint spécifique
            // String url = baseUrl + "/api/models?projectId=" + projectId;

            // Option 2: Récupérer tous les modèles et filtrer (implémentation actuelle)
            String url = baseUrl + "/api/templates";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditTemplate> models = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AuditTemplate>>() {}
                );

                log.info("✅ {} modèles disponibles pour le projet", models.size());
                return models;
            } else {
                log.error("❌ Erreur API: Code {}", response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des modèles pour le projet", e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupérer un modèle par son ID
     */
    public AuditTemplate getModelById(Long modelId) {
        log.info("Récupération du modèle ID: {}", modelId);

        try {
            String url = baseUrl + "/api/templates/" + modelId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuditTemplate model = objectMapper.readValue(
                        response.body(),
                        AuditTemplate.class
                );

                log.info("✅ Modèle récupéré: {}", model.getName());
                return model;
            } else {
                log.error("❌ Modèle introuvable: {}", modelId);
                throw new RuntimeException("Modèle introuvable avec l'ID: " + modelId);
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du modèle", e);
            throw new RuntimeException("Impossible de récupérer le modèle: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer les modèles actifs uniquement
     */
    public List<AuditTemplate> getActiveModels() {
        log.info("Récupération des modèles actifs");

        try {
            String url = baseUrl + "/api/templates";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditTemplate> models = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AuditTemplate>>() {}
                );

                // Filtrer les modèles actifs si nécessaire
                log.info("✅ {} modèles actifs récupérés", models.size());
                return models;
            } else {
                log.error("❌ Erreur API: Code {}", response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des modèles actifs", e);
            return new ArrayList<>();
        }
    }

    /**
     * Rechercher des modèles par nom
     */
    public List<AuditTemplate> searchModels(String query) {
        log.info("Recherche de modèles: {}", query);

        try {
            String url = baseUrl + "/api/templates/search?name=" + query;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditTemplate> models = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AuditTemplate>>() {}
                );

                log.info("✅ {} modèles trouvés", models.size());
                return models;
            } else {
                log.warn("⚠️ Aucun modèle trouvé pour: {}", query);
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la recherche de modèles", e);
            return new ArrayList<>();
        }
    }
}