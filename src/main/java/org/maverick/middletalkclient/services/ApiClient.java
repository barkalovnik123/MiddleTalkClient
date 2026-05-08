package org.maverick.middletalkclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.exceptions.AuthError;
import org.maverick.middletalkclient.exceptions.ConferenceError;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String domain = "http://127.0.0.1:8080";
//    private static final String domain = "https://settle-crawling-pastel.ngrok-free.dev";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();
    private static Runnable onUnauthorized; // колбэк для UI

    public static void setUnauthorizedHandler(Runnable handler) { onUnauthorized = handler; }

    public static <T> CompletableFuture<T> get(String path, Class<T> type) {
        return sendAsync(HttpRequest.newBuilder()
                .uri(URI.create(domain + path))
                .header("Authorization", "Bearer " + State.getUser().JWT())
                .GET().build(), type);
    }

    public static <T> CompletableFuture<T> post(String path, Object body, Class<T> type) {
        try {
            String json = JSON.writeValueAsString(body);
            return sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create(domain + path))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + State.getUser().JWT())
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build(), type);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

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

    public static String getConferencesRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/conferences"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + State.getUser().JWT())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет конференций" :
                                    "Ошибка";
            throw new ConferenceError(err_msg);
        }

        return response.body();
    }

    public static String sendLoginRequest(String username, String password) throws IOException, InterruptedException {
        String json = "{\"username\":\"" + username  + "\",\"password\":\"" + password+ "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет такого пользователя или неверный пароль" :
                                    "Ошибка";
            throw new AuthError(err_msg);
        }

        return response.body();
    }

    public static String getMessagesInConferenceRequest(long conference_id) throws IOException, InterruptedException {

        String json = "{\"conference_id\":" + conference_id  + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/messages"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + State.getUser().JWT())
                .method("GET", HttpRequest.BodyPublishers.ofString(json)) // Я слишком поздно узнал
                .build(); // что в GET по договорённости никто тело не включает...

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет конференций" :
                                    "Ошибка";
            throw new ConferenceError(err_msg);
        }

        return response.body();
    }

    public static String getUserById(long id) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/users/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + State.getUser().JWT())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет такого юзера" :
                                    "Ошибка";
            throw new ConferenceError(err_msg);
        }

        return response.body();

    }

    public static String sendCreateConferenceRequest(String json) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/conferences"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + State.getUser().JWT())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет такого пользователя или неверный пароль" :
                                    "Ошибка";
            throw new AuthError(err_msg);
        }

        return response.body();

    }

    public static String sendRegisterRequest(String username, String password) throws IOException, InterruptedException {
        String json = "{\"username\":\"" + username  + "\",\"password\":\"" + password+ "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            String err_msg =
                    response.statusCode() >= 500 ? "Ошибка на сервере" :
                            response.statusCode() >= 400 ? "Нет такого пользователя или неверный пароль" :
                                    "Ошибка";
            throw new AuthError(err_msg);
        }

        return response.body();
    }

}