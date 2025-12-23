package com.yourapp.services;

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

/**
 * Service responsable de la gestion des documents d'audit
 * Supporte tous les types de documents avec extraction de contenu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditDocumentService {

    private final AuditDocumentRepository documentRepository;
    private final AuditRepository auditRepository;

    private final String uploadDir = "uploads/audit-documents/";

    /**
     * Uploader un document pour un audit
     */
    @Transactional
    public AuditDocumentDto uploadDocument(MultipartFile file, Long auditId) {
        log.info("📤 Upload du document {} pour l'audit {}", file.getOriginalFilename(), auditId);

        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit introuvable avec l'ID: " + auditId));

        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            AuditDocument document = new AuditDocument();
            document.setAudit(audit);
            document.setDocumentName(originalFilename);
            document.setDocumentPath(filePath.toString());
            document.setStatus("UPLOADED");

            document = documentRepository.save(document);

            log.info("✅ Document {} uploadé avec succès", originalFilename);
            return mapToDto(document);

        } catch (IOException e) {
            log.error("❌ Erreur lors de l'upload du document", e);
            throw new RuntimeException("Erreur lors de l'upload du document: " + e.getMessage());
        }
    }

    /**
     * Uploader plusieurs documents pour un audit
     */
    @Transactional
    public List<AuditDocumentDto> uploadMultipleDocuments(List<MultipartFile> files, Long auditId) {
        log.info("📤 Upload de {} documents pour l'audit {}", files.size(), auditId);

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
        log.info("⬇️ Téléchargement du document {}", documentId);

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
            log.error("❌ Erreur lors du téléchargement du document {}", documentId, e);
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
     * Lire le contenu d'un document selon son type
     */
    public String readDocumentContent(Long documentId) {
        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            String fileName = document.getDocumentName().toLowerCase();

            if (fileName.endsWith(".txt")) {
                return readTextFile(filePath);
            } else if (fileName.endsWith(".docx")) {
                return readDocxFile(filePath);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                return readExcelFile(filePath);
            } else if (fileName.endsWith(".pdf")) {
                return readPdfFile(filePath);
            } else {
                log.warn("⚠️ Type de fichier non supporté pour lecture: {}", fileName);
                return "";
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la lecture du document {}", documentId, e);
            throw new RuntimeException("Erreur lors de la lecture du document: " + e.getMessage());
        }
    }

    /**
     * Lire un fichier texte
     */
    private String readTextFile(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    /**
     * Lire un fichier DOCX
     */
    private String readDocxFile(Path filePath) throws IOException {
        try (InputStream fis = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }

    /**
     * Lire un fichier Excel
     */
    private String readExcelFile(Path filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (InputStream fis = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                content.append("=== Feuille: ").append(sheet.getSheetName()).append(" ===\n\n");

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = getCellValueAsString(cell);
                        if (!cellValue.isEmpty()) {
                            content.append(cellValue).append("\t");
                        }
                    }
                    content.append("\n");
                }
                content.append("\n");
            }
        }

        return content.toString();
    }

    /**
     * Obtenir la valeur d'une cellule Excel
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Lire un fichier PDF (placeholder - nécessite Apache PDFBox)
     */
    private String readPdfFile(Path filePath) throws IOException {
        log.info("📄 Lecture PDF: {}", filePath.getFileName());
        // Nécessite l'ajout de Apache PDFBox dans les dépendances
        return "[Contenu PDF - Ajoutez Apache PDFBox pour extraction complète]";
    }

    /**
     * Supprimer un document
     */
    @Transactional
    public void deleteDocument(Long documentId) {
        log.info("🗑️ Suppression du document {}", documentId);

        AuditDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID: " + documentId));

        try {
            Path filePath = Paths.get(document.getDocumentPath());
            Files.deleteIfExists(filePath);

            documentRepository.deleteById(documentId);

            log.info("✅ Document {} supprimé avec succès", documentId);
        } catch (IOException e) {
            log.error("❌ Erreur lors de la suppression du document {}", documentId, e);
            throw new RuntimeException("Erreur lors de la suppression du document: " + e.getMessage());
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