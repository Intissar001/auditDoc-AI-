package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO pour la création d'un audit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditCreateRequestDto {

    @NotNull(message = "L'identifiant du projet est obligatoire")
    private Long projectId;

    @NotNull(message = "L'identifiant du modèle est obligatoire")
    private Long modelId;

    @NotNull(message = "Au moins un document est requis")
    private List<Long> documentIds;

}