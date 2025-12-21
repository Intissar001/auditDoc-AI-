package com.yourapp.services_UI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourapp.dto.AuditDocumentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service JavaFX pour gérer l'upload de fichiers vers l'API
 */
@Service
@Slf4j
public class FileUploadService {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FileUploadService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Upload un seul fichier pour un audit
     */
    public AuditDocumentDto uploadFile(File file, Long auditId) {
        log.info("Upload du fichier: {} pour l'audit ID: {}", file.getName(), auditId);

        try {
            String url = baseUrl + "/api/audit-documents/upload";
            String boundary = "----Boundary" + UUID.randomUUID().toString().replace("-", "");

            // Construire le corps multipart
            byte[] multipartBody = buildMultipartBody(file, auditId, boundary);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                AuditDocumentDto document = objectMapper.readValue(
                        response.body(),
                        AuditDocumentDto.class
                );

                log.info("✅ Fichier uploadé avec succès: {}", file.getName());
                return document;
            } else {
                log.error("❌ Erreur upload: Code {}", response.statusCode());
                throw new RuntimeException("Erreur lors de l'upload du fichier: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload du fichier: {}", file.getName(), e);
            throw new RuntimeException("Impossible d'uploader le fichier: " + e.getMessage(), e);
        }
    }

    /**
     * Upload multiple fichiers pour un audit
     */
    public List<AuditDocumentDto> uploadMultipleFiles(List<File> files, Long auditId) {
        log.info("Upload de {} fichiers pour l'audit ID: {}", files.size(), auditId);

        List<AuditDocumentDto> uploadedDocuments = new ArrayList<>();

        for (File file : files) {
            try {
                AuditDocumentDto document = uploadFile(file, auditId);
                uploadedDocuments.add(document);
            } catch (Exception e) {
                log.error("❌ Échec de l'upload du fichier: {}", file.getName(), e);
                // Continuer avec les autres fichiers même si un échoue
            }
        }

        log.info("✅ {} fichiers uploadés sur {} tentés", uploadedDocuments.size(), files.size());
        return uploadedDocuments;
    }

    /**
     * Construire le corps multipart pour l'upload
     */
    private byte[] buildMultipartBody(File file, Long auditId, String boundary) throws IOException {
        StringBuilder builder = new StringBuilder();

        // Ajouter le paramètre auditId
        builder.append("--").append(boundary).append("\r\n");
        builder.append("Content-Disposition: form-data; name=\"auditId\"\r\n");
        builder.append("\r\n");
        builder.append(auditId).append("\r\n");

        // Ajouter le fichier
        builder.append("--").append(boundary).append("\r\n");
        builder.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(file.getName()).append("\"\r\n");
        builder.append("Content-Type: ").append(getContentType(file)).append("\r\n");
        builder.append("\r\n");

        byte[] header = builder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

        // Combiner header + contenu + footer
        byte[] result = new byte[header.length + fileContent.length + footer.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(fileContent, 0, result, header.length, fileContent.length);
        System.arraycopy(footer, 0, result, header.length + fileContent.length, footer.length);

        return result;
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

    /**
     * Récupérer tous les documents d'un audit
     */
    public List<AuditDocumentDto> getDocumentsByAudit(Long auditId) {
        log.info("Récupération des documents de l'audit ID: {}", auditId);

        try {
            String url = baseUrl + "/api/audit-documents/audit/" + auditId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<AuditDocumentDto> documents = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, AuditDocumentDto.class)
                );

                log.info("✅ {} documents récupérés", documents.size());
                return documents;
            } else {
                log.error("❌ Erreur récupération documents: Code {}", response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des documents", e);
            return new ArrayList<>();
        }
    }

    /**
     * Supprimer un document
     */
    public boolean deleteDocument(Long documentId) {
        log.info("Suppression du document ID: {}", documentId);

        try {
            String url = baseUrl + "/api/audit-documents/" + documentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || response.statusCode() == 200) {
                log.info("✅ Document supprimé avec succès");
                return true;
            } else {
                log.error("❌ Erreur suppression document: Code {}", response.statusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression du document", e);
            return false;
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

        // Vérifier la taille (max 50MB par exemple)
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
}