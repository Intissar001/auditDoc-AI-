package com.yourapp.AI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourapp.model.AuditIssue;
import com.yourapp.model.Audit;
import com.yourapp.model.AuditDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser pour transformer les réponses brutes de l'IA en objets structurés
 */
@Component
@Slf4j
public class AiResponseParser {

    private final ObjectMapper objectMapper;

    public AiResponseParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parser la réponse JSON de l'IA et créer des objets AuditIssue
     */
    public List<AuditIssue> parseResponse(String aiResponse, Audit audit, AuditDocument document) {
        log.info("Parsing de la réponse IA pour l'audit {}", audit.getId());

        List<AuditIssue> issues = new ArrayList<>();

        try {
            // Nettoyer la réponse (enlever les backticks markdown si présents)
            String cleanedResponse = cleanJsonResponse(aiResponse);

            log.debug("Réponse nettoyée: {}", cleanedResponse.substring(0, Math.min(200, cleanedResponse.length())));

            // Parser le JSON
            JsonNode rootNode = objectMapper.readTree(cleanedResponse);
            JsonNode issuesNode = rootNode.get("issues");

            if (issuesNode == null || !issuesNode.isArray()) {
                log.warn("Aucun tableau 'issues' trouvé dans la réponse IA");
                return issues;
            }

            // Parcourir tous les problèmes
            for (JsonNode issueNode : issuesNode) {
                AuditIssue issue = parseIssueNode(issueNode, audit, document);
                if (issue != null) {
                    issues.add(issue);
                }
            }

            log.info("{} problème(s) parsé(s) avec succès", issues.size());

        } catch (Exception e) {
            log.error("Erreur lors du parsing de la réponse IA", e);

            // En cas d'erreur, essayer de parser au format texte simple
            issues.addAll(parseFallbackTextResponse(aiResponse, audit, document));
        }

        return issues;
    }

    /**
     * Parser un nœud JSON représentant un problème
     */
    private AuditIssue parseIssueNode(JsonNode issueNode, Audit audit, AuditDocument document) {
        try {
            String issueType = getTextValue(issueNode, "issueType", "Problème détecté");
            String description = getTextValue(issueNode, "description");
            String suggestion = getTextValue(issueNode, "suggestion");
            Integer pageNumber = getIntValue(issueNode, "pageNumber");
            Integer paragraphNumber = getIntValue(issueNode, "paragraphNumber");

            // Créer l'issue
            AuditIssue issue = new AuditIssue();
            issue.setAudit(audit);
            issue.setDocument(document);
            issue.setIssueType(issueType);
            issue.setDescription(description);
            issue.setSuggestion(suggestion);
            issue.setPageNumber(pageNumber);
            issue.setParagraphNumber(paragraphNumber);
            issue.setStatus("Open");

            return issue;

        } catch (Exception e) {
            log.error("Erreur lors du parsing d'un problème individuel", e);
            return null;
        }
    }

    /**
     * Nettoyer la réponse JSON (enlever les backticks markdown, etc.)
     */
    private String cleanJsonResponse(String response) {
        // Enlever les backticks markdown
        response = response.replaceAll("```json\\s*", "");
        response = response.replaceAll("```\\s*", "");

        // Trim les espaces
        response = response.trim();

        // Si la réponse ne commence pas par {, essayer de trouver le début du JSON
        if (!response.startsWith("{")) {
            int startIndex = response.indexOf("{");
            if (startIndex != -1) {
                response = response.substring(startIndex);
            }
        }

        // Si la réponse ne se termine pas par }, essayer de trouver la fin du JSON
        if (!response.endsWith("}")) {
            int endIndex = response.lastIndexOf("}");
            if (endIndex != -1) {
                response = response.substring(0, endIndex + 1);
            }
        }

        return response;
    }

    /**
     * Extraire une valeur texte d'un nœud JSON
     */
    private String getTextValue(JsonNode node, String fieldName) {
        return getTextValue(node, fieldName, null);
    }

    /**
     * Extraire une valeur texte d'un nœud JSON avec valeur par défaut
     */
    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull()) {
            return fieldNode.asText();
        }
        return defaultValue;
    }

    /**
     * Extraire une valeur entière d'un nœud JSON
     */
    private Integer getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && fieldNode.isNumber()) {
            return fieldNode.asInt();
        }
        return null;
    }

    /**
     * Parser une réponse en texte simple (fallback)
     */
    private List<AuditIssue> parseFallbackTextResponse(String response, Audit audit, AuditDocument document) {
        log.info("Tentative de parsing au format texte simple");

        List<AuditIssue> issues = new ArrayList<>();

        try {
            // Créer un problème unique avec la réponse complète
            AuditIssue issue = new AuditIssue();
            issue.setAudit(audit);
            issue.setDocument(document);
            issue.setIssueType("Analyse IA - Réponse non structurée");
            issue.setDescription(response.substring(0, Math.min(500, response.length())));
            issue.setSuggestion("La réponse de l'IA n'était pas au format attendu. Veuillez vérifier manuellement.");
            issue.setStatus("Open");

            issues.add(issue);

        } catch (Exception e) {
            log.error("Échec du parsing fallback", e);
        }

        return issues;
    }

    /**
     * Valider la structure de la réponse JSON
     */
    public boolean validateResponse(String response) {
        try {
            String cleaned = cleanJsonResponse(response);
            JsonNode rootNode = objectMapper.readTree(cleaned);
            JsonNode issuesNode = rootNode.get("issues");

            return issuesNode != null && issuesNode.isArray();
        } catch (Exception e) {
            log.error("Réponse JSON invalide", e);
            return false;
        }
    }

    /**
     * Extraire uniquement les problèmes critiques d'une réponse
     */
    public List<AuditIssue> parseCriticalIssuesOnly(String aiResponse, Audit audit, AuditDocument document) {
        List<AuditIssue> allIssues = parseResponse(aiResponse, audit, document);

        return allIssues.stream()
                .filter(issue -> issue.getIssueType() != null &&
                        (issue.getIssueType().contains("critique") ||
                                issue.getIssueType().contains("critical") ||
                                issue.getIssueType().contains("Non-conformité")))
                .toList();
    }
}