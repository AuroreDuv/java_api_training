package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class CallGetHandlerTest {
    @Test
    void get_handler_test_ping_prints_hello() throws Exception {
        int port = 9876;
        Launcher launcher = new Launcher();
        HttpServer server = launcher.start_server(port);

        ProcessBuilder pb = new ProcessBuilder("curl", "http://localhost:9876/ping");
        Process p = pb.start();

        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        Assertions.assertEquals("OK", br.readLine());

        p.destroy();
        launcher.stop_server(server);
    }
}
