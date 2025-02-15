package org.network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    static ArrayList<String> packets = new ArrayList<>();
    static ArrayList<byte[]> encryptedPackets = new ArrayList<>();

    /**
     * Get the necessary information to set the program up
     * MSG = message that you're going to send to the server
     * MSG_SIZE = packet size
     * ITERATIONS = number of packets to send
     * return @encryptedPackets - an ArrayList that stores encrypted message as a byte[] - will be sent to the server
     */
    static void setup() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter the message you would like to send: ");
//        TCP.msg = scanner.nextLine();
//        System.out.println("Enter the amount of bytes each packet should send: ");
//        TCP.msgSize = scanner.nextInt();
//        System.out.println("Enter the number of packets to send: ");
//        TCP.iterations = scanner.nextInt();
//        scanner.close();

        int stringIndex = 0;
        for (int i = 0; i < TCP.iterations; i++) {
            packets.add(TCP.msg.substring(stringIndex, stringIndex + TCP.msgSize));
            stringIndex+= TCP.msgSize;
        }
        for (String s : packets) {
            encryptedPackets.add(TCP.xorEncode(s.getBytes(), TCP.key));
        }
        System.out.println(packets);
        printPackets(encryptedPackets);
    }

    /**
     * DEBUG
     * @param encryptedPackets - print encrypted packets in terminal in readable format
     */
    static void printPackets(ArrayList<byte[]> encryptedPackets) {
        for (byte[] bytes : encryptedPackets) {
            System.out.println(Arrays.toString(bytes));
        }
    }

    public static void main(String[] args) {
        setup();
        String host = "localhost";
        int echoServicePortNumber = 26880;

        Socket echoSocket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            echoSocket = new Socket(host, echoServicePortNumber);
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());

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
            for (int i = 0; i < encryptedPackets.size(); i++) {
                long sendTime = System.nanoTime();
                out.writeInt(TCP.msgSize);
                out.write((encryptedPackets.get(i)));
                int length = in.readInt();
                byte[] byteArray = new byte[length];
                in.readFully(byteArray);
                long receiveTime = System.nanoTime();
                double diffInSeconds = (receiveTime - sendTime)* Math.pow(10,9);
                System.out.println("packet sent in " + diffInSeconds + " seconds");
            }

            out.close();
            in.close();
            echoSocket.close();
        }
        catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }
}
