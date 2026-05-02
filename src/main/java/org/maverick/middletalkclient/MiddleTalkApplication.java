package org.maverick.middletalkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MiddleTalkApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MiddleTalkApplication.class.getResource("auth-view-modern.fxml"));
        NavigationController.init(stage);
        NavigationController.navigateTo("auth-view-modern.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
