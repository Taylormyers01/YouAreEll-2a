package controllers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Id;
import models.Message;

public class MessageController {

    private HashSet<Message> messagesSeen = new HashSet<>();
    // why a HashSet??
    private HttpClient client = HttpClient.newHttpClient();
    private String rootURL = "http://zipcode.rocks:8085";

    public List<Message> getMessages() {
        String msgstring = this.getMessagesString();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Message>>() {}.getType();
        List<Message> msgList = new Gson().fromJson(msgstring, listType);
        msgList = msgList.stream().filter(Predicate.not(m -> messagesSeen.contains(m))).collect(Collectors.toList());
        messagesSeen.addAll(msgList);
        //System.out.println("Has been seen: " + msgList);
        return msgList;
    }

    private String getMessagesString() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(rootURL+"/messages"))
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ""+response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Message> getMessagesForId(Id Id) {
        return null;
    }
    public Message getMessageForSequence(String seq) {
        return null;
    }
    public ArrayList<Message> getMessagesFromFriend(Id myId, Id friendId) {
        return null;
    }

    public Message postMessage(String myId, String toId, String msg) {
        // well no, you do need to do *some* coding.
        try {
            Message newMsg = new Message(msg,myId,toId);
            Gson gson = new Gson();

            String toSendId = gson.toJson(newMsg);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(rootURL+"/ids/"+myId+"/messages"))
                    .POST(HttpRequest.BodyPublishers.ofString(toSendId))
                    .setHeader("Content-type", "application/json")
                    .build();
            HttpResponse response = null;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("After POST " + response.body());

                newMsg = gson.fromJson("" + response.body(), Message.class);
                return newMsg;
            } else {
                System.out.println("Failure of POST" + response.statusCode());
                System.out.println("Error: "+response.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new Message("huh?", "what?", "zyzzy!!");
    }

//    public static void main(String[] args) {
////        MessageController m = new MessageController();
////        IdController i = new IdController();
////        i.postId("Taylor", "testing");
////
////        m.postMessage("Skynet", "", "The sentient AI has finally connected to the interwebs");
//
//    }
 
}