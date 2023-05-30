package dev.pabloac.client;

import dev.pabloac.utils.IpAddressValidator;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

class XMLtoTCPTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private static final String XML_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Msg Name=\"OpenStudies\" Type=\"XA\">"
            + "<Param Name=\"ProcessId\">Identificador</Param></Msg>";

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void main() {
        testIpValidator();

        Assertions.assertEquals(XMLtoTCP.createXML("Identificador"), XML_RESULT, "createXML");

        testWithMockServer();

    }

    public void testIpValidator(){
        assertEquals(true, IpAddressValidator.isValidIPV4Address("0.0.0.0"));
        assertEquals(true, IpAddressValidator.isValidIPV4Address("255.255.255.255"));
        assertEquals(false, IpAddressValidator.isValidIPV4Address("999.255.255.255"));
        assertEquals(false, IpAddressValidator.isValidIPV4Address("127.000.000.001"));
        assertEquals(false, IpAddressValidator.isValidIPV4Address("-1"));
        assertEquals(false, IpAddressValidator.isValidIPV4Address("google"));
    }

    public void testWithMockServer(){
        Thread mockServer = new dev.pabloac.mockServer.MockSever("MockServer");
        mockServer.start();

        setUpStreams();

        //Correct paramenters
        ByteArrayInputStream inputStream = new ByteArrayInputStream("127.0.0.1\n6000\nProcess1\n".getBytes());
        Scanner in = new Scanner(inputStream);
        XMLtoTCP.establishTcpConnection(in);

        String[] out = outContent.toString().split("\n");
        Assertions.assertEquals("OK", out[out.length - 1].trim(), "Ok from mockServer");

        ExpectedException exception = ExpectedException.none();
        exception.expect(IOException.class);
        inputStream = new ByteArrayInputStream("127.0.0.1\n2000\nProcess1\n".getBytes());
        in = new Scanner(inputStream);
        XMLtoTCP.establishTcpConnection(in);


        restoreStreams();
    }

    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}