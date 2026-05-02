package org.maverick.middletalkclient.models;

public record Message(
        String username,
        long conference_id,
        String type,
        String content,
        String createdAt
) {
}
