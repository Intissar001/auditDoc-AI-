package com.yourapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ⚙️ Configuration PostgreSQL
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/auditdoc";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = ""; // 👈 DIR PASSWORD DYAL PostgreSQL DYALEK HNA

    private static Connection connection = null;

    /**
     * Obtenir la connexion à la base de données PostgreSQL
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Charger le driver PostgreSQL
                Class.forName("org.postgresql.Driver");

                // Établir la connexion
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✅ Connexion PostgreSQL réussie!");

            } catch (ClassNotFoundException e) {
                System.err.println("❌ Driver PostgreSQL introuvable!");
                System.err.println("💡 Ajoute cette dependency dans pom.xml:");
                System.err.println("<dependency>");
                System.err.println("    <groupId>org.postgresql</groupId>");
                System.err.println("    <artifactId>postgresql</artifactId>");
                System.err.println("    <version>42.7.1</version>");
                System.err.println("</dependency>");
                throw new SQLException("Driver PostgreSQL introuvable", e);
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion PostgreSQL!");
                System.err.println("💡 Vérifie:");
                System.err.println("   - PostgreSQL est démarré");
                System.err.println("   - Database 'auditdoc' existe");
                System.err.println("   - Username: " + DB_USER);
                System.err.println("   - Password est correct");
                System.err.println("   - Port: 5432");
                throw e;
            }
        }
        return connection;
    }

    /**
     * Fermer la connexion
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Connexion PostgreSQL fermée");
                connection = null;
            } catch (SQLException e) {
                System.err.println("❌ Erreur fermeture: " + e.getMessage());
            }
        }
    }

    /**
     * Tester la connexion
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                System.out.println("✅ Test connexion PostgreSQL réussi!");
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("❌ Test connexion échoué: " + e.getMessage());
            return false;
        }
    }
}
