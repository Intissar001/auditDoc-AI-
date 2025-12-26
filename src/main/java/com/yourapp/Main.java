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

@SpringBootApplication
@EnableJpaRepositories("com.yourapp.DAO")
@EntityScan("com.yourapp.model")
public class Main extends Application {

    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        System.setProperty("glass.win.uiScale", "1.0");
        launch(args);
    }

    @Override
    public void init() {
        System.out.println("🔧 Initialisation de Spring Boot...");
        SpringApplication app = new SpringApplication(Main.class);

        // Ensure no web server starts for the desktop app
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);

        springContext = app.run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("🎨 Chargement de l'interface JavaFX...");

        // **MODIFICATION CLÉ : Démarrer avec login.fxml**
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/fxml/login.fxml"));

        // Link JavaFX to Spring context
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("AuditDoc AI - Connexion");

        // **MODIFICATION CLÉ : Maximiser la fenêtre au démarrage**
        stage.setMaximized(true);

        stage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }
}