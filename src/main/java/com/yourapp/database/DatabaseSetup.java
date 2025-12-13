package com.yourapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Database setup and initialization class.
 * Creates tables and inserts default data.
 */
public class DatabaseSetup {

    /**
     * Sets up the database by creating tables and inserting default data.
     */
    public static void setupDatabase() {
        try {
            System.out.println("🔨 Configuring database...");

            createUsersTable();
            createAuditTemplatesTable();
            createSystemSettingsTable();
            createRolesTable();
            createProjectsTable();
            createUserInvitationsTable();
            insertDefaultData();

            System.out.println("✅ Database configured successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Configuration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the users table if it doesn't exist.
     */
    private static void createUsersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "role VARCHAR(50) NOT NULL," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeSQL(sql, "Table 'users'");
    }

    /**
     * Creates the audit_templates table if it doesn't exist.
     */
    private static void createAuditTemplatesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS audit_templates (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(255) NOT NULL," +
                "description TEXT," +
                "organisation VARCHAR(255) NOT NULL," +
                "nombre_regles INTEGER DEFAULT 0," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeSQL(sql, "Table 'audit_templates'");
    }

    /**
     * Creates the system_settings table if it doesn't exist.
     */
    private static void createSystemSettingsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS system_settings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stockage_local INTEGER DEFAULT 1," +
                "sauvegarde_cloud INTEGER DEFAULT 0," +
                "alertes_email INTEGER DEFAULT 1," +
                "rappels_audit INTEGER DEFAULT 1," +
                "fournisseur_ocr VARCHAR(255) DEFAULT 'Intégré'," +
                "modele_ia VARCHAR(255) DEFAULT 'AuditDoc AI v1.0'," +
                "organisation VARCHAR(255)," +
                "version VARCHAR(50) DEFAULT '1.0.0'," +
                "type_licence VARCHAR(50) DEFAULT 'Standard'" +
                ")";
        executeSQL(sql, "Table 'system_settings'");
    }

    /**
     * Creates the roles table if it doesn't exist.
     */
    private static void createRolesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS roles (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "key VARCHAR(50) UNIQUE NOT NULL," +
                "name VARCHAR(255) NOT NULL," +
                "description TEXT" +
                ")";
        executeSQL(sql, "Table 'roles'");
    }

    /**
     * Creates the projects table if it doesn't exist.
     */
    private static void createProjectsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS projects (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "description TEXT," +
                "status VARCHAR(50) DEFAULT 'planned'," +
                "organization_id VARCHAR(36)," +
                "created_by VARCHAR(36)," +
                "start_date DATETIME," +
                "end_date DATETIME," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeSQL(sql, "Table 'projects'");
    }

    /**
     * Creates the user_invitations table if it doesn't exist.
     */
    private static void createUserInvitationsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS user_invitations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email VARCHAR(255) NOT NULL," +
                "role_id VARCHAR(36) NOT NULL," +
                "project_id VARCHAR(36)," +
                "invitation_code VARCHAR(50) UNIQUE NOT NULL," +
                "status VARCHAR(50) DEFAULT 'pending'," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeSQL(sql, "Table 'user_invitations'");
    }

    /**
     * Inserts default data into tables if they are empty.
     */
    private static void insertDefaultData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Cannot get database connection");
            }

            // Check if users table is empty
            try (Statement stmt = conn.createStatement();
                 var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("📝 Inserting default data...");

                    // Insert default user
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO users (nom, email, password, role) VALUES (?, ?, ?, ?)")) {
                        pstmt.setString(1, "Admin Principal");
                        pstmt.setString(2, "boudifatima450@gmail.com");
                        pstmt.setString(3, "admin123");
                        pstmt.setString(4, "ADMINISTRATEUR");
                        pstmt.executeUpdate();
                    }

                    // Insert default audit templates
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO audit_templates (nom, description, organisation, nombre_regles) VALUES (?, ?, ?, ?)")) {
                        
                        pstmt.setString(1, "Template AFD");
                        pstmt.setString(2, "Normes d'audit AFD");
                        pstmt.setString(3, "Agence Française de Développement");
                        pstmt.setInt(4, 15);
                        pstmt.executeUpdate();

                        pstmt.setString(1, "Template USAID");
                        pstmt.setString(2, "USAID Standard Provisions");
                        pstmt.setString(3, "USAID");
                        pstmt.setInt(4, 22);
                        pstmt.executeUpdate();

                        pstmt.setString(1, "Template ISO 19011");
                        pstmt.setString(2, "Lignes directrices audit");
                        pstmt.setString(3, "Standard International");
                        pstmt.setInt(4, 18);
                        pstmt.executeUpdate();
                    }

                    // Insert default system settings
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO system_settings (stockage_local, sauvegarde_cloud, alertes_email, " +
                            "rappels_audit, fournisseur_ocr, modele_ia, organisation, version, type_licence) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        pstmt.setInt(1, 1); // localStorage enabled
                        pstmt.setInt(2, 0); // cloudBackup disabled
                        pstmt.setInt(3, 1); // emailAlerts enabled
                        pstmt.setInt(4, 1); // auditReminders enabled
                        pstmt.setString(5, "Intégré");
                        pstmt.setString(6, "AuditDoc AI v1.0");
                        pstmt.setString(7, "Default Organization");
                        pstmt.setString(8, "1.0.0");
                        pstmt.setString(9, "Standard");
                        pstmt.executeUpdate();
                    }

                    // Insert default roles
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO roles (id, key, name, description) VALUES (?, ?, ?, ?)")) {
                        
                        pstmt.setString(1, UUID.randomUUID().toString());
                        pstmt.setString(2, "ADMINISTRATEUR");
                        pstmt.setString(3, "Administrateur");
                        pstmt.setString(4, "Accès complet à toutes les fonctionnalités");
                        pstmt.executeUpdate();

                        pstmt.setString(1, UUID.randomUUID().toString());
                        pstmt.setString(2, "CHARGE_DE_PROJET");
                        pstmt.setString(3, "Chargé de Projet");
                        pstmt.setString(4, "Gestion des projets et audits");
                        pstmt.executeUpdate();

                        pstmt.setString(1, UUID.randomUUID().toString());
                        pstmt.setString(2, "LECTEUR");
                        pstmt.setString(3, "Lecteur");
                        pstmt.setString(4, "Consultation uniquement");
                        pstmt.executeUpdate();
                    }

                    // Insert default projects
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO projects (id, name, description, status) VALUES (?, ?, ?, ?)")) {
                        
                        pstmt.setString(1, UUID.randomUUID().toString());
                        pstmt.setString(2, "Projet Alpha");
                        pstmt.setString(3, "Description du Projet Alpha");
                        pstmt.setString(4, "in_progress");
                        pstmt.executeUpdate();

                        pstmt.setString(1, UUID.randomUUID().toString());
                        pstmt.setString(2, "Projet Beta");
                        pstmt.setString(3, "Description du Projet Beta");
                        pstmt.setString(4, "planned");
                        pstmt.executeUpdate();
                    }

                    System.out.println("✅ Default data inserted");
                }
            }
        }
    }

    /**
     * Executes a SQL statement.
     * @param sql SQL statement to execute
     * @param tableName name of the table being created (for logging)
     */
    private static void executeSQL(String sql, String tableName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            if (conn == null) {
                throw new SQLException("Cannot get database connection");
            }
            
            stmt.executeUpdate(sql);
            System.out.println("✅ " + tableName + " created");
        }
    }

    /**
     * Main method for testing database setup.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("🔧 Database Setup - SQLite");
        System.out.println("=================================");

        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ SQLite connection OK");
            setupDatabase();
            System.out.println("✅ Database setup completed!");
        } else {
            System.err.println("❌ Unable to connect to SQLite database");
            System.err.println("Please check:");
            System.err.println("  1. SQLite JDBC driver is in classpath");
            System.err.println("  2. Write permissions in the application directory");
        }
    }
}

