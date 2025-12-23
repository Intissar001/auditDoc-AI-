package com.yourapp.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service d'extraction de contenu pour tous types de documents
 * Supporte: PDF, DOCX, DOC, XLSX, XLS, TXT
 */
@Service
@Slf4j
public class DocumentContentExtractor {

    /**
     * Extraire le contenu d'un document selon son type
     */
    public String extractContent(Path filePath, String fileName) throws IOException {
        log.info("📖 Extraction du contenu de: {}", fileName);

        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".txt")) {
            return extractTextContent(filePath);
        } else if (lowerFileName.endsWith(".pdf")) {
            return extractPdfContent(filePath);
        } else if (lowerFileName.endsWith(".docx")) {
            return extractDocxContent(filePath);
        } else if (lowerFileName.endsWith(".doc")) {
            return extractDocContent(filePath);
        } else if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls")) {
            return extractExcelContent(filePath);
        } else {
            log.warn("⚠️ Type de fichier non supporté: {}", fileName);
            return "";
        }
    }

    /**
     * Extraire le contenu d'un fichier texte
     */
    private String extractTextContent(Path filePath) throws IOException {
        log.info("📄 Extraction TXT");
        return Files.readString(filePath);
    }

    /**
     * Extraire le contenu d'un fichier PDF avec Apache PDFBox
     */
    private String extractPdfContent(Path filePath) throws IOException {
        log.info("📄 Extraction PDF avec PDFBox");

        try (PDDocument document = PDDocument.load(filePath.toFile())) {

            PDFTextStripper stripper = new PDFTextStripper();

            // Configuration pour améliorer l'extraction
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            log.info("✅ PDF extrait: {} pages, {} caractères",
                    document.getNumberOfPages(), text.length());

            return text;
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction PDF", e);
            throw new IOException("Erreur extraction PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Extraire le contenu d'un fichier DOCX
     */
    private String extractDocxContent(Path filePath) throws IOException {
        log.info("📄 Extraction DOCX");

        try (InputStream inputStream = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String text = extractor.getText();

            log.info("✅ DOCX extrait: {} caractères", text.length());
            return text;
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction DOCX", e);
            throw new IOException("Erreur extraction DOCX: " + e.getMessage(), e);
        }
    }

    /**
     * Extraire le contenu d'un fichier DOC (ancien format)
     */
    private String extractDocContent(Path filePath) throws IOException {
        log.info("📄 Extraction DOC");

        try (InputStream inputStream = Files.newInputStream(filePath);
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {

            String text = extractor.getText();

            log.info("✅ DOC extrait: {} caractères", text.length());
            return text;
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction DOC", e);
            throw new IOException("Erreur extraction DOC: " + e.getMessage(), e);
        }
    }

    /**
     * Extraire le contenu d'un fichier Excel (XLSX ou XLS)
     */
    private String extractExcelContent(Path filePath) throws IOException {
        log.info("📄 Extraction Excel");

        StringBuilder content = new StringBuilder();

        try (InputStream inputStream = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            int totalRows = 0;

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                content.append("\n=== Feuille: ").append(sheet.getSheetName()).append(" ===\n\n");

                for (Row row : sheet) {
                    totalRows++;
                    boolean hasContent = false;

                    for (Cell cell : row) {
                        String cellValue = getCellValueAsString(cell);
                        if (!cellValue.isEmpty()) {
                            content.append(cellValue).append("\t");
                            hasContent = true;
                        }
                    }

                    if (hasContent) {
                        content.append("\n");
                    }
                }
                content.append("\n");
            }

            log.info("✅ Excel extrait: {} feuilles, {} lignes, {} caractères",
                    workbook.getNumberOfSheets(), totalRows, content.length());

            return content.toString();

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction Excel", e);
            throw new IOException("Erreur extraction Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Obtenir la valeur d'une cellule Excel en tant que String
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    // Formater les nombres pour éviter la notation scientifique
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.format("%d", (long) numValue);
                    }
                    return String.format("%.2f", numValue);

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        return cell.getCellFormula();
                    }

                case BLANK:
                    return "";

                default:
                    return "";
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur lors de la lecture d'une cellule: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Extraire un aperçu du contenu (premiers N caractères)
     */
    public String extractPreview(Path filePath, String fileName, int maxChars) throws IOException {
        String fullContent = extractContent(filePath, fileName);

        if (fullContent.length() <= maxChars) {
            return fullContent;
        }

        return fullContent.substring(0, maxChars) + "...";
    }

    /**
     * Vérifier si un type de fichier est supporté
     */
    public boolean isSupported(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".txt") ||
                lowerFileName.endsWith(".pdf") ||
                lowerFileName.endsWith(".docx") ||
                lowerFileName.endsWith(".doc") ||
                lowerFileName.endsWith(".xlsx") ||
                lowerFileName.endsWith(".xls");
    }
}