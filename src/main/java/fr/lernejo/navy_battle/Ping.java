package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

class Ping implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String body = "OK";
        exchange.sendResponseHeaders(200, body.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }
}

