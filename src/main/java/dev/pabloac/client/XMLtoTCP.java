package dev.pabloac.client;

import dev.pabloac.utils.IpAddressValidator;
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

public class XMLtoTCP {

    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        establishTcpConnection(sc);
    }

    /**
     * Prompts the user for an IP address, a port and a process identifier
     * Tries to connect to received IP and port and send an XML object
     * in String form with the specified process identifier
     *
     * @param sc a Scanner object
     */
    protected static void establishTcpConnection(Scanner sc) {
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

    /**
     * Requests the user to write an IP address until a valid IPV4 one is received,
     * then returns an InetAddress object of said address
     * @param sc a Scanner object
     * @return an InetAddress object
     * @throws UnknownHostException
     */
    protected static InetAddress inputIP(Scanner sc) throws UnknownHostException {
        InetAddress ipAddress;
        boolean validIp;

        System.out.println("Specify the IP (v4) to connecto to:");

        String ipString;
        do {
            ipString = sc.nextLine();
            validIp = IpAddressValidator.isValidIPV4Address(ipString);
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

    /**
     * Requests a TCP port to the user until receiving one
     * in the specified range (1 - 49150)
     * @param sc a Scanner object
     * @return a port number in int form
     */
    protected static int inputPort(Scanner sc) {
        final int MIN_PORT = 0;
        final int MAX_PORT = 49151;
        int port = -1;
        boolean validPort = false;


        System.out.println("Specify the port to connecto to:");
        do {
            try {
                port = Integer.parseInt(sc.nextLine());

                if (port > MIN_PORT && port < MAX_PORT) {
                    validPort = true;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            if (!validPort) System.out.println("Port out of range (1 - 49151), please try again:");

        } while (!validPort);

        return port;
    }

    /**
     * Asks the user to input a Procress Indentifier until a non-empty string
     * @param sc a Scanner object
     * @return a String
     */
    protected static String inputProcessIdentifier(Scanner sc){
        System.out.println("Specify the process identifier:");
        String processIdentifier;
        do {
            processIdentifier = sc.nextLine();
            if(processIdentifier.isBlank()) System.out.println("The process ID can't be blank, please try again:");
        } while (processIdentifier.length() < 1);

        return processIdentifier;
    }

    /**
     * Programatically creates an XML string with the specified structure
     * @param processIdentifier the value to be inserted in the <Param> node
     * @return a XML string
     */
    protected static String createXML(String processIdentifier){
        //<?xml version="1.0" encoding="UTF-8"?><Msg Name="OpenStudies" Type="XA"><Param Name="ProcessId">Identificador</Param></Msg>";
        final String ROOT_TAG = "Msg";
        final String PARAM_TAG = "Param";
        final String ROOT_NAME_VALUE = "OpenStudies";
        final String PARAM_NAME_VALUE = "ProcessId";
        final String NAME_ATT_TAG = "Name";
        final String TYPE_ATT_TAG = "Type";
        final String TYPE_ATT_VALUE = "XA";

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
