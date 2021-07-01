package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher {
    public void displayGrid(GameGrid gameGrid) {
        System.out.print(String.format("\033[H\033[2J"));
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++)
            {
                Ship elmt = gameGrid.get_grid()[i][j];
                if (elmt == null) {
                    System.out.print(".  |  ");
                }
                else if (elmt.get_slug().equals("miss")) {
                    System.out.print("\u001B[31m" + "." + "\u001B[0m");
                    System.out.print("  |  ");
                    gameGrid.get_grid()[i][j] = null;
                }
                else if (elmt.get_slug().equals("hit")) {
                    System.out.print("\u001B[31m" + "X" + "\u001B[0m");
                    System.out.print("  |  ");
                    gameGrid.get_grid()[i][j] = null;
                }
                else {
                    System.out.print(elmt.get_slug().toUpperCase().charAt(0));
                    System.out.print("  |  ");
                }
            }
            System.out.print("\n");
        }
    }

    public HttpServer startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        GameGrid gameGrid = new GameGrid(10, 10);
        displayGrid(gameGrid);
        server.createContext("/ping", new Ping());
        server.createContext("/api/game/start", new StartGame());
        server.createContext("/api/game/fire", new Fire(gameGrid));
        server.setExecutor(executor);
        server.start();

        return server;
    }

    public void stopServer(HttpServer server) {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Launcher launcher = new Launcher();
        launcher.startServer(port);

        if (args.length != 1) {
            String adversaryUrl = args[1];
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(adversaryUrl + "/api/game/start"))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + port + "\", \"message\":\"hello\"}"))
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            Random random = new Random();

            char randomLetter = (char)(random.nextInt(10) + 65);
            int randomY = random.nextInt(11);
            String coordinates = randomLetter + Integer.toString(randomY);

            HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/game/fire?cell=" + coordinates))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();
            client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
        }
    }
}
