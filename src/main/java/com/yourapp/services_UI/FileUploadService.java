package com.yourapp.services_UI;

import com.yourapp.dto.AuditDocumentDto;
import com.yourapp.services.AuditDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Service UI pour gérer l'upload et l'extraction de contenu de fichiers
 * Intermédiaire entre JavaFX et le service backend
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AuditDocumentService documentService;

    /**
     * Upload un seul fichier pour un audit avec extraction du contenu
     */
    public AuditDocumentDto uploadFile(File file, Long auditId, Long projectId) {
        log.info("📤 Upload du fichier: {} pour l'audit ID: {} et Projet ID: {}", file.getName(), auditId, projectId);

        if (!validateFile(file)) {
            throw new RuntimeException("Fichier invalide: " + file.getName());
        }

        try {
            org.springframework.web.multipart.MultipartFile multipartFile = convertFileToMultipartFile(file);
            // On ajoute projectId à l'appel
            return documentService.uploadDocument(multipartFile, auditId, projectId);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload du fichier: {}", file.getName(), e);
            throw new RuntimeException("Impossible d'uploader le fichier: " + e.getMessage(), e);
        }
    }

    /**
     * Upload multiple fichiers pour un audit
     */
    public List<AuditDocumentDto> uploadMultipleFiles(List<File> files, Long auditId, Long projectId) {
        List<AuditDocumentDto> uploadedDocuments = new ArrayList<>();
        for (File file : files) {
            try {
                // On passe le projectId ici
                AuditDocumentDto document = uploadFile(file, auditId, projectId);
                uploadedDocuments.add(document);
            } catch (Exception e) {
                log.error("❌ Échec de l'upload du fichier: {}", file.getName(), e);
            }
        }
        return uploadedDocuments;
    }

    /**
     * Extraire le contenu d'un fichier selon son type
     */
    public String extractFileContent(File file) {
        log.info("📖 Extraction du contenu de: {}", file.getName());

        try {
            String fileName = file.getName().toLowerCase();

            if (fileName.endsWith(".txt")) {
                return extractTextContent(file);
            } else if (fileName.endsWith(".pdf")) {
                return extractPdfContent(file);
            } else if (fileName.endsWith(".docx")) {
                return extractDocxContent(file);
            } else if (fileName.endsWith(".doc")) {
                return extractDocContent(file);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                return extractExcelContent(file);
            } else {
                log.warn("⚠️ Type de fichier non supporté pour extraction: {}", fileName);
                return "";
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction du contenu de: {}", file.getName(), e);
            return "";
        }
    }

    /**
     * Extraire le contenu d'un fichier texte
     */
    private String extractTextContent(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    /**
     * Extraire le contenu d'un fichier PDF
     */
    private String extractPdfContent(File file) throws IOException {
        log.info("📄 Extraction PDF avec PDFBox: {}", file.getName());

        try (org.apache.pdfbox.pdmodel.PDDocument document =
                     org.apache.pdfbox.pdmodel.PDDocument.load(file)) {

            org.apache.pdfbox.text.PDFTextStripper stripper =
                    new org.apache.pdfbox.text.PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);
            log.info("✅ PDF extrait: {} pages, {} caractères",
                    document.getNumberOfPages(), text.length());

            return text;
        } catch (Exception e) {
            log.error("❌ Erreur extraction PDF", e);
            throw new IOException("Erreur extraction PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Extraire le contenu d'un fichier DOCX
     */
    private String extractDocxContent(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String content = extractor.getText();
            log.info("✅ Contenu DOCX extrait: {} caractères", content.length());
            return content;
        }
    }

    /**
     * Extraire le contenu d'un fichier DOC (ancien format)
     */
    private String extractDocContent(File file) throws IOException {
        log.info("📄 Extraction DOC: {}", file.getName());
        // Pour les fichiers .doc, nécessite Apache POI HWPF
        return "[Contenu DOC - Extraction nécessite Apache POI HWPF]";
    }

    /**
     * Extraire le contenu d'un fichier Excel
     */
    private String extractExcelContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
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

            log.info("✅ Contenu Excel extrait: {} caractères", content.length());
            return content.toString();
        }
    }

    /**
     * Obtenir la valeur d'une cellule Excel en tant que String
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
     * Récupérer tous les documents d'un audit
     */
    public List<AuditDocumentDto> getDocumentsByAudit(Long auditId) {
        log.info("📥 Récupération des documents de l'audit ID: {}", auditId);

        try {
            List<AuditDocumentDto> documents = documentService.getDocumentsByAudit(auditId);
            log.info("✅ {} documents récupérés", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des documents", e);
            throw new RuntimeException("Impossible de récupérer les documents: " + e.getMessage(), e);
        }
    }

    /**
     * Supprimer un document
     */
    public void deleteDocument(Long documentId) {
        log.info("🗑️ Suppression du document ID: {}", documentId);

        try {
            documentService.deleteDocument(documentId);
            log.info("✅ Document supprimé avec succès");
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression du document", e);
            throw new RuntimeException("Impossible de supprimer le document: " + e.getMessage(), e);
        }
    }

    /**
     * Valider qu'un fichier peut être uploadé
     */
    public boolean validateFile(File file) {
        if (file == null || !file.exists()) {
            log.warn("⚠️ Fichier inexistant ou null");
            return false;
        }

        if (!file.canRead()) {
            log.warn("⚠️ Fichier non lisible: {}", file.getName());
            return false;
        }

        // Vérifier la taille (max 50MB)
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.length() > maxSize) {
            log.warn("⚠️ Fichier trop volumineux: {} (max 50MB)", file.getName());
            return false;
        }

        // Vérifier l'extension
        String fileName = file.getName().toLowerCase();
        boolean validExtension = fileName.endsWith(".pdf") ||
                fileName.endsWith(".docx") ||
                fileName.endsWith(".doc") ||
                fileName.endsWith(".xlsx") ||
                fileName.endsWith(".xls") ||
                fileName.endsWith(".txt");

        if (!validExtension) {
            log.warn("⚠️ Extension de fichier non supportée: {}", file.getName());
            return false;
        }

        return true;
    }

    /**
     * Valider une liste de fichiers
     */
    public List<File> validateFiles(List<File> files) {
        List<File> validFiles = new ArrayList<>();

        for (File file : files) {
            if (validateFile(file)) {
                validFiles.add(file);
            }
        }

        return validFiles;
    }

    /**
     * Convertir un File Java en MultipartFile Spring
     */
    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        String contentType = getContentType(file);

        return new MockMultipartFile(
                "file",
                file.getName(),
                contentType,
                input
        );
    }

    /**
     * Déterminer le Content-Type basé sur l'extension du fichier
     */
    private String getContentType(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }
}