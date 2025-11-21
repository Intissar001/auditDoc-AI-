package com.auditdocai.model;

import java.util.UUID;

public class Permission {

    private UUID id;
    private String key;        // ex: "document.create"
    private String name;       // ex: "Create Document"
    private String category;   // ex: "documents", "audit", "settings"

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
