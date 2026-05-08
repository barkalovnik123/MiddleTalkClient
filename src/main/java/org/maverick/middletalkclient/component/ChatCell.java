package org.maverick.middletalkclient.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.maverick.middletalkclient.models.ChatMessage;

import java.time.format.DateTimeFormatter;

public class ChatCell extends ListCell<ChatMessage> {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    private final HBox bubble = new HBox(8);
    private final Label textLabel = new Label();
    private final Label usernameLabel = new Label();
    private final Label timestampLabel = new Label();
    private final Label avatarLabel = new Label();

    public ChatCell() {
        // 🔹 Настройка текстового лейбла
        textLabel.getStyleClass().add("message-text");
        textLabel.setWrapText(true);
        textLabel.setPrefWidth(400); // Макс. ширина пузыря

        // 🔹 Настройка заголовка (аватар + имя + время)
        usernameLabel.getStyleClass().add("username");
        timestampLabel.getStyleClass().add("timestamp");
        avatarLabel.getStyleClass().add("avatar");
        avatarLabel.setPrefSize(32, 32);

        HBox header = new HBox(6, avatarLabel, usernameLabel, timestampLabel);
        header.getStyleClass().add("message-header");
        HBox.setHgrow(usernameLabel, Priority.ALWAYS);

        // 🔹 Сборка пузыря
        VBox content = new VBox(4, header, textLabel);
        bubble.getStyleClass().add("message-bubble");
        bubble.getChildren().add(content);

        // 🔹 Применяем стили при смене темы (опционально)
        getStyleClass().add("chat-cell");
    }

    @Override
    protected void updateItem(ChatMessage msg, boolean empty) {
        super.updateItem(msg, empty);

        if (empty || msg == null) {
            setGraphic(null);
            getStyleClass().remove("increment");
        } else {
            // 🔹 Заполнение данными
            textLabel.setText(msg.content());
            usernameLabel.setText(msg.username());
            timestampLabel.setText(msg.timestamp().format(TIME_FORMAT));

            // 🔹 Аватар: первая буква имени
            String initial = msg.username().isEmpty() ? "?"
                    : msg.username().substring(0, 1).toUpperCase();
            avatarLabel.setText(initial);

            // 🔹 Стилизация: свои / чужие / системные
            bubble.getStyleClass().removeAll("own", "other", "system");
            if (msg.isSystem()) {
                bubble.getStyleClass().add("system");
                avatarLabel.setVisible(false); // Скрываем аватар для системных
                usernameLabel.setVisible(false);
            } else if (msg.isOwn()) {
                bubble.getStyleClass().add("own");
                avatarLabel.setVisible(false); // Свои — без аватара (как в Telegram)
                usernameLabel.setVisible(false);
                bubble.setAlignment(Pos.CENTER_RIGHT);
            } else {
                bubble.getStyleClass().add("other");
                avatarLabel.setVisible(true);
                usernameLabel.setVisible(true);
                bubble.setAlignment(Pos.CENTER_LEFT);
            }

            // 🔹 Анимация появления (только для новых сообщений)
            if (!getStyleClass().contains("increment")) {
                getStyleClass().add("increment");
            }

            setGraphic(bubble);

            // 🔹 Выравнивание ячейки
            setAlignment(msg.isOwn() ? Pos.CENTER_RIGHT :
                    msg.isSystem() ? Pos.CENTER : Pos.CENTER_LEFT);
        }
    }
}