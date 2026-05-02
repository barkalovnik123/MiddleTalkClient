package org.maverick.middletalkclient.models;

public record User(
        long id,
        String username,
        String createdAt,
        String JWT
) {
}
