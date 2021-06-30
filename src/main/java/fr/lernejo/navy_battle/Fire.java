package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class Fire implements HttpHandler {
    final private GameGrid gameGrid;

    public Fire(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public String getConsequence(int x, int y) {
        Ship ship = gameGrid.get_grid()[x][y];
        if (ship != null) {
            gameGrid.hitShip(x, y);
            if (ship.isAlive(gameGrid)) {
                return "hit";
            } else {
                return "sunk";
            }
        }
        return "miss";
    }

    public String constructResponseBody(HttpExchange exchange) throws IOException {
        String cell = exchange.getRequestURI().getQuery().replace("cell=", "");
        int x = Integer.parseInt(cell.replace(Character.toString(cell.charAt(0)), "")) - 1;
        int y = cell.charAt(0) - 65;
        String shipState; Boolean shipLeft; String body;
        shipState = getConsequence(x, y);
        shipLeft = gameGrid.isShipLeftOnGrid();
        body = "{\"consequence\": \"" + shipState + "\", \"shipLeft\": " + shipLeft + "}";
        exchange.sendResponseHeaders(202, body.length());
        return body;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String body;
        System.out.print(String.format("\033[H\033[2J"));

        try {
            body = constructResponseBody(exchange);
        } catch (Exception e) {
            body = "Bad Request";
            exchange.sendResponseHeaders(400, body.length());
        }
        Launcher launcher = new Launcher();
        launcher.displayGrid(gameGrid);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }
}
