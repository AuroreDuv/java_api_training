package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class StartGame implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String body; String adversaryUrl = "";
        try (InputStream inputSchema = getClass().getResourceAsStream("/schema.json")) { // Get Json Schema for validation
            JSONObject postRequest = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            SchemaLoader.load(new JSONObject(new JSONTokener(inputSchema))).validate(postRequest); // Json Schema Validation
            body = "{\"id\": \"2aca7611-0ae4-49f3-bf63-75bef4769028\", \"url\": \"http://localhost:" + exchange.getRequestHeaders().getFirst("X-My-Port") + "\", \"message\": \"May the best code win\"}";
            exchange.sendResponseHeaders(202, body.length());
        } catch (Exception e) { body = "Bad Request"; exchange.sendResponseHeaders(400, body.length()); }
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes()); }

        // First fire
        Fire fire = new Fire(null);
        int myPort = fire.parsePort(exchange.getRequestHeaders().getFirst("Host"));
        int adversaryPort = Integer.parseInt(exchange.getRequestHeaders().getFirst("X-My-Port"));
        System.out.println(myPort);
        System.out.println(adversaryPort);
        fire.randomFire(myPort, adversaryPort);
    }
}
