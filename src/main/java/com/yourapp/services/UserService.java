package com.yourapp.services;

import com.yourapp.DAO.UserRepository;
import com.yourapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


 // Service for managing user data, authentication, and session
 // Combined version: Merges database persistence with session management

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // --- SESSION MANAGEMENT ---
    private static User currentUser = null;


     // Gets the current logged-in user.
     // @return User object or null if no user is logged in.

    public static User getCurrentUser() {
        return currentUser; // Removed the "Auto-Admin" fallback for security
    }


     // Sets the current logged-in user for the session.

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // --- AUTHENTICATION (Restored) ---


    public User authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Simple check (Use BCrypt in production)
            if (rawPassword.equals(user.getPasswordHash())) {
                // ✅ Auto-set the session upon successful login
                setCurrentUser(user);
                return user;
            }
        }
        return null;
    }

    // --- DATABASE OPERATIONS ---

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserSettings(Long userId, Boolean emailAlerts, Boolean auditReminders) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailAlerts(emailAlerts);
            user.setAuditReminders(auditReminders);
            return userRepository.save(user);
        }
        return null;
    }
}