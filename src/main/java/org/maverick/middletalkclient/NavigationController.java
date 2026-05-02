package org.maverick.middletalkclient;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationController {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void navigateTo(String fxmlPath) {
        if (stage == null) {
            throw new IllegalStateException("NavigationController не инициализирован.");
        }

        Runnable switchScene = () -> {
            try {
                FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
                Parent root = loader.load();

                stage.setScene(new Scene(root));
                stage.sizeToScene();
                stage.centerOnScreen();
            } catch (IOException e) {
                throw new RuntimeException("Не удалось загрузить: " + fxmlPath, e);
            }
        };
        if (Platform.isFxApplicationThread()) {
            switchScene.run();
        } else {
            Platform.runLater(switchScene);
        }
    }

    public static Stage getStage() { return stage; }
}