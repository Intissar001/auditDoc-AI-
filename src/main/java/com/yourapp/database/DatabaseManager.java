package com.yourapp.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton Database Manager
 * Handles database connections
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private EntityManagerFactory entityManagerFactory;

    private DatabaseManager() {
        try {
            System.out.println("🔌 Initializing database connection...");
            this.entityManagerFactory = Persistence.createEntityManagerFactory("AuditDocPU");
            System.out.println("✅ Database connected successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to database!");
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("🔒 Database connection closed.");
        }
    }
}
