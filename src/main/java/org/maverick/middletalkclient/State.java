package org.maverick.middletalkclient;

import org.maverick.middletalkclient.models.Conference;
import org.maverick.middletalkclient.models.Message;
import org.maverick.middletalkclient.models.User;

import java.util.ArrayList;

public class State {

    private static User user;

    public static User getUser() {
        return user;
    }
    public static void setUser(User user) {
        State.user = user;
    }

    private static ArrayList<Conference> conferences;

    public static ArrayList<Conference> getConferences() {return conferences;}
    public static void setConferences(ArrayList<Conference> conferences) {State.conferences = conferences;}

    private static Conference currentConference;

    public static Conference getCurrentConference() {return currentConference;}
    public static void setCurrentConference(Conference currentConference) {State.currentConference = currentConference;}

    private static ArrayList<Message> messageList;

    public static ArrayList<Message> getMessageList() {return messageList;}
    public static void setMessageList(ArrayList<Message> messageList) {State.messageList = messageList;}
}
