package com.yourapp.model;

import java.time.LocalDateTime;

public class Notification {

    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification(String message) {
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void markAsRead() { this.read = true; }
}
