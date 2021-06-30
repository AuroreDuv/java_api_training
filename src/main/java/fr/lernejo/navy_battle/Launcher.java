package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher {
    public HttpServer start_server(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        server.createContext("/ping", new Ping());
        server.createContext("/api/game/start", new StartGame());
        server.setExecutor(executor);
        server.start();

        return server;
    }

    public void stop_server(HttpServer server) {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Launcher launcher = new Launcher();
        launcher.start_server(port);

        if (args.length != 1) {
            String adversaryUrl = args[1];
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(adversaryUrl + "/api/game/start"))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + port + "\", \"message\":\"hello\"}"))
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
