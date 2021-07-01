package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartStopServer {
    public void displayHit(GameGrid gameGrid, int i, int j) {
        System.out.print("\u001B[31m" + "X" + "\u001B[0m  |  ");
        gameGrid.get_grid()[i][j] = null;
    }

    public void displayMiss(GameGrid gameGrid, int i, int j) {
        System.out.print("\u001B[31m" + "." + "\u001B[0m  |  ");
        gameGrid.get_grid()[i][j] = null;
    }
    public void displayGrid(GameGrid gameGrid) {
        System.out.print(String.format("\033[H\033[2J"));
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                Ship elmt = gameGrid.get_grid()[i][j];
                if (elmt == null) { System.out.print(".  |  "); }
                else if (elmt.getSlug().equals("miss")) { displayMiss(gameGrid, i, j); }
                else if (elmt.getSlug().equals("hit")) { displayHit(gameGrid, i, j); }
                else { System.out.print(elmt.getSlug().toUpperCase().charAt(0) + "  |  "); }
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

}
