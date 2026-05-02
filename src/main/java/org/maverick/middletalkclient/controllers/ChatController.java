package org.maverick.middletalkclient.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.component.SyntaxEditor;
import org.maverick.middletalkclient.models.Conference;
import org.maverick.middletalkclient.models.Message;
import org.maverick.middletalkclient.services.DataManager;
import org.maverick.middletalkclient.services.WsClient;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ChatController {

    private final WsClient wsService = new WsClient();
    public StackPane editorContainer;
    private final SyntaxEditor editor = new SyntaxEditor();

    private boolean codeAreaEditedByCode = false;

    @FXML private Label statusLabel;
    @FXML private TextArea messageArea;
    @FXML private TextArea input;
    @FXML private Button sendButton;

    @FXML public void initialize() throws IOException, InterruptedException {

        DataManager.getMessagesInConference(State.getCurrentConference().id());

        State.getMessageList().forEach(message -> {
                messageArea.appendText(
                        "[" + message.createdAt() + "] " +
                                message.username() + ": " +
                                message.content() + "\n"
                );
        });

        // Обработка входящих сообщений
        wsService.setOnMessage(rawJson -> {
            ObjectMapper mapper = new ObjectMapper();
            Message message;
            try {
                 message = mapper.readValue(rawJson, Message.class);
            } catch (JsonProcessingException e) {
                return;
            }

            if (Objects.equals(message.type(), "text")) {
                messageArea.appendText(
                        "[" + message.createdAt() + "] " +
                                message.username() + ": " +
                                message.content() + "\n"
                );
            } else {
                if (!Objects.equals(message.username(), State.getUser().username())) {
                    codeAreaEditedByCode = true;
                    editor.getCodeArea().clear();
                    editor.getCodeArea().appendText(message.content());
                    codeAreaEditedByCode = false;
                }
            }
        });

        wsService.setOnOpen(() -> {
            statusLabel.setText("🟢 Подключено");
            sendButton.setDisable(false);
        });

        wsService.setOnClose(() -> {
            statusLabel.setText("⚪ Отключено");
            sendButton.setDisable(true);
        });

        wsService.setOnError(err -> {
            statusLabel.setText("❌ " + err);
            sendButton.setDisable(true);
        });

        // Подключаемся сразу при открытии чата
        Conference conf = State.getCurrentConference();
        if (conf != null) {
            wsService.connect(conf.id(), State.getUser().JWT());
        } else {
            statusLabel.setText("❌ Нет ID конференции");
        }


        editorContainer.getChildren().add(editor.getCodeArea());

        // Пример начального кода
        editor.getCodeArea().appendText("public class Main {\n    public static void main(String[] args) {\n        // TODO\n    }\n}");

        editor.getCodeArea().textProperty().addListener((obs, oldText, newText) -> {
            if (codeAreaEditedByCode) return;
            System.out.println("Что-то новенькое:");
            IO.println(newText); // ваша логика

            var username = State.getUser().username();
            var conference_id = State.getCurrentConference().id();
            var type = "text";
            var content = newText;

            var mapper = new ObjectMapper();

            String json = null;
            try {
                json = mapper.writeValueAsString(Map.of(
                        "type", "code",
                        "content", content, // \n станет \\n внутри строки
                        "username", username,
                        "conference_id", conference_id
                ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

//            String json = "{\"username\":\"" + username  + "\",\"conference_id\":\"" +
//                    conference_id + "\",\"type\":\"" + type + "\",\"content\":\"" + content + "\"}";

            wsService.send(json);

        });

    }

    @FXML public void onSendButton() throws IOException, InterruptedException {

        var username = State.getUser().username();
        var conference_id = State.getCurrentConference().id();
        var type = "text";
        var content = input.getText();

        String json = "{\"username\":\"" + username  + "\",\"conference_id\":\"" +
                conference_id + "\",\"type\":\"" + type + "\",\"content\":\"" + content + "\"}";

        wsService.send(json);

        input.setText("");

    }

}
