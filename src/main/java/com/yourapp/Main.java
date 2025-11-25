/*package com.yourapp; // Use your actual package name

import com.yourapp.controller.AuditController; // Import the controller provided earlier
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // --- MODIFIED CODE START ---

        // 1. Point to the new AuditView.fxml (adjust path as necessary)
        // Assuming AuditView.fxml is in a resource folder named 'views/fxml'
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/fxml/Audit.fxml"));

        // Ensure the controller package matches the structure provided earlier
        // fxmlLoader.setController(new AuditController()); // Only needed if controller is not specified in FXML

        // 2. Load the FXML and create the scene
        // Use a large size to fit the detailed layout (1400x900 recommended)
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        // 3. Link the CSS file (styles.css) to the scene
        // Assuming styles.css is in a resource folder named 'css'
        String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("Audit Doc AI - Smart Auditing");
        stage.setScene(scene);
        stage.show();

        // --- MODIFIED CODE END ---
    }

    public static void main(String[] args) {
        launch();
    }
} */

package com.yourapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/views/fxml/Audit.fxml")  // USE AUDIT HERE
        );

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("JavaFX - Audit Screen");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}