package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL; // Ajout

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // 1. Définition du chemin
        String fxmlPath = "/views/fxml/ProjetsView.fxml";

        // TEST RAPIDE : S'assurer que Java trouve la ressource
        URL resourceUrl = getClass().getResource(fxmlPath);
        if (resourceUrl == null) {
            // Si le plugin Maven échoue à cause de la ressource, cette ligne te le dira
            System.err.println("ERREUR GRAVE: Fichier FXML non trouvé au chemin: " + fxmlPath);
            throw new IOException("Fichier FXML manquant.");
        }

        // 2. Chargement du FXML
        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);

        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setTitle("JavaFX - Audit Screen");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}