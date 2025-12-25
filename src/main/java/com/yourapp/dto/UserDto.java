package com.yourapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les utilisateurs - utilisé par le frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Boolean emailAlerts = true;
    private Boolean auditReminders = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Alias pour compatibilité UI
    public String getUsername() {
        return fullName;
    }
}