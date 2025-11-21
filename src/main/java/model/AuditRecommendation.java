package com.auditdocai.model;

import java.util.UUID;

public class AuditRecommendation {
    private UUID id;
    private UUID findingId;     // AuditFinding.id
    private String recommendationText;
    private String impact;      // "LOW", "MEDIUM", "HIGH"

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFindingId() { return findingId; }
    public void setFindingId(UUID findingId) { this.findingId = findingId; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }

    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
}
