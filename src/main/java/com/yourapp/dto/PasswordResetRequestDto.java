package com.yourapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequestDto {

    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    // Constructors
    public PasswordResetRequestDto() {}

    public PasswordResetRequestDto(String email) {
        this.email = email;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}