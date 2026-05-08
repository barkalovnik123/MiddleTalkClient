package org.maverick.middletalkclient.builders;

import org.maverick.middletalkclient.models.Message;

public class MessageBuilder {

    public static String buildStringMessage(Message message) {
        return "[" + message.createdAt() + "] " +
                message.username() + ": " +
                message.content() + "\n";
    }

}
