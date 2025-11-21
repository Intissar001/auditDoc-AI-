package com.auditdocai.model;

import java.util.UUID;

public class DocumentMetadata {

    private UUID id;
    private UUID documentId;
    private String key;        // ex: "author", "subject", "language"
    private String value;      // ex: "Ministry of Health", "ISO 9001"

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
