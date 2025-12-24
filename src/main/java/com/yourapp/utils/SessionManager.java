package com.yourapp.utils;

import com.yourapp.model.User;

public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("✅ Session started for user: " + (user != null ? user.getEmail() : "null"));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        System.out.println("👋 User logged out: " + (currentUser != null ? currentUser.getEmail() : "null"));
        this.currentUser = null;
    }

    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}