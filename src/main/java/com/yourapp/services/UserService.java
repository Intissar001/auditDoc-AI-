package com.yourapp.services;

import com.yourapp.model.User;

public class UserService {

    public User getCurrentUser() {
        // Use the static UserService from com.yourapp.service if available
        try {
            User user = com.yourapp.service.UserService.getCurrentUser();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            // Fallback if static service not available
        }
        
        // Fallback: return default user with all required parameters (name, email, role)
        return new User("Mohamed El Idrissi", "admin@example.com", "Administrateur");
    }
}
