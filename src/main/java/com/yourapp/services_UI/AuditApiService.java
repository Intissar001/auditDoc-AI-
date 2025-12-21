package com.yourapp.services_UI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourapp.dto.AuditCreateRequestDto;
import com.yourapp.dto.AuditResponseDto;
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
 * Service JavaFX pour communiquer avec l'API REST des audits
 */
@Service
@Slf4j
public class AuditApiService {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuditApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Créer un nouvel audit
     */
    public AuditResponseDto createAudit(AuditCreateRequestDto request) {
        log.info("Création d'un audit pour le projet ID: {}", request.getProjectId());

        try {
            String url = baseUrl + "/api/audits";

            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                AuditResponseDto audit = objectMapper.readValue(
                        response.body(),
                        AuditResponseDto.class
                );

                log.info("✅ Audit créé avec succès. ID: {}", audit.getId());
                return audit;
            } else {
                log.error("❌ Erreur création audit: Code {}", response.statusCode());
                throw new RuntimeException("Erreur lors de la création de l'audit: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de l'audit", e);
            throw new RuntimeException("Impossible de créer l'audit: " + e.getMessage(), e);
        }
    }

    /**
     * Lancer l'analyse d'un audit
     */
    public AuditResponseDto startAnalysis(Long auditId) {
        log.info("Démarrage de l'analyse pour l'audit ID: {}", auditId);

        try {
            String url = baseUrl + "/api/audits/" + auditId + "/analyze";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuditResponseDto audit = objectMapper.readValue(
                        response.body(),
                        AuditResponseDto.class
                );

                log.info("✅ Analyse lancée avec succès pour l'audit: {}", auditId);
                return audit;
            } else {
                log.error("❌ Erreur démarrage analyse: Code {}", response.statusCode());
                throw new RuntimeException("Erreur lors du démarrage de l'analyse: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors du démarrage de l'analyse", e);
            throw new RuntimeException("Impossible de démarrer l'analyse: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer un audit par son ID
     */
    public AuditResponseDto getAuditById(Long auditId) {
        log.info("Récupération de l'audit ID: {}", auditId);

        try {
            String url = baseUrl + "/api/audits/" + auditId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuditResponseDto audit = objectMapper.readValue(
                        response.body(),
                        AuditResponseDto.class
                );

                log.info("✅ Audit récupéré: Status = {}", audit.getStatus());
                return audit;
            } else {
                log.error("❌ Audit introuvable: {}", auditId);
                throw new RuntimeException("Audit introuvable avec l'ID: " + auditId);
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'audit", e);
            throw new RuntimeException("Impossible de récupérer l'audit: " + e.getMessage(), e);
        }
    }

    /**
     * Récupérer tous les audits d'un projet
     */
    public List<AuditResponseDto> getAuditsByProject(Long projectId) {
        log.info("Récupération des audits du projet ID: {}", projectId);

        try {
            String url = baseUrl + "/api/audits/project/" + projectId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditResponseDto> audits = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AuditResponseDto>>() {}
                );

                log.info("✅ {} audits récupérés pour le projet", audits.size());
                return audits;
            } else {
                log.error("❌ Erreur récupération audits: Code {}", response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des audits du projet", e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupérer les statistiques d'un audit
     */
    public java.util.Map<String, Object> getAuditStatistics(Long auditId) {
        log.info("Récupération des statistiques de l'audit ID: {}", auditId);

        try {
            String url = baseUrl + "/api/audits/" + auditId + "/statistics";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                java.util.Map<String, Object> stats = objectMapper.readValue(
                        response.body(),
                        new TypeReference<java.util.Map<String, Object>>() {}
                );

                log.info("✅ Statistiques récupérées pour l'audit");
                return stats;
            } else {
                log.error("❌ Erreur récupération statistiques: Code {}", response.statusCode());
                return new java.util.HashMap<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des statistiques", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * Supprimer un audit
     */
    public boolean deleteAudit(Long auditId) {
        log.info("Suppression de l'audit ID: {}", auditId);

        try {
            String url = baseUrl + "/api/audits/" + auditId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || response.statusCode() == 200) {
                log.info("✅ Audit supprimé avec succès");
                return true;
            } else {
                log.error("❌ Erreur suppression audit: Code {}", response.statusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de l'audit", e);
            return false;
        }
    }

    /**
     * Polling pour vérifier le statut d'un audit en cours
     */
    public AuditResponseDto pollAuditStatus(Long auditId, int maxAttempts, int intervalSeconds) {
        log.info("Polling du statut de l'audit ID: {}", auditId);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                AuditResponseDto audit = getAuditById(auditId);

                if ("COMPLETED".equals(audit.getStatus()) || "FAILED".equals(audit.getStatus())) {
                    log.info("✅ Audit terminé avec le statut: {}", audit.getStatus());
                    return audit;
                }

                log.info("⏳ Tentative {}/{}: Statut actuel = {}", attempt, maxAttempts, audit.getStatus());

                if (attempt < maxAttempts) {
                    Thread.sleep(intervalSeconds * 1000L);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("❌ Polling interrompu", e);
                throw new RuntimeException("Polling interrompu", e);
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors du polling (tentative {}): {}", attempt, e.getMessage());
            }
        }

        log.warn("⚠️ Timeout: L'audit n'a pas terminé dans le délai imparti");
        throw new RuntimeException("Timeout: L'audit n'a pas terminé dans le délai imparti");
    }
}