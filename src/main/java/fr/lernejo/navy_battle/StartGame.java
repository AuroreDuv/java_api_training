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
        String body;
        JSONObject postRequest = null;
        try (InputStream inputSchema = getClass().getResourceAsStream("/schema.json")) { // Get Json Schema for validation
            postRequest = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            SchemaLoader.load(new JSONObject(new JSONTokener(inputSchema))).validate(postRequest); // Json Schema Validation
            // Send response with status Accepted 202
            body = "{\"id\": \"2aca7611-0ae4-49f3-bf63-75bef4769028\", \"url\": \"http://" + exchange.getRequestHeaders().getFirst("Host") + "\", \"message\": \"May the best code win\"}";
            exchange.sendResponseHeaders(202, body.length());
        } catch (Exception e) {
            body = "Bad Request";
            exchange.sendResponseHeaders(400, body.length());
        }
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }

        String url = exchange.getRequestHeaders().getFirst("Host");
        int port = Integer.parseInt(url.substring(url.indexOf("localhost:")+10).trim());
        String adversaryUrl = postRequest.getString("url");
        int adversaryPort = Integer.parseInt(adversaryUrl.substring(adversaryUrl.indexOf("localhost:")+10).trim());

        Fire fire = new Fire(null);
        fire.randomFire(port, adversaryPort);
    }
}
