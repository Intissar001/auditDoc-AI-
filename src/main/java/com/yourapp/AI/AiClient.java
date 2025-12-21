package com.yourapp.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * Client pour communiquer avec l'API d'intelligence artificielle
 * Envoie les requêtes et récupère les réponses
 */
@Component
@Slf4j
public class AiClient {

    @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${ai.max.tokens:2000}")
    private Integer maxTokens;

    @Value("${ai.temperature:0.7}")
    private Double temperature;

    private final RestTemplate restTemplate;

    public AiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envoyer une requête à l'API IA et récupérer la réponse
     */
    public String sendRequest(String prompt) {
        log.info("Envoi d'une requête à l'API IA");
        log.debug("Prompt: {}", prompt.substring(0, Math.min(200, prompt.length())) + "...");

        try {
            // Vérifier que la clé API est configurée
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("Clé API non configurée, utilisation du mode simulation");
                return simulateAiResponse(prompt);
            }

            // Construire la requête
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                    Map.of("role", "user", "content", prompt)
            });
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Envoyer la requête
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Extraire la réponse
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object choices = responseBody.get("choices");

                if (choices instanceof java.util.List && !((java.util.List) choices).isEmpty()) {
                    Map<String, Object> firstChoice = (Map<String, Object>) ((java.util.List) choices).get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String content = (String) message.get("content");

                    log.info("Réponse IA reçue avec succès");
                    log.debug("Réponse: {}", content.substring(0, Math.min(200, content.length())) + "...");

                    return content;
                }
            }

            throw new RuntimeException("Réponse invalide de l'API IA");

        } catch (Exception e) {
            log.error("Erreur lors de la communication avec l'API IA", e);
            throw new RuntimeException("Erreur lors de la communication avec l'API IA: " + e.getMessage(), e);
        }
    }

    /**
     * Simuler une réponse de l'IA pour les tests sans clé API
     */
    private String simulateAiResponse(String prompt) {
        log.info("Mode simulation activé - génération d'une réponse de test");

        // Réponse simulée au format JSON structuré
        return """
        {
          "issues": [
            {
              "title": "Problème de formatage",
              "description": "Le document présente des incohérences de formatage dans plusieurs sections.",
              "category": "FORMATTING",
              "severity": "MEDIUM",
              "location": "Page 1, section Introduction",
              "detectedText": "Texte mal formaté détecté",
              "suggestion": "Uniformiser le formatage en utilisant les styles définis dans le modèle."
            },
            {
              "title": "Manque d'informations obligatoires",
              "description": "Certaines informations requises sont absentes du document.",
              "category": "CONTENT",
              "severity": "HIGH",
              "location": "Page 3, section Références",
              "detectedText": "Références incomplètes",
              "suggestion": "Ajouter toutes les références bibliographiques requises selon les normes."
            },
            {
              "title": "Structure non conforme",
              "description": "L'organisation du document ne suit pas la structure recommandée.",
              "category": "STRUCTURE",
              "severity": "LOW",
              "location": "Document global",
              "detectedText": "Structure générale",
              "suggestion": "Réorganiser les sections selon l'ordre standard: Introduction, Méthodologie, Résultats, Conclusion."
            }
          ]
        }
        """;
    }

    /**
     * Tester la connexion avec l'API IA
     */
    public boolean testConnection() {
        try {
            String testResponse = sendRequest("Test de connexion. Réponds 'OK'.");
            return testResponse != null && !testResponse.isEmpty();
        } catch (Exception e) {
            log.error("Échec du test de connexion avec l'API IA", e);
            return false;
        }
    }

    /**
     * Envoyer une requête avec un timeout personnalisé
     */
    public String sendRequestWithTimeout(String prompt, int timeoutSeconds) {
        log.info("Envoi d'une requête avec timeout de {} secondes", timeoutSeconds);

        // Note: Implémenter le timeout avec RestTemplate configuré
        // Pour l'instant, déléguer à la méthode standard
        return sendRequest(prompt);
    }

    /**
     * Envoyer une requête streaming (pour les longues réponses)
     */
    public String sendStreamingRequest(String prompt) {
        log.info("Envoi d'une requête en mode streaming");

        // Note: Implémenter le streaming pour les réponses longues
        // Pour l'instant, déléguer à la méthode standard
        return sendRequest(prompt);
    }
}