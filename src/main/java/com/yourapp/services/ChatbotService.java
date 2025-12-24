package com.yourapp.services;

import com.yourapp.DAO.AuditRepository;
import com.yourapp.DAO.ProjectRepository;
import com.yourapp.DAO.AuditIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final AuditRepository auditRepository;
    private final ProjectRepository projectRepository;
    private final AuditIssueRepository issueRepository;

    // These read directly from your application.properties
    @Value("${gemini.apiKey}")
    private String apiKey;

    @Value("${gemini.model}")
    private String modelName;

    @Value("${gemini.baseUrl}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper method to build the URL dynamically
    private String getApiUrl() {
        return String.format("%s/%s:generateContent", baseUrl, modelName);
    }

    // Envoyer un message au chatbot et recevoir une réponse
    public String sendMessage(String userMessage) {
        log.info("📩 Message reçu: {}", userMessage);

        try {
            // Construire le contexte avec les données de l'application
            String context = buildContext();

            // Construire le prompt complet
            String fullPrompt = buildPrompt(context, userMessage);

            // Appeler l'API Gemini
            String response = callGeminiAPI(fullPrompt);

            log.info("✅ Réponse générée avec succès");
            return response;

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du message", e);
            return "Désolé, je rencontre un problème technique. Veuillez réessayer.";
        }
    }

     // Construire le contexte avec les données actuelles
    private String buildContext() {
        StringBuilder context = new StringBuilder();

        context.append("CONTEXTE DE L'APPLICATION:\n");
        context.append("Tu es un assistant intelligent pour une application de gestion d'audits documentaires.\n\n");

        // Statistiques des projets
        long totalProjects = projectRepository.count();
        context.append("Nombre total de projets: ").append(totalProjects).append("\n");

        // Statistiques des audits
        long totalAudits = auditRepository.count();
        long completedAudits = auditRepository.findByStatus("COMPLETED").size();
        long pendingAudits = auditRepository.findByStatus("PENDING").size();

        context.append("Nombre total d'audits: ").append(totalAudits).append("\n");
        context.append("Audits complétés: ").append(completedAudits).append("\n");
        context.append("Audits en attente: ").append(pendingAudits).append("\n");

        // Statistiques des problèmes
        long totalIssues = issueRepository.count();
        long openIssues = issueRepository.findByStatus("Open").size();

        context.append("Nombre total de problèmes détectés: ").append(totalIssues).append("\n");
        context.append("Problèmes ouverts: ").append(openIssues).append("\n\n");

        // Liste des projets récents
        context.append("PROJETS RÉCENTS:\n");
        projectRepository.findAll().stream()
                .limit(5)
                .forEach(p -> context.append("- ").append(p.getName())
                        .append(" (").append(p.getStatus()).append(")\n"));

        return context.toString();
    }


      // Construire le prompt complet pour Gemini
    private String buildPrompt(String context, String userMessage) {
        return context + "\n\n" +
                "INSTRUCTIONS:\n" +
                "- Réponds en français de manière claire et concise\n" +
                "- Base tes réponses sur le contexte fourni\n" +
                "- Si tu ne connais pas la réponse exacte, propose des suggestions utiles\n" +
                "- Pour les questions sur les audits, explique les processus\n" +
                "- Pour les problèmes, propose des solutions concrètes\n" +
                "- Reste professionnel et constructif\n\n" +
                "MESSAGE DE L'UTILISATEUR: " + userMessage + "\n\n" +
                "RÉPONSE:";
    }


     // Appeler l'API Gemini

    private String callGeminiAPI(String prompt) throws Exception {
        // Construire le corps de la requête
        Map<String, Object> requestBody = new HashMap<>();

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();

        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);

        content.put("parts", parts);
        contents.add(content);

        requestBody.put("contents", contents);

        // Ajouter les paramètres de génération
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1000);
        requestBody.put("generationConfig", generationConfig);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // Créer la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiUrl()))
                .timeout(java.time.Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();


        // Envoyer la requête
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Parser la réponse
        if (response.statusCode() == 200) {
            return parseGeminiResponse(response.body());
        } else {
            log.error("Erreur API Gemini: {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Erreur API Gemini: " + response.statusCode());
        }
    }


     // Parser la réponse de Gemini

    private String parseGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        if (root.has("candidates") && root.get("candidates").isArray() &&
                root.get("candidates").size() > 0) {

            JsonNode candidate = root.get("candidates").get(0);

            if (candidate.has("content") && candidate.get("content").has("parts") &&
                    candidate.get("content").get("parts").isArray() &&
                    candidate.get("content").get("parts").size() > 0) {

                JsonNode part = candidate.get("content").get("parts").get(0);

                if (part.has("text")) {
                    return part.get("text").asText();
                }
            }
        }

        return "Désolé, je n'ai pas pu générer de réponse.";
    }


     // Obtenir des suggestions de questions

    public List<String> getSuggestedQuestions() {
        return Arrays.asList(
                "Combien d'audits sont en cours ?",
                "Quels sont mes projets actifs ?",
                "Comment résoudre les problèmes détectés ?",
                "Quelles sont les prochaines actions à faire ?",
                "Comment améliorer mon score de conformité ?",
                "Montre-moi un résumé de mes audits"
        );
    }
}