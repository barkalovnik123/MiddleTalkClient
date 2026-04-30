package org.maverick.middletalkclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationController {
    // 🔹 Единый статический Stage для всего приложения
    private static Stage stage;

    /**
     * Инициализация. Вызовите ОДИН раз в start() вашего Launcher.
     */
    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    /**
     * Статический переход на экран.
     * Работает из любого потока (автоматически переключается на FX-поток).
     */
    public static void navigateTo(String fxmlPath) {
        if (stage == null) {
            throw new IllegalStateException("❌ NavigationController не инициализирован. Вызовите init(stage) в start()");
        }

        Runnable switchScene = () -> {
            try {
                FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
                Parent root = loader.load();

                // 🔹 Опционально: внедрение зависимостей в контроллер
                injectDependencies(loader.getController());

                stage.setScene(new Scene(root));
                stage.sizeToScene();
                stage.centerOnScreen();
            } catch (IOException e) {
                throw new RuntimeException("❌ Не удалось загрузить: " + fxmlPath, e);
            }
        };
        if (Platform.isFxApplicationThread()) {
            switchScene.run();
        } else {
            Platform.runLater(switchScene);
        }
    }

    /**
     * Внедрение сервисов в контроллеры (раскомментируйте и доработайте под ваши классы)
     */
    private static void injectDependencies(Object controller) {
        // Пример:
        // if (controller instanceof LoginController c) c.init(AuthManager.get(), ApiClient.get());
        // if (controller instanceof ChatController c) c.init(ChatService.get());
    }

    /** 🔹 Вспомогательный метод: получить текущий Stage (если очень нужно) */
    public static Stage getStage() { return stage; }
}