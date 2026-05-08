package org.maverick.middletalkclient.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.maverick.middletalkclient.models.ChatMessage;

public class ChatComponent extends VBox {

    private final ListView<ChatMessage> listView;
    private final ObservableList<ChatMessage> messages;

    public ChatComponent() {
        getStyleClass().add("chat-container");

        listView = new ListView<>();
        listView.getStyleClass().add("chat-list");
        listView.setCellFactory(lv -> new ChatCell());

        listView.setFocusTraversable(false);

        messages = FXCollections.observableArrayList();
        listView.setItems(messages);

        getChildren().add(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
    }

    public void addMessage(ChatMessage msg) {
        if (javafx.application.Platform.isFxApplicationThread()) {
            doAddMessage(msg);
        } else {
            javafx.application.Platform.runLater(() -> doAddMessage(msg));
        }
    }

    private void doAddMessage(ChatMessage msg) {
        messages.add(msg);
        scrollToBottom();
    }

    public void addMessages(java.util.Collection<ChatMessage> msgs) {
        if (javafx.application.Platform.isFxApplicationThread()) {
            messages.addAll(msgs);
            scrollToBottom();
        } else {
            javafx.application.Platform.runLater(() -> {
                messages.addAll(msgs);
                scrollToBottom();
            });
        }
    }

    public void clear() {
        if (javafx.application.Platform.isFxApplicationThread()) {
            messages.clear();
        } else {
            javafx.application.Platform.runLater(messages::clear);
        }
    }

    public void scrollToBottom() {
        if (javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> {
                int lastIndex = messages.size() - 1;
                if (lastIndex >= 0) {
                    listView.scrollTo(lastIndex);
                }
            });
        }
    }

    public ListView<ChatMessage> getListView() {
        return listView;
    }

    public ObservableList<ChatMessage> getMessages() {
        return messages;
    }
}