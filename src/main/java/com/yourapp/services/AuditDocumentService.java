package com.yourapp.services;

import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.Audit;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsable de la gestion des documents d'audit
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditDocumentService {

    private final AuditDocumentRepository documentRepository;
    private final AuditRepository auditRepository;

    // Configurer le chemin de stockage des documents
    private final String uploadDir = "uploads/audit-documents/";

    /**
     * Uploader un document pour un audit
     */
    @Transactional
    public AuditDocumentDto uploadDocument(MultipartFile file, Long auditId) {
        log.info("Upload du document {} pour l'audit {}", file.getOriginalFilename(), auditId);

        // Vérifier que l'audit existe
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'entité AuditDocument
            AuditDocument document = new AuditDocument();
            document.setAudit(audit);
            document.setDocumentName(originalFilename);
            document.setDocumentPath(filePath.toString());
            document.setStatus("UPLOADED");

            document = documentRepository.save(document);

            log.info("Document {} uploadé avec succès pour l'audit {}", originalFilename, auditId);
            return mapToDto(document);

        } catch (IOException e) {
            log.error("Erreur lors de l'upload du document", e);
            throw new RuntimeException("Erreur lors de l'upload du document: " + e.getMessage());
        }
    }

    /**
     * Uploader plusieurs documents pour un audit
     */
    @Transactional
    public List<AuditDocumentDto> uploadMultipleDocuments(List<MultipartFile> files, Long auditId) {
        log.info("Upload de {} documents pour l'audit {}", files.size(), auditId);

        return files.stream()
                .map(file -> uploadDocument(file, auditId))
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un document par son ID
     */
    public AuditDocumentDto getDocumentById(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));
        return mapToDto(document);
    }

    /**
     * Récupérer tous les documents d'un audit
     */
    public List<AuditDocumentDto> getDocumentsByAudit(Long auditId) {
        List<AuditDocument> documents = documentRepository.findByAuditId(auditId);
        return documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Télécharger un document
     */
    public Resource downloadDocument(Long documentId) {
        log.info("Téléchargement du document {}", documentId);

        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier: " + document.getDocumentName());
            }
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du document {}", documentId, e);
            throw new RuntimeException("Erreur lors du téléchargement du document: " + e.getMessage());
        }
    }

    /**
     * Mettre à jour le statut d'un document
     */
    @Transactional
    public AuditDocumentDto updateStatus(Long documentId, String status) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        document.setStatus(status);

        if ("ANALYZED".equals(status)) {
            document.setAnalyzedAt(LocalDateTime.now());
        }

        document = documentRepository.save(document);
        return mapToDto(document);
    }

    /**
     * Analyser un document spécifique
     */
    @Transactional
    public AuditDocumentDto analyzeDocument(Long documentId) {
        log.info("Analyse du document {}", documentId);

        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        document.setStatus("PROCESSING");
        documentRepository.save(document);

        try {
            // Logique d'analyse du document (sera implémentée avec l'IA)
            // aiAuditService.analyzeDocument(document.getAudit(), document);

            document.setStatus("ANALYZED");
            document.setAnalyzedAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Erreur lors de l'analyse du document {}", documentId, e);
            document.setStatus("ERROR");
            document.setErrorMessage(e.getMessage());
        }

        document = documentRepository.save(document);
        return mapToDto(document);
    }

    /**
     * Supprimer un document
     */
    @Transactional
    public void deleteDocument(Long documentId) {
        log.info("Suppression du document {}", documentId);

        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            // Supprimer le fichier physique
            Path filePath = Paths.get(document.getDocumentPath());
            Files.deleteIfExists(filePath);

            // Supprimer l'entrée en base de données
            documentRepository.deleteById(documentId);

            log.info("Document {} supprimé avec succès", documentId);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du document {}", documentId, e);
            throw new RuntimeException("Erreur lors de la suppression du document: " + e.getMessage());
        }
    }

    /**
     * Lire le contenu d'un document (pour l'analyse IA)
     */
    public String readDocumentContent(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du document {}", documentId, e);
            throw new RuntimeException("Erreur lors de la lecture du document: " + e.getMessage());
        }
    }

    /**
     * Mapper une entité AuditDocument vers AuditDocumentDto
     */
    private AuditDocumentDto mapToDto(AuditDocument document) {
        return AuditDocumentDto.builder()
                .id(document.getId())
                .auditId(document.getAudit() != null ? document.getAudit().getId() : null)
                .fileName(document.getDocumentName())
                .storagePath(document.getDocumentPath())
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .analyzedAt(document.getAnalyzedAt())
                .errorMessage(document.getErrorMessage())
                .issuesCount(document.getIssuesCount())
                .build();
    }
}