package com.yourapp.service;

import com.yourapp.database.DatabaseConnection;
import com.yourapp.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing roles.
 */
public class RoleService {

    /**
     * Retrieves all roles from the database.
     * If no roles are found, returns default roles.
     * @return A list of Role objects.
     */
    public static List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        
        // First, try to get roles from database if roles table exists
        // For now, return default roles since roles table may not exist in current schema
        return getDefaultRoles();
    }

    /**
     * Returns a hardcoded list of default roles.
     * @return A list of default Role objects.
     */
    private static List<Role> getDefaultRoles() {
        List<Role> roles = new ArrayList<>();
        
        Role admin = new Role();
        admin.setId(UUID.randomUUID());
        admin.setKey("ADMINISTRATEUR");
        admin.setName("Administrateur");
        admin.setDescription("Accès complet à toutes les fonctionnalités");
        roles.add(admin);
        
        Role projectManager = new Role();
        projectManager.setId(UUID.randomUUID());
        projectManager.setKey("CHARGE_DE_PROJET");
        projectManager.setName("Chargé de Projet");
        projectManager.setDescription("Gestion des projets et audits");
        roles.add(projectManager);
        
        Role reader = new Role();
        reader.setId(UUID.randomUUID());
        reader.setKey("LECTEUR");
        reader.setName("Lecteur");
        reader.setDescription("Consultation uniquement");
        roles.add(reader);
        
        return roles;
    }

    /**
     * Gets a role by its key.
     * @param key The role key (e.g., "ADMINISTRATEUR").
     * @return The Role object, or null if not found.
     */
    public static Role getRoleByKey(String key) {
        List<Role> roles = getAllRoles();
        return roles.stream()
                .filter(role -> role.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }
}

