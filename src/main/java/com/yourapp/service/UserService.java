package com.yourapp.service;

import com.yourapp.model.User;

/**
 * Service for managing user authentication and session.
 */
public class UserService {
    
    private static User currentUser = null;
    
    /**
     * Gets the current logged-in user.
     * @return User object or null if no user is logged in
     */
    public static User getCurrentUser() {
        if (currentUser == null) {
            // Fallback for testing
            return new User("Admin", "admin@example.com", "Administrateur");
        }
        return currentUser;
    }
    
    /**
     * Sets the current logged-in user.
     * @param user User object to set as current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Gets a user by email.
     * @param email user email
     * @return User object or null if not found
     */
    public static User getUserByEmail(String email) {
        // For now, return a default user
        // TODO: Load from database when database is available
        if (email != null && email.contains("@")) {
            return new User("User", email, "Utilisateur");
        }
        return null;
    }
}
