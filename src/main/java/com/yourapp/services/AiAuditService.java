package com.yourapp.services;

import com.yourapp.AI.AiClient;
import com.yourapp.AI.AiPromptBuilder;
import com.yourapp.AI.AiResponseParser;
import com.yourapp.model.Audit;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.AuditIssue;
import com.yourapp.model.AuditTemplate;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.AuditTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsable de l'intégration avec l'intelligence artificielle
 * Orchestre l'analyse des documents via l'IA
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiAuditService {

    private final AiClient aiClient;
    private final AiPromptBuilder promptBuilder;
    private final AiResponseParser responseParser;
    private final AuditDocumentService documentService;
    private final AuditDocumentRepository documentRepository;
    private final AuditTemplateRepository templateRepository;
    private final AuditIssueService issueService;

    /**
     * Analyser un audit complet avec tous ses documents
     */
    @Transactional
    public void analyzeAudit(Audit audit) {
        log.info("Démarrage de l'analyse IA pour l'audit {}", audit.getId());

        try {
            // Récupérer le template (modèle) d'analyse
            AuditTemplate template = templateRepository.findById(audit.getModelId())
                    .orElseThrow(() -> new RuntimeException("Template introuvable avec l'ID: " + audit.getModelId()));

            // Récupérer tous les documents de l'audit
            List<AuditDocument> documents = documentRepository.findByAuditId(audit.getId());

            if (documents.isEmpty()) {
                throw new RuntimeException("Aucun document à analyser pour l'audit " + audit.getId());
            }

            // Analyser chaque document
            for (AuditDocument document : documents) {
                analyzeDocument(audit, document, template);
            }

            log.info("Analyse IA terminée pour l'audit {}", audit.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse IA de l'audit {}", audit.getId(), e);
            throw new RuntimeException("Erreur lors de l'analyse IA: " + e.getMessage(), e);
        }
    }

    /**
     * Analyser un document spécifique
     */
    @Transactional
    public void analyzeDocument(Audit audit, AuditDocument document, AuditTemplate template) {
        log.info("Analyse du document {} pour l'audit {}", document.getId(), audit.getId());

        try {
            // Mettre à jour le statut du document
            document.setStatus("PROCESSING");
            documentRepository.save(document);

            // Lire le contenu du document
            String documentContent = documentService.readDocumentContent(document.getId());

            // Construire le prompt avec le template et le contenu du document
            String prompt = promptBuilder.buildPrompt(template, documentContent, document.getDocumentName());

            log.debug("Prompt construit pour le document {}: {}", document.getId(),
                    prompt.substring(0, Math.min(200, prompt.length())) + "...");

            // Envoyer la requête à l'IA
            String aiResponse = aiClient.sendRequest(prompt);

            log.debug("Réponse IA reçue pour le document {}", document.getId());

            // Parser la réponse de l'IA et créer les issues
            List<AuditIssue> issues = responseParser.parseResponse(aiResponse, audit, document);

            // Sauvegarder les issues détectées
            issueService.saveIssues(issues);

            // Mettre à jour le statut du document
            document.setStatus("ANALYZED");
            document.setAnalyzedAt(java.time.LocalDateTime.now());
            document.setIssuesCount(issues.size());
            documentRepository.save(document);

            log.info("Document {} analysé avec succès. {} problèmes détectés.",
                    document.getId(), issues.size());

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse du document {}", document.getId(), e);

            // Mettre à jour le statut en erreur
            document.setStatus("ERROR");
            document.setErrorMessage(e.getMessage());
            documentRepository.save(document);

            throw new RuntimeException("Erreur lors de l'analyse du document: " + e.getMessage(), e);
        }
    }

    /**
     * Analyser un texte simple (pour les tests)
     */
    public List<AuditIssue> analyzeText(String text, AuditTemplate template, Audit audit) {
        log.info("Analyse d'un texte simple pour l'audit {}", audit.getId());

        try {
            // Construire le prompt
            String prompt = promptBuilder.buildPrompt(template, text, "text_sample");

            // Envoyer la requête à l'IA
            String aiResponse = aiClient.sendRequest(prompt);

            // Parser la réponse
            List<AuditIssue> issues = responseParser.parseResponse(aiResponse, audit, null);

            log.info("Analyse du texte terminée. {} problèmes détectés.", issues.size());
            return issues;

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse du texte", e);
            throw new RuntimeException("Erreur lors de l'analyse du texte: " + e.getMessage(), e);
        }
    }

    /**
     * Tester la connexion avec l'IA
     */
    public boolean testAiConnection() {
        try {
            String testPrompt = "Test de connexion. Réponds simplement 'OK'.";
            String response = aiClient.sendRequest(testPrompt);
            log.info("Test de connexion IA réussi. Réponse: {}", response);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.error("Échec du test de connexion IA", e);
            return false;
        }
    }

    /**
     * Analyser un document avec un modèle personnalisé (prompt custom)
     */
    @Transactional
    public List<AuditIssue> analyzeWithCustomPrompt(Audit audit, Long documentId, String customPrompt) {
        log.info("Analyse avec prompt personnalisé pour le document {}", documentId);

        try {
            // Lire le contenu du document
            String documentContent = documentService.readDocumentContent(documentId);

            // Récupérer le document
            AuditDocument document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document introuvable"));

            // Construire le prompt complet
            String fullPrompt = customPrompt + "\n\nContenu du document:\n" + documentContent;

            // Envoyer la requête
            String aiResponse = aiClient.sendRequest(fullPrompt);

            // Parser la réponse
            List<AuditIssue> issues = responseParser.parseResponse(aiResponse, audit, document);

            // Sauvegarder les issues
            issueService.saveIssues(issues);

            log.info("Analyse personnalisée terminée. {} problèmes détectés.", issues.size());
            return issues;

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse personnalisée", e);
            throw new RuntimeException("Erreur lors de l'analyse personnalisée: " + e.getMessage(), e);
        }
    }
}