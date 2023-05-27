package server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketHandlingThread extends Thread {
    private Socket clientSocket;

    public SocketHandlingThread(Socket clientSocket) {
        this.clientSocket = clientSocket;

        InetAddress clientAddress = clientSocket.getInetAddress();
        System.out.println("\n----------------------------------------");
        System.out.println("Client address: " + clientAddress + ", port: " + clientSocket.getPort());
        System.out.println("Connected to port: " + clientSocket.getLocalPort() + ".\n");
    }

    public SocketHandlingThread(String name, Socket clientSocket) {
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
            String line;
            do {
                line = dis.readUTF();

                if (!line.equals("*")){
                    System.out.println("Recibido: " + line);
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    System.out.println("Enviado: " + line.toUpperCase());
                    dos.writeUTF(line.toUpperCase());
                }
            } while (!line.equals("*"));

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
