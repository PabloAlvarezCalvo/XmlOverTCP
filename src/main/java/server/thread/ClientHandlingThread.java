package server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandlingThread extends Thread {
    private Socket clientSocket;

    public ClientHandlingThread(Socket clientSocket) {
        this.clientSocket = clientSocket;

        InetAddress clientAddress = clientSocket.getInetAddress();
        System.out.println("\n----------------------------------------");
        System.out.println("Client address: " + clientAddress + ", port: " + clientSocket.getPort());
        System.out.println("Connected to port: " + clientSocket.getLocalPort() + ".\n");
    }

    public ClientHandlingThread(String name, Socket clientSocket) {
        super(name);

        this.clientSocket = clientSocket;

        InetAddress clientAddress = clientSocket.getInetAddress();
        System.out.println("\n----------------------------------------");
        System.out.println("Client address: " + clientAddress + ", port: " + clientSocket.getPort());
        System.out.println("Connected to port: " + clientSocket.getLocalPort() + ".\n");
    }

    public void run(){
        try (
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream())
        ){
            System.out.println("Received from client:");
            System.out.println(dis.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Thread " + this.getName() + " execution ended.");

    }
}
