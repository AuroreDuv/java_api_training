package fr.lernejo.navy_battle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Launcher {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[0]);
        StartStopServer startStopServer = new StartStopServer();
        startStopServer.startServer(port);

        if (args.length != 1) {
            String adversaryUrl = args[1];
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(adversaryUrl + "/api/game/start"))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + port + "\", \"message\":\"hello\"}"))
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            // First fire
            Fire fire = new Fire(null);
            int myPort = port;
            int adversaryPort = fire.parseAdversaryPort(adversaryUrl);
            fire.randomFire(myPort, adversaryPort);
        }
    }
}
