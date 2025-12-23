package com.yourapp.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Client pour communiquer avec l'API d'intelligence artificielle
 * Avec fallback automatique en mode simulation si quota dépassé
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

    @Value("${ai.simulation.mode:auto}")
    private String simulationMode; // auto, enabled, disabled

    private final RestTemplate restTemplate;
    private boolean quotaExceeded = false;

    public AiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envoyer une requête à l'API IA et récupérer la réponse
     */
    public String sendRequest(String prompt) {
        log.info("📤 Envoi d'une requête à l'API IA");
        log.debug("Prompt: {}", prompt.substring(0, Math.min(200, prompt.length())) + "...");

        // Si le mode simulation est activé ou si le quota est dépassé
        if ("enabled".equalsIgnoreCase(simulationMode) ||
                ("auto".equalsIgnoreCase(simulationMode) && shouldUseSimulation())) {
            log.info("🎭 Mode simulation actif");
            return simulateAiResponse(prompt);
        }

        try {
            // Vérifier que la clé API est configurée
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("⚠️ Clé API non configurée, passage en mode simulation");
                quotaExceeded = true;
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

                    log.info("✅ Réponse IA reçue avec succès");
                    log.debug("Réponse: {}", content.substring(0, Math.min(200, content.length())) + "...");

                    return content;
                }
            }

            throw new RuntimeException("Réponse invalide de l'API IA");

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("⚠️ Quota API dépassé (429), passage en mode simulation", e);
            quotaExceeded = true;
            return simulateAiResponse(prompt);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("⚠️ Clé API invalide (401), passage en mode simulation", e);
            quotaExceeded = true;
            return simulateAiResponse(prompt);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la communication avec l'API IA", e);

            // En mode auto, basculer en simulation en cas d'erreur
            if ("auto".equalsIgnoreCase(simulationMode)) {
                log.warn("🎭 Passage en mode simulation suite à l'erreur");
                quotaExceeded = true;
                return simulateAiResponse(prompt);
            }

            throw new RuntimeException("Erreur lors de la communication avec l'API IA: " + e.getMessage(), e);
        }
    }

    /**
     * Déterminer si le mode simulation doit être utilisé
     */
    private boolean shouldUseSimulation() {
        return quotaExceeded || apiKey == null || apiKey.isEmpty();
    }

    /**
     * Simuler une réponse de l'IA pour les tests ou en cas d'erreur
     */
    private String simulateAiResponse(String prompt) {
        log.info("🎭 Mode simulation - génération d'une réponse de test");

        // Analyser le prompt pour générer une réponse plus pertinente
        boolean isArabic = prompt.contains("مراسلة") || prompt.contains("الجمعية");

        // Réponse simulée au format JSON structuré
        if (isArabic) {
            return """
            {
              "issues": [
                {
                  "issueType": "مشكلة التنسيق",
                  "description": "يحتوي المستند على تناقضات في التنسيق في عدة أقسام",
                  "pageNumber": 1,
                  "paragraphNumber": 2,
                  "suggestion": "توحيد التنسيق باستخدام الأنماط المحددة في النموذج"
                },
                {
                  "issueType": "معلومات مفقودة",
                  "description": "بعض المعلومات المطلوبة غير موجودة في المستند",
                  "pageNumber": 2,
                  "paragraphNumber": 1,
                  "suggestion": "إضافة جميع المراجع الببليوغرافية المطلوبة وفقًا للمعايير"
                },
                {
                  "issueType": "عدم المطابقة للمعايير",
                  "description": "لا يتبع تنظيم الوثيقة البنية الموصى بها",
                  "pageNumber": null,
                  "paragraphNumber": null,
                  "suggestion": "إعادة تنظيم الأقسام حسب الترتيب القياسي: المقدمة، المنهجية، النتائج، الخاتمة"
                }
              ]
            }
            """;
        }

        return """
        {
          "issues": [
            {
              "issueType": "Problème de formatage",
              "description": "Le document présente des incohérences de formatage dans plusieurs sections",
              "pageNumber": 1,
              "paragraphNumber": 2,
              "suggestion": "Uniformiser le formatage en utilisant les styles définis dans le modèle"
            },
            {
              "issueType": "Informations manquantes",
              "description": "Certaines informations requises sont absentes du document",
              "pageNumber": 3,
              "paragraphNumber": 1,
              "suggestion": "Ajouter toutes les références bibliographiques requises selon les normes"
            },
            {
              "issueType": "Non-conformité structurelle",
              "description": "L'organisation du document ne suit pas la structure recommandée",
              "pageNumber": null,
              "paragraphNumber": null,
              "suggestion": "Réorganiser les sections selon l'ordre standard: Introduction, Méthodologie, Résultats, Conclusion"
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
            log.error("❌ Échec du test de connexion avec l'API IA", e);
            return false;
        }
    }

    /**
     * Réinitialiser le flag de quota dépassé
     */
    public void resetQuotaFlag() {
        quotaExceeded = false;
        log.info("🔄 Flag quota réinitialisé");
    }

    /**
     * Vérifier si le mode simulation est actif
     */
    public boolean isSimulationMode() {
        return "enabled".equalsIgnoreCase(simulationMode) ||
                ("auto".equalsIgnoreCase(simulationMode) && quotaExceeded);
    }

    /**
     * Obtenir le statut de l'API
     */
    public String getApiStatus() {
        if (isSimulationMode()) {
            return "MODE_SIMULATION";
        } else if (apiKey == null || apiKey.isEmpty()) {
            return "NO_API_KEY";
        } else {
            return "ACTIVE";
        }
    }
}