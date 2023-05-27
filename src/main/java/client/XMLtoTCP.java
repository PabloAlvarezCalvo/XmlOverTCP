package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLtoTCP {
    private final static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private final static String XML_BODY = "<Msg Name=\"OpenStudies\" Type=\"XA\"><Param Name=\"ProcessId\">Identificador</Param></Msg>";
    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        InetAddress serverAddress = inputIP();
        int serverPort = inputPort();
        String xmlString = inputPID();

        tcpConnection(serverAddress, serverPort, xmlString);
    }

    private static void tcpConnection(InetAddress serverAddress, int serverPort, String xmlString) {
        try(
                Socket clientSocket = new Socket(serverAddress, serverPort);
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream())
        ){
            System.out.println("Sending:");
            System.out.println(xmlString + "\n");

            dos.writeUTF(xmlString);

            System.out.println("Server response:");
            System.out.println(dis.readUTF());


        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static InetAddress inputIP() {
        InetAddress ipAddress = null;
        boolean validIp;

        Pattern ipPattern = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");

        System.out.println("Specify the IP to connecto to:");

        String ipString;
        do {
            ipString = sc.nextLine();
            Matcher matcher = ipPattern.matcher(ipString);
            validIp = matcher.matches();
            if (!validIp) System.out.println("Incorrect IP address, please try again:");
        } while (!validIp);


        try {
            ipAddress = InetAddress.getByName(ipString);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
        }

        return ipAddress;
    }

    private static int inputPort() {
        int port = -1;
        boolean validPort = false;


        System.out.println("Specify the port to connecto to:");
        do {
            try {
                port = Integer.parseInt(sc.nextLine());

                if (port > 0 && port < 49151) {
                    validPort = true;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            if (!validPort) System.out.println("Port out of range (1 - 49151), please try again:");

        } while (!validPort);

        return port;
    }

    private static String inputPID(){
        System.out.println("Specify the process ID:");
        String pid;
        do {
            pid = sc.nextLine();
            if(pid.length() < 1 ) System.out.println("The process ID can't be blank, please try again:");
        } while (pid.length() < 1);

        return XML_DECLARATION + XML_BODY.replace("Identificador", pid);
    }
}
