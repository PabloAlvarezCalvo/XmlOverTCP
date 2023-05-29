package client;

import org.junit.Before;
import org.junit.After;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

class XMLtoTCPTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @org.junit.jupiter.api.Test
    void main() {

        setUpStreams();

        ByteArrayInputStream inputStream = new ByteArrayInputStream("127.0.0.1\n6000\nProcess1".getBytes());
        Scanner in = new Scanner(inputStream);
        XMLtoTCP.establishTcpConnection(in);

        String[] out = outContent.toString().split("\n");
        Assertions.assertEquals("Server response:", out[out.length - 2].trim());
        Assertions.assertEquals("OK", out[out.length - 1].trim());

        restoreStreams();
    }


    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}