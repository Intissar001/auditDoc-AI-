package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDocumentDto {

    private Long id;

    private Long auditId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String storagePath;

    private String status; // UPLOADED, PROCESSING, ANALYZED, ERROR

    private LocalDateTime uploadedAt;

    private LocalDateTime analyzedAt;

    private String errorMessage;

    private Integer issuesCount;
}