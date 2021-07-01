package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class Fire implements HttpHandler {
    final private GameGrid gameGrid;

    public Fire(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public String getConsequence(int x, int y) {
        Ship ship = gameGrid.get_grid()[x][y];
        if (ship != null) {
            gameGrid.hitShip(x, y);
            if (ship.isAlive(gameGrid)) { return "hit"; }
            else { return "sunk"; }
        }
        gameGrid.colorMissedShip(x, y);
        return "miss";
    }

    public String constructResponseBody(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String cell = query.substring(query.indexOf("cell=")+5).trim();
        int x = Integer.parseInt(cell.replace(Character.toString(cell.charAt(0)), "")) - 1;
        int y = cell.charAt(0) - 65;
        String shipState; Boolean shipLeft;
        shipState = getConsequence(x, y);
        shipLeft = gameGrid.isShipLeftOnGrid();
        return "{\"consequence\": \"" + shipState + "\", \"shipLeft\": " + shipLeft + "}";
    }

    public int parsePort(String query) {
        return Integer.parseInt(query.substring(query.indexOf("localhost:")+10).trim());
    }

    public int parseAdversaryPort(String query) {
        return Integer.parseInt(query.substring(query.indexOf("localhost:")+10).trim());
    }

    public void randomFire(int myPort, int adversaryPort) {
        Random random = new Random();
        char randomLetter = (char)(random.nextInt(10) + 65); int randomY = random.nextInt(10) + 1;
        String coordinates = randomLetter + Integer.toString(randomY);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + adversaryPort + "/api/game/fire?cell=" + coordinates))
            .setHeader("Accept", "application/json").setHeader("Content-Type", "application/json")
            .setHeader("X-Adversary-Port", Integer.toString(myPort)).GET().build();
        client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
    }

    public void handle(HttpExchange exchange) throws IOException {
        String body; int adversaryPort = 8000;
        try {
            if (exchange.getRequestHeaders().getFirst("X-Adversary-Port") != null) {
                adversaryPort = Integer.parseInt(exchange.getRequestHeaders().getFirst("X-Adversary-Port"));
            }
            body = constructResponseBody(exchange);
            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.sendResponseHeaders(202, body.length());
        } catch (Exception e) { body = "Bad Request"; exchange.sendResponseHeaders(400, body.length()); }
        StartStopServer startStopServer = new StartStopServer(); startStopServer.displayGrid(gameGrid);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes()); }
        int myPort = parsePort(exchange.getRequestHeaders().getFirst("Host"));
        if (gameGrid.isShipLeftOnGrid()) { this.randomFire(adversaryPort, myPort); }
    }
}
