package org.maverick.middletalkclient.models;

import java.time.LocalDateTime;

public record ChatMessage(
        Long id,
        String username,
        String content,
        boolean isOwn,          // 🔹 Сообщение от текущего пользователя
        boolean isSystem,       // 🔹 Системное уведомление
        LocalDateTime timestamp // 🔹 Время отправки
) {
    // 🔹 Удобный конструктор для новых сообщений
    public static ChatMessage of(String username, String content, boolean isOwn) {
        return new ChatMessage(null, username, content, isOwn, false, LocalDateTime.now());
    }

    public static ChatMessage system(String content) {
        return new ChatMessage(null, "SYSTEM", content, false, true, LocalDateTime.now());
    }
}