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

    public Fire(GameGrid gameGrid) { this.gameGrid = gameGrid; }

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

    public int getPort(HttpExchange exchange) {
        String s = exchange.getRequestHeaders().getFirst("Host");
        return Integer.parseInt(s.substring(s.indexOf("localhost:")+10).trim());
    }

    public int getAdversaryPort(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        return Integer.parseInt(query.substring(query.indexOf("port=")+5).trim().split("&")[0]);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String body;
        try {
            body = constructResponseBody(exchange);
            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.sendResponseHeaders(202, body.length());
        } catch (Exception e) {
            body = "Bad Request"; exchange.sendResponseHeaders(400, body.length());
        }
        Launcher launcher = new Launcher(); launcher.displayGrid(gameGrid);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes()); }

        int port = getPort(exchange);
        int adversaryPort = getAdversaryPort(exchange);
        if (gameGrid.isShipLeftOnGrid()) { this.randomFire(port, adversaryPort); }
    }

    public void randomFire(int port, int adversaryPort) {
        Random random = new Random();
        char randomLetter = (char)(random.nextInt(10) + 65); int randomY = random.nextInt(11);
        String coordinates = randomLetter + Integer.toString(randomY);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + adversaryPort + "/api/game/fire?port=" + port + "&cell=" + coordinates))
            .setHeader("Accept", "application/json").setHeader("Content-Type", "application/json").GET().build();
        client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest2 = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + adversaryPort + "/api/game/fire?cell=" + coordinates))
            .setHeader("Accept", "application/json").setHeader("Content-Type", "application/json").GET().build();
        client.sendAsync(getRequest2, HttpResponse.BodyHandlers.ofString());
    }
}
