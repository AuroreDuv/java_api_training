package fr.lernejo.navy_battle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

class LauncherTest {
    @Test
    void main_call_with_one_argument() throws Exception {
        int port = 9875;
        String[] args = {Integer.toString(port)};
        Launcher.main(args);

        try (Socket s = new Socket("localhost", port)) {
        } catch (IOException e) {
            Assertions.fail("Server not started");
        }
    }

    @Test
    void main_call_with_two_arguments() throws Exception {
        int port = 9874;
        String url = "http://localhost:8765";
        String[] args = {Integer.toString(port), url};
        Launcher.main(args);

        try (Socket s = new Socket("localhost", port)) {
        } catch (IOException e) {
            Assertions.fail("Server not started");
        }
    }
}
