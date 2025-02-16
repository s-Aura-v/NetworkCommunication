package org.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    static String host = "localhost";
    static int echoServicePortNumber = 26880;
    static int udpServicePortNumber = 26881;

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the message you would like to send: ");
        Helpers.msg = scanner.nextLine();
        System.out.println("Enter the amount of bytes each packet should send: ");
        Helpers.msgSize = scanner.nextInt();
        System.out.println("Enter the number of packets to send: ");
        Helpers.iterations = scanner.nextInt();
        System.out.println("TCP Connection? (False = UDP)");
        boolean tcp = scanner.nextBoolean();
        scanner.close();

        int stringIndex = 0;
        for (int i = 0; i < Helpers.iterations; i++) {
            packets.add(Helpers.msg.substring(stringIndex, stringIndex + Helpers.msgSize));
            stringIndex += Helpers.msgSize;
        }
        for (String s : packets) {
            encryptedPackets.add(Helpers.xorEncode(s.getBytes(), Helpers.key));
        }
        System.out.println(packets);
        printPackets(encryptedPackets);

        if (tcp) {
            TCPConnection();
        } else {
            UDPConnection();
        }
    }

    public static void main(String[] args) {
        setup();
    }

    static void TCPConnection() {
        try (Socket echoSocket = new Socket(host, echoServicePortNumber);
             DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());
             DataInputStream in = new DataInputStream(echoSocket.getInputStream())) {

            echoSocket.setSoTimeout(30000);

            for (int i = 0; i < encryptedPackets.size(); i++) {
                long sendTime = System.nanoTime();
                out.writeInt(encryptedPackets.get(i).length);
                out.write(encryptedPackets.get(i));
                out.flush();


                int length = in.readInt();
                byte[] byteArray = new byte[length];
                in.readFully(byteArray);

                long receiveTime = System.nanoTime();
                double diffInSeconds = (receiveTime - sendTime) * 1e-9;
                System.out.println("Packet " + (i + 1) + " sent and received in " + diffInSeconds + " seconds");
            }

            System.out.println("All packets sent and received successfully.");
            in.close();
            out.close();
            echoSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO failure.");
            e.printStackTrace();
        }
    }

    static void UDPConnection() {
        try (DatagramSocket socket = new DatagramSocket(26881)) {
            InetAddress address = InetAddress.getByName(host);
            for (int i = 0; i < encryptedPackets.size(); i++) {
                DatagramPacket packet = new DatagramPacket(encryptedPackets.get(i), encryptedPackets.get(i).length, address, 26882);
                socket.send(packet);
                System.out.println("Datagram " + i + " sent.");

                // Receive the response from the server
//                byte[] buffer = new byte[1024];
//                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
//                socket.receive(receivePacket); // Blocks until a response is received

            }
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * DEBUG
     *
     * @param encryptedPackets - print encrypted packets in terminal in readable format
     */
    static void printPackets(ArrayList<byte[]> encryptedPackets) {
        for (byte[] bytes : encryptedPackets) {
            System.out.println(Arrays.toString(bytes));
        }
    }
}