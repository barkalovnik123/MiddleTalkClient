package org.maverick.middletalkclient.services.old;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://127.0.0.1:8080/api";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();
    private static Runnable onUnauthorized; // колбэк для UI

    public static void setUnauthorizedHandler(Runnable handler) { onUnauthorized = handler; }

    // 🔹 GET
    public static <T> CompletableFuture<T> get(String path, Class<T> type) {
        return sendAsync(HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Authorization", "Bearer " + AuthManager.get().getUser().JWT())
                .GET().build(), type);
    }

    // 🔹 POST
    public static <T> CompletableFuture<T> post(String path, Object body, Class<T> type) {
        try {
            String json = JSON.writeValueAsString(body);
            return sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthManager.get().getUser().JWT())
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build(), type);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 🔹 Отправка + парсинг + глобальная обработка 401
    private static <T> CompletableFuture<T> sendAsync(HttpRequest req, Class<T> type) {
        return HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(res -> {
                    if (res.statusCode() == 401) {
                        Platform.runLater(() -> {
                            if (onUnauthorized != null) onUnauthorized.run();
                        });
                        throw new RuntimeException("Сессия истекла. Требуется повторный вход.");
                    }
                    if (res.statusCode() >= 400) {
                        try {
                            Map<String, Object> err = JSON.readValue(res.body(), Map.class);
                            throw new RuntimeException(err.getOrDefault("error", "HTTP " + res.statusCode()).toString());
                        } catch (Exception ignored) {
                            throw new RuntimeException("HTTP " + res.statusCode());
                        }
                    }
                    try {
                        return JSON.readValue(res.body(), type);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}