package com.yourapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application Spring Boot REST API
 * Cette application expose les API REST sur le port 8080
 * Elle tourne SÉPARÉMENT de l'application JavaFX
 */
@SpringBootApplication
@EnableJpaRepositories("com.yourapp.DAO")
@EntityScan("com.yourapp.model")
public class AuditDocAiApplication {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🚀 Démarrage du Backend REST API");
        System.out.println("========================================");

        SpringApplication.run(AuditDocAiApplication.class, args);

        System.out.println("========================================");
        System.out.println("✅ Backend REST API démarré sur http://localhost:8080");
        System.out.println("📡 API disponible: http://localhost:8080/api/");
        System.out.println("========================================");
    }

    /**
     * Configuration CORS pour permettre les requêtes depuis JavaFX
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}