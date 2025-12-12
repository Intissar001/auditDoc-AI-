package com.yourapp.service;

import com.yourapp.database.DatabaseConnection;
import com.yourapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for user management and session handling.
 */
public class UserService {
    
    private static User currentUser = null;

    /**
     * Authenticates a user by email and password.
     * @param email user email
     * @param password user password
     * @return User object if authentication succeeds, null otherwise
     */
    public static User authenticateUser(String email, String password) {
        String sql = "SELECT id, nom, email, role FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return null;
            }
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("nom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a user by email.
     * @param email user email
     * @return User object if found, null otherwise
     */
    public static User getUserByEmail(String email) {
        String sql = "SELECT id, nom, email, role FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return null;
            }
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("nom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all users from the database.
     * @return List of all users
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, nom, email, role FROM users ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                return users;
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("nom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Sets the current user in session.
     * @param user User object to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the current user from session.
     * @return Current User object, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
}

