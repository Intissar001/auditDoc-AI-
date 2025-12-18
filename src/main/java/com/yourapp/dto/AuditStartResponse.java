package com.yourapp.dto;

public class AuditStartResponse {

    private Long auditId;
    private String message;

    public AuditStartResponse(Long auditId, String message) {
        this.auditId = auditId;
        this.message = message;
    }

    public Long getAuditId() {
        return auditId;
    }

    public String getMessage() {
        return message;
    }
}
