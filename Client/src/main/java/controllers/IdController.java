package controllers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Id;

public class IdController {
    private HashMap<String, Id> allIds;
    private String rootURL = "http://zipcode.rocks:8085";
    private HttpClient client = HttpClient.newHttpClient();

    Id myId;

    public List<Id> getIds() {
        String idsstring = this.getIdsString();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Id>>() {}.getType();
        List<Id> idList = new Gson().fromJson(idsstring, listType);
        return idList;
    }

    public Id postId(String name, String ghname) {
        try {
            Id newID = new Id(name, ghname);
            Gson gson = new Gson();

            String toSendId = gson.toJson(newID);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(rootURL+"/ids"))
                    .POST(HttpRequest.BodyPublishers.ofString(toSendId))
                    .setHeader("Content-type", "application/json")
                    .build();
            HttpResponse response = null;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("After POST " + response.body());

                newID = gson.fromJson("" + response.body(), Id.class);
                return newID;
            } else {
                System.out.println("Failure of POST" + response.statusCode());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new Id("failllll!!!","codeNoWork!!!!");
    }

    public Id putId(Id id) {
        return null;
    }

    public String getIdsString() { //public List<Id> getIds() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(rootURL+"/ids"))
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ""+response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}