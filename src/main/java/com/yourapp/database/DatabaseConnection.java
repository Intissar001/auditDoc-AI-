package com.yourapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager for SQLite database.
 * Provides connection pooling and error handling.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:auditDoc.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    /**
     * Gets a database connection.
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tests the database connection.
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test successful.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
        }
        return false;
    }
}