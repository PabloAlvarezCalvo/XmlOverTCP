package dev.pabloac.mockServer;

import java.io.IOException;
import java.net.ServerSocket;

public class MockSever extends Thread {
    private static final int SERVER_PORT = 6000;
    public MockSever(String name) {
        super(name);
    }

    public void run(){
        int connectionCount = 0;

        while (true) {
            try (
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT)
            ) {
                System.out.println("Awaiting connection...");
                Thread clientThread = new ClientHandlingThread("Client " + ++connectionCount, serverSocket.accept());
                clientThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
