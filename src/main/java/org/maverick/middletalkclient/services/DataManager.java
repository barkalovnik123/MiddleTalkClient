package org.maverick.middletalkclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.models.Conference;
import org.maverick.middletalkclient.models.Message;
import org.maverick.middletalkclient.models.User;
import org.maverick.middletalkclient.models.Username;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public static void login(String username, String password) throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        State.setUser(mapper.readValue(ApiClient.sendLoginRequest(username, password), User.class));

        IO.println(State.getUser());

    }

    public static void getConferenceList() throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        String json = ApiClient.getConferencesRequest();

        Conference[] arr = mapper.readValue(json, Conference[].class);

        State.setConferences(new ArrayList<>(List.of(arr)));

    }

    public static void getMessagesInConference(long conference_id) throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        String json = ApiClient.getMessagesInConferenceRequest(conference_id);

        Message[] arr = mapper.readValue(json, Message[].class);

        State.setMessageList(new ArrayList<>(List.of(arr)));

        IO.println(State.getMessageList());

    }

    public static String getUserById(long id) throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(ApiClient.getUserById(id), Username.class).username();

    }

}
