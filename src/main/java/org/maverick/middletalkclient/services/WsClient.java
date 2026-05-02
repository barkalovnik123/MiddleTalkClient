package org.maverick.middletalkclient.services;

import javafx.application.Platform;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class WsClient {
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private WebSocket ws;

    private Consumer<String> onMessage;
    private Consumer<String> onError;
    private Runnable onOpen;
    private Runnable onClose;

    public void setOnMessage(Consumer<String> cb) { this.onMessage = cb; }
    public void setOnError(Consumer<String> cb) { this.onError = cb; }
    public void setOnOpen(Runnable cb) { this.onOpen = cb; }
    public void setOnClose(Runnable cb) { this.onClose = cb; }

    public void connect(long confId, String token) {
        if (ws != null && !ws.isOutputClosed()) disconnect();

        String url = String.format("ws://127.0.0.1:8080/ws/conference/%d?token=%s", confId, token);
//        String url = String.format("wss://settle-crawling-pastel.ngrok-free.dev/ws/conference/%d?token=%s", confId, token);

        Listener listener = new Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                ws = webSocket;
                if (onOpen != null) Platform.runLater(onOpen);
                requestMore(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                if (onMessage != null) {
                    Platform.runLater(() -> onMessage.accept(data.toString()));
                }
                if (last) requestMore(webSocket);
                return CompletableFuture.completedFuture(null);
            }

//            @Override
//            public CompletionStage<?> onError(WebSocket webSocket, Throwable error) {
//                if (onError != null) {
//                    Platform.runLater(() -> onError.accept(error.getMessage()));
//                }
//                return CompletableFuture.completedFuture(null);
//            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                if (onClose != null) Platform.runLater(onClose);
                return CompletableFuture.completedFuture(null); // ✅ Обязательный возврат
            }

            private void requestMore(WebSocket ws) {
                ws.request(1); // Разрешаем получить следующее сообщение
            }
        };

        HTTP.newWebSocketBuilder()
                .buildAsync(URI.create(url), listener)
                .exceptionally(ex -> {
                    if (onError != null) Platform.runLater(() -> onError.accept("Connection failed: " + ex.getMessage()));
                    return null;
                });
    }

    public boolean isConnected() {
        return ws != null && !ws.isInputClosed() && !ws.isOutputClosed();
    }

    public void send(String json) {
        if (isConnected()) {
            ws.sendText(json, true);
        }
    }

    public void disconnect() {
        if (ws != null) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "Client disconnecting");
            ws = null;
        }
    }
}