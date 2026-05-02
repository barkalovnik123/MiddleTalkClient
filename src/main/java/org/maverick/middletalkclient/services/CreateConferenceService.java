package org.maverick.middletalkclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateConferenceService {

    public static void create(String name, String description, long owner_id) {

        String json = "{\"owner_id\":"  + owner_id + ",\"name\":\"" + name +
                "\",\"description\":\"" + description + "\"}";

        ApiClient.sendCreateConferenceRequest(json);

    }

}
