package org.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    static void setup() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the message you would like to send: ");
        TCP.msg = scanner.nextLine();
        System.out.println("Enter the amount of bytes each packet should send: ");
        TCP.msgSize = scanner.nextInt();
    }

    
    public static void main(String[] args) {
        setup();
        String host = "localhost";
        int echoServicePortNumber = 26880;

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(host, echoServicePortNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                userInput = stdIn.readLine();
                byte[] decoded = TCP.xorDecode(userInput.getBytes(), TCP.key);
                System.out.println("echo: " + in.readLine());
                System.out.println(Arrays.toString(decoded));
            }

            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
        }
        catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }
}
