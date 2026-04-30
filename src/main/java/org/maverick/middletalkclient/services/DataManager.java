package org.maverick.middletalkclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.exceptions.AuthError;
import org.maverick.middletalkclient.models.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthManager {

    public static void login(String username, String password) throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        State.setUser(mapper.readValue(ApiClient.sendLoginRequest(username, password), User.class));

        IO.println(State.getUser());

    }

}
