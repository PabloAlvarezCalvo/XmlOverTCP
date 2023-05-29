package client;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLtoTCP {

    //<Msg Name="OpenStudies" Type="XA"><Param Name="ProcessId">Identificador</Param></Msg>";
    private final static String ROOT_TAG = "Msg";
    private final static String PARAM_TAG = "Param";
    private final static String ROOT_NAME_VALUE = "OpenStudies";
    private final static String PARAM_NAME_VALUE = "ProcessId";
    private final static String NAME_ATT_TAG = "Name";
    private final static String TYPE_ATT_TAG = "Type";
    private final static String TYPE_ATT_VALUE = "XA";
    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        establishTcpConnection(sc);
    }

    public static void establishTcpConnection(Scanner sc) {
        InetAddress serverAddress;
        try {
            serverAddress = inputIP(sc);

            int serverPort = inputPort(sc);
            String processIdentifier = inputProcessIdentifier(sc);
            String xmlString = createXML(processIdentifier);

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
                clientSocket.shutdownOutput();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static InetAddress inputIP(Scanner sc) throws UnknownHostException {
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
            throw new UnknownHostException(e.getMessage());
        }

        return ipAddress;
    }

    private static int inputPort(Scanner sc) {
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

    private static String inputProcessIdentifier(Scanner sc){
        System.out.println("Specify the process ID:");
        String pid;
        do {
            pid = sc.nextLine();
            if(pid.length() < 1 ) System.out.println("The process ID can't be blank, please try again:");
        } while (pid.length() < 1);

        return pid;
    }

    private static String createXML(String processIdentifier){
        try {
            String output;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            try {
                builder = dbFactory.newDocumentBuilder();

                DOMImplementation implementation = builder.getDOMImplementation();
                Document document = implementation.createDocument(null, null, null);
                document.setXmlStandalone(true);

                // root element
                Element rootElement = document.createElement(ROOT_TAG);
                document.appendChild(rootElement);
                // setting attribute to the root element
                Attr attrName = document.createAttribute(NAME_ATT_TAG);
                attrName.setValue(ROOT_NAME_VALUE);
                rootElement.setAttributeNode(attrName);
                Attr attrType = document.createAttribute(TYPE_ATT_TAG);
                attrType.setValue(TYPE_ATT_VALUE);
                rootElement.setAttributeNode(attrType);

                Element param = document.createElement(PARAM_TAG);
                Attr paramAttrName = document.createAttribute(NAME_ATT_TAG);
                paramAttrName.setValue(PARAM_NAME_VALUE);
                param.setAttributeNode(paramAttrName);
                param.setTextContent(processIdentifier);

                rootElement.appendChild(param);

                TransformerFactory tfFactory = TransformerFactory.newInstance();
                Transformer transformer = tfFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");


                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(writer));
                output = writer.getBuffer().toString().replace("\n|\r", "");
            } catch (TransformerException e) {
                output = "";
                System.err.println(e.getMessage());
            }

            return output;


        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
