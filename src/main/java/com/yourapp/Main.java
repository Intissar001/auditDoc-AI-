package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application principale qui combine JavaFX et Spring Boot
 * Cette application démarre Spring Boot SANS serveur web (mode local)
 */
@SpringBootApplication
@EnableJpaRepositories("com.yourapp.DAO")
@EntityScan("com.yourapp.model")
public class Main extends Application {

    private static ConfigurableApplicationContext springContext;

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🚀 Démarrage de AuditDoc AI");
        System.out.println("========================================");

        // Lancer JavaFX (qui initialisera Spring Boot dans init())
        launch(args);
    }

    /**
     * Initialiser Spring Boot AVANT le démarrage de JavaFX
     */
    @Override
    public void init() {

        /** aicha
        SpringApplication app = new SpringApplication(AuditDocAiApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);

        System.out.println("🔧 Initialisation de Spring Boot...");

        SpringApplication app = new SpringApplication(Main.class);

        // ⚠️ IMPORTANT : Désactiver le serveur web Tomcat
        // L'application tourne en LOCAL, pas en mode serveur
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        */

        springContext = app.run();

        System.out.println("✅ Spring Boot initialisé (mode local, sans serveur web)");
    }

    /**
     * Démarrer l'interface JavaFX
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("🎨 Chargement de l'interface JavaFX...");

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/views/fxml/signup.fxml")
        );

        // Utiliser Spring pour instancier les contrôleurs
        loader.setControllerFactory(param -> springContext.getBean(param));

        Parent root = loader.load();

/**aicha
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("AuditDoc AI - Connexion");

        MainLayoutController mainController = loader.getController();
        mainController.setSpringContext(springContext);
        mainController.loadView("Dashboard.fxml");

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("AuditDoc AI - Document Compliance Analysis");
        stage.setMaximized(true);
        */

        stage.show();

        System.out.println("========================================");
        System.out.println("✅ AuditDoc AI démarré avec succès !");
        System.out.println("========================================");
    }

    /**
     * Nettoyer les ressources lors de la fermeture
     */
    @Override
  /**aicha
    public void stop() {springContext.close();}

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    public void stop() {
        System.out.println("🛑 Fermeture de l'application...");
        if (springContext != null) {
            springContext.close();
        }
        System.out.println("👋 Application fermée");
*/
}
}