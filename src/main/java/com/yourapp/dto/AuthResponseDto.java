package com.yourapp.dto;

public class AuthResponseDto {

    private boolean success;
    private String message;
    private Long userId;
    private String userEmail;
    private String userRole;

    // Constructors
    public AuthResponseDto() {}

    public AuthResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponseDto(boolean success, String message, Long userId, String userEmail, String userRole) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    // Static factory methods
    public static AuthResponseDto success(String message, Long userId, String userEmail, String userRole) {
        return new AuthResponseDto(true, message, userId, userEmail, userRole);
    }

    public static AuthResponseDto failure(String message) {
        return new AuthResponseDto(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}