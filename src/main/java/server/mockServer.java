package server;

import server.thread.ClientHandlingThread;

import java.io.IOException;
import java.net.ServerSocket;

public class mockServer {
    public static void main(String[] args) {
        int serverPort = 6000;
        int connectionCount = 0;

        while (true) {
            try (
                    ServerSocket serverSocket = new ServerSocket(serverPort)
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
