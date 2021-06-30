package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

class StartGame implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream inputSchema = getClass().getResourceAsStream("/schema.json")) { // Get Json Schema for validation
            JSONObject postRequest = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            SchemaLoader.load(new JSONObject(new JSONTokener(inputSchema))).validate(postRequest); // Json Schema Validation
            // Send response with status Accepted 202
            body = "{\"id\": \"2aca7611-0ae4-49f3-bf63-75bef4769028\", \"url\": \"http://" + exchange.getRequestHeaders().getFirst("Host") + "\", \"message\": \"May the best code win\"}";
            exchange.sendResponseHeaders(202, body.length());
            Random random = new Random();

            char randomLetter = (char)(random.nextInt(10) + 65);
            int randomY = random.nextInt(11);
            String coordinates = randomLetter + Integer.toString(randomY);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create("http://" + postRequest.getString("url") + "/api/game/fire?cell=" + coordinates))
                .build();
            client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            body = "Bad Request";
            exchange.sendResponseHeaders(400, body.length());
        }
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }
}
