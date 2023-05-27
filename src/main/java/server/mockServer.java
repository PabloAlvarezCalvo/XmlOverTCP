package server;

import java.io.IOException;
import java.net.ServerSocket;

public class mockServer {
    public static void main(String[] args) {
        int serverPort = 6000;

        try(
                ServerSocket serverSocket = new ServerSocket(serverPort)
        ){
            System.out.println("Awaiting connection...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
