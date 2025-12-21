package com.yourapp.AI;

import com.yourapp.model.AuditTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * Constructeur de prompts pour l'intelligence artificielle
 * Génère dynamiquement les prompts en fonction du template et du contenu
 */
@Component
@Slf4j
public class AiPromptBuilder {

    /**
     * Construire un prompt complet pour l'analyse d'un document
     */
    public String buildPrompt(AuditTemplate template, String documentContent, String documentName) {
        log.info("Construction du prompt pour le document: {}", documentName);

        StringBuilder prompt = new StringBuilder();

        // En-tête du prompt
        prompt.append("Tu es un assistant d'audit documentaire spécialisé. ");
        prompt.append("Ta tâche est d'analyser le document fourni et d'identifier tous les problèmes selon les critères définis.\n\n");

        // Informations sur le template d'analyse
        prompt.append("=== MODÈLE D'ANALYSE ===\n");
        prompt.append("Nom du modèle: ").append(template.getName()).append("\n");

        if (template.getDescription() != null && !template.getDescription().isEmpty()) {
            prompt.append("Description: ").append(template.getDescription()).append("\n");
        }

        if (template.getOrganization() != null && !template.getOrganization().isEmpty()) {
            prompt.append("Organisation: ").append(template.getOrganization()).append("\n");
        }

        prompt.append("Nombre de règles: ").append(template.getRuleCount()).append("\n");

        // Instructions pour le format de réponse
        prompt.append("\n=== FORMAT DE RÉPONSE REQUIS ===\n");
        prompt.append("Tu dois répondre UNIQUEMENT au format JSON suivant, sans texte supplémentaire:\n");
        prompt.append("{\n");
        prompt.append("  \"issues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"issueType\": \"Type du problème\",\n");
        prompt.append("      \"description\": \"Description détaillée du problème\",\n");
        prompt.append("      \"pageNumber\": 1,\n");
        prompt.append("      \"paragraphNumber\": 2,\n");
        prompt.append("      \"suggestion\": \"Suggestion d'amélioration ou correction\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");

        // Types de problèmes
        prompt.append("=== TYPES DE PROBLÈMES POSSIBLES ===\n");
        prompt.append("- Non-conformité réglementaire\n");
        prompt.append("- Erreur de formatage\n");
        prompt.append("- Information manquante\n");
        prompt.append("- Incohérence dans le contenu\n");
        prompt.append("- Problème de structure\n");
        prompt.append("- Erreur grammaticale ou orthographique\n\n");

        // Document à analyser
        prompt.append("=== DOCUMENT À ANALYSER ===\n");
        prompt.append("Nom du document: ").append(documentName).append("\n\n");
        prompt.append("Contenu:\n");
        prompt.append("---\n");
        prompt.append(documentContent).append("\n");
        prompt.append("---\n\n");

        // Instructions finales
        prompt.append("Analyse ce document attentivement et identifie TOUS les problèmes. ");
        prompt.append("Sois précis dans tes descriptions et tes suggestions. ");
        prompt.append("Indique le numéro de page et de paragraphe si possible. ");
        prompt.append("Réponds UNIQUEMENT avec le JSON structuré, sans texte avant ou après.\n");

        log.debug("Prompt construit avec succès. Longueur: {} caractères", prompt.length());

        return prompt.toString();
    }

    /**
     * Construire un prompt simple pour des tests rapides
     */
    public String buildSimplePrompt(String text) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Analyse le texte suivant et identifie les problèmes:\n\n");
        prompt.append(text).append("\n\n");
        prompt.append("Réponds au format JSON avec la structure suivante:\n");
        prompt.append("{\n");
        prompt.append("  \"issues\": [\n");
        prompt.append("    {\"issueType\": \"...\", \"description\": \"...\", \"suggestion\": \"...\"}\n");
        prompt.append("  ]\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    /**
     * Construire un prompt pour une analyse comparative
     */
    public String buildComparativePrompt(AuditTemplate template, String document1, String document2) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Tu es un assistant d'audit documentaire. Compare les deux documents suivants ");
        prompt.append("selon le modèle d'analyse '").append(template.getName()).append("'.\n\n");

        prompt.append("=== DOCUMENT 1 ===\n");
        prompt.append(document1).append("\n\n");

        prompt.append("=== DOCUMENT 2 ===\n");
        prompt.append(document2).append("\n\n");

        prompt.append("Identifie les différences, incohérences et problèmes entre ces documents.\n");
        prompt.append("Réponds au format JSON standard avec la liste des problèmes détectés.\n");

        return prompt.toString();
    }

    /**
     * Construire un prompt pour une validation de conformité
     */
    public String buildCompliancePrompt(AuditTemplate template, String documentContent, String standard) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Tu es un expert en conformité documentaire. ");
        prompt.append("Vérifie la conformité du document suivant par rapport au standard: ");
        prompt.append(standard).append("\n\n");

        if (template.getDescription() != null) {
            prompt.append("=== CRITÈRES DE CONFORMITÉ ===\n");
            prompt.append(template.getDescription()).append("\n\n");
        }

        prompt.append("=== DOCUMENT À VÉRIFIER ===\n");
        prompt.append(documentContent).append("\n\n");

        prompt.append("Liste tous les points de non-conformité au format JSON standard.\n");

        return prompt.toString();
    }

    /**
     * Construire un prompt pour une analyse de qualité
     */
    public String buildQualityPrompt(AuditTemplate template, String documentContent) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Tu es un évaluateur de qualité documentaire. ");
        prompt.append("Évalue la qualité du document suivant selon ces aspects:\n");
        prompt.append("- Clarté et lisibilité\n");
        prompt.append("- Structure et organisation\n");
        prompt.append("- Cohérence et logique\n");
        prompt.append("- Complétude des informations\n");
        prompt.append("- Respect des bonnes pratiques\n\n");

        if (template.getDescription() != null) {
            prompt.append("Critères d'évaluation: ").append(template.getDescription()).append("\n\n");
        }

        prompt.append("=== DOCUMENT ===\n");
        prompt.append(documentContent).append("\n\n");

        prompt.append("Identifie tous les points d'amélioration et fournis des suggestions concrètes.\n");
        prompt.append("Réponds au format JSON standard.\n");

        return prompt.toString();
    }

    /**
     * Ajouter des instructions supplémentaires au prompt
     */
    public String addInstructions(String basePrompt, String additionalInstructions) {
        return basePrompt + "\n\n=== INSTRUCTIONS SUPPLÉMENTAIRES ===\n" + additionalInstructions + "\n";
    }

    /**
     * Limiter la longueur du contenu pour éviter de dépasser les limites de tokens
     */
    public String truncateContent(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }

        log.warn("Contenu tronqué de {} à {} caractères", content.length(), maxLength);
        return content.substring(0, maxLength) + "\n\n[... contenu tronqué ...]";
    }
}