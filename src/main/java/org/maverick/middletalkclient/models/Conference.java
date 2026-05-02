package org.maverick.middletalkclient.models;

public record Conference(
        long id,
        long ownerId, // Причина моего самого долгого дебага - я не обратил внимание на то
        String name, // что я эту переменную случайно назвал CamelCase'ом
        String description,
        String createdAt
) {
    @Override
    public String toString() {
        return name;
    }
}
