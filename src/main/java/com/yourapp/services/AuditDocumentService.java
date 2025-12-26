package com.yourapp.services;

import com.yourapp.DAO.AuditIssueRepository;
import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.Audit;
import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.DAO.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditDocumentService {

    private final AuditDocumentRepository documentRepository;
    private final AuditRepository auditRepository;
    private final AuditIssueRepository auditIssueRepository;

    private final String uploadDir = "uploads/audit-documents/";

    /**
     * Uploader un document pour un audit et un projet spécifique
     */
    @Transactional
    public AuditDocumentDto uploadDocument(MultipartFile file, Long auditId, Long projectId) {
        // 1. Sauvegarde du fichier physique
        String path = storeFile(file);

        // 2. Récupération de l'audit parent
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        // 3. Création de l'entité Document avec liaison Projet
        AuditDocument doc = new AuditDocument();
        doc.setDocumentName(file.getOriginalFilename());
        doc.setDocumentPath(path);
        doc.setAudit(audit);
        doc.setStatus("UPLOADED");
        doc.setProjectId(projectId); // Liaison Supabase

        // 4. Enregistrement
        AuditDocument savedDoc = documentRepository.save(doc);

        log.info("✅ Document '{}' enregistré et lié au projet ID: {}", file.getOriginalFilename(), projectId);
        return mapToDto(savedDoc);
    }

    /**
     * Méthode interne pour sauvegarder le fichier sur le disque
     */
    private String storeFile(MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetPath = root.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier: " + e.getMessage());
        }
    }

    /**
     * Uploader plusieurs documents (Corrigé pour inclure projectId)
     */
    @Transactional
    public List<AuditDocumentDto> uploadMultipleDocuments(List<MultipartFile> files, Long auditId, Long projectId) {
        log.info("📤 Upload de {} documents pour l'audit {} et projet {}", files.size(), auditId, projectId);

        return files.stream()
                .map(file -> uploadDocument(file, auditId, projectId))
                .collect(Collectors.toList());
    }

    public AuditDocumentDto getDocumentById(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable: " + documentId));
        return mapToDto(document);
    }

    public List<AuditDocumentDto> getDocumentsByAudit(Long auditId) {
        return documentRepository.findByAuditId(auditId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Resource downloadDocument(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) return resource;
            else throw new RuntimeException("Fichier non lisible");
        } catch (Exception e) {
            throw new RuntimeException("Erreur téléchargement: " + e.getMessage());
        }
    }

    @Transactional
    public AuditDocumentDto updateStatus(Long documentId, String status) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        document.setStatus(status);
        if ("ANALYZED".equals(status)) {
            document.setAnalyzedAt(LocalDateTime.now());
        }
        return mapToDto(documentRepository.save(document));
    }

    public String readDocumentContent(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            String fileName = document.getDocumentName().toLowerCase();

            if (fileName.endsWith(".txt")) return readTextFile(filePath);
            if (fileName.endsWith(".docx")) return readDocxFile(filePath);
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) return readExcelFile(filePath);
            // Pour le PDF, utilisez votre méthode existante ou Apache PDFBox
            return "[Contenu non extrait pour ce format]";
        } catch (Exception e) {
            throw new RuntimeException("Erreur lecture: " + e.getMessage());
        }
    }

    private String readTextFile(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    private String readDocxFile(Path filePath) throws IOException {
        try (InputStream fis = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String readExcelFile(Path filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream fis = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        content.append(getCellValueAsString(cell)).append("\t");
                    }
                    content.append("\n");
                }
            }
        }
        return content.toString();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * Supprimer un document et toutes les dépendances (Issues)
     */
    @Transactional
    public void deleteDocument(Long documentId) {
        log.info("🗑️ Tentative de suppression du document ID: {}", documentId);

        // 1. Récupérer le document pour vérifier s'il existe et avoir son chemin de fichier
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            // 2. Supprimer d'abord les "issues" liées dans la table audit_issue
            // Cette méthode doit être définie dans votre AuditIssueRepository
            auditIssueRepository.deleteByDocumentId(documentId);
            log.info("✅ Issues liées au document {} supprimées", documentId);

            // 3. Supprimer le fichier physique sur le disque
            Path filePath = Paths.get(document.getDocumentPath());
            Files.deleteIfExists(filePath);

            // 4. Supprimer l'entrée dans la table auditdocument
            documentRepository.delete(document);

            log.info("✅ Document et enregistrements associés supprimés avec succès");
        } catch (IOException e) {
            log.error("❌ Erreur lors de la suppression du fichier physique", e);
            throw new RuntimeException("Erreur lors de la suppression du fichier : " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression en base de données", e);
            throw new RuntimeException("Erreur SQL : " + e.getMessage());
        }
    }

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
                // Ajoutez ceci dans votre AuditDocumentDto si ce n'est pas déjà fait
                // .projectId(document.getProjectId())
                .build();
    }
}