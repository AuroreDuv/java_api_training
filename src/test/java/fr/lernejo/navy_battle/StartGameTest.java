package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class CallPostHandlerTest {
    @Test
    void get_handler_test_api_game_start_with_no_request() throws Exception {
        int port = 9876;
        Launcher launcher = new Launcher();
        HttpServer server = launcher.start_server(port);

        ProcessBuilder pb = new ProcessBuilder("curl", "http://localhost:9876/api/game/start");
        Process p = pb.start();
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Assertions.assertEquals("Bad Request", br.readLine());
        p.destroy();

        launcher.stop_server(server);
    }

    @Test
    void get_handler_test_api_game_start_with_request_without_message() throws Exception {
        int port = 9876;
        Launcher launcher = new Launcher();
        HttpServer server = launcher.start_server(port);

        ProcessBuilder pb = new ProcessBuilder("curl", "--request", "POST", "--data", "{\"id\": \"0c575465-21f6-43c9-8a2d-bc64c3ae6241\", \"url\": \"http://localhost:8795\"}", "http://localhost:9876/api/game/start");
        Process p = pb.start();
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Assertions.assertEquals("Bad Request", br.readLine());
        p.destroy();

        launcher.stop_server(server);
    }

    @Test
    void get_handler_test_api_game_start_with_request_ok() throws Exception {
        int port = 9876;
        Launcher launcher = new Launcher();
        HttpServer server = launcher.start_server(port);

        ProcessBuilder pb = new ProcessBuilder("curl", "--request", "POST", "--data", "{\"id\": \"0c575465-21f6-43c9-8a2d-bc64c3ae6241\", \"url\": \"http://localhost:8795\", \"message\": \"I will crush you!\"}", "http://localhost:9876/api/game/start");
        Process p = pb.start();
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Assertions.assertEquals("{\"id\": \"2aca7611-0ae4-49f3-bf63-75bef4769028\", \"url\": \"http://localhost:9876\", \"message\": \"May the best code win\"}", br.readLine());
        p.destroy();

        launcher.stop_server(server);
    }
}
