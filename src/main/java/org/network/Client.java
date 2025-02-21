package org.network;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {
    /**
     * The server where the data will be sent to.
     * Host: server url
     * Port: port
     */
    static String host = "localhost";
    static int echoServicePortNumber = 26880;
    static int udpServicePortNumber = 26881;
    static String agreement = "13610152"; // Triangular Numbers


    /**
     * Data that is being sent to the server.
     * String converted into bytes that is encrypted through an XOR operation.
     */
    static ArrayList<byte[]> encryptedPackets = new ArrayList<>();

    /**
     * Although I do not need this and encrypt the packets immediately, I want to keep it just as a good visualizer.
     */
    static ArrayList<String> packets = new ArrayList<>();

    /**
     * Get the necessary information for the program
     * MSG = STRING message that will be sent to the server
     * MSG_SIZE = packet size. amount of data that will be sent per packet.
     * ITERATIONS = number of packets to send.
     * if iteration*msg_size < message length, then the message will be cut off.
     * else if iteration*msg_size > message length, then the program will break.
     * <p>
     * return @encryptedPackets - an ArrayList that stores encrypted message as a byte[] - will be sent to the server
     */
    static void setup() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter the message you would like to send: ");
//        Helpers.msg = scanner.nextLine();
//        System.out.println("Enter the amount of bytes each packet should send: ");
//        Helpers.msgSize = scanner.nextInt();
//        System.out.println("Enter the number of packets to send: ");
//        Helpers.iterations = scanner.nextInt();
//        System.out.println("TCP Connection? (False = UDP)");
//        boolean tcp = scanner.nextBoolean();
//        System.out.println("Throughput or Latency?");
//        boolean throughputLatency = scanner.nextBoolean();
//        scanner.close();

//        int stringIndex = 0;
//        for (int i = 0; i < Helpers.iterations; i++) {
//            packets.add(Helpers.msg.substring(stringIndex, stringIndex + Helpers.msgSize));
//            stringIndex += Helpers.msgSize;
//        }
//        for (String s : packets) {
//            encryptedPackets.add(Helpers.xorEncode(s.getBytes(), Helpers.key));
//        }
//        System.out.println(packets);
//        printPackets(encryptedPackets);
//
//        // TODO: UNCOMMENT AND FIX WHEN THROUGHPUT ENDS
////        if (tcp) {
////            TCPConnection();
////        } else {
////            UDPConnection();
////        }

        //THROUGHPUT TEST
        System.out.println(Helpers.msg.getBytes().length);
        char[] megabyteString = new char[1048576];
        Arrays.fill(megabyteString, 's');
        Helpers.msg = new String(megabyteString);
        System.out.println("Bit Size of Input: " + Helpers.msg.getBytes().length);
        Helpers.msgSize = 512;
        Helpers.iterations = 2048;

        int stringIndex = 0;
        for (int i = 0; i < Helpers.iterations; i++) {
            String message = Helpers.msg.substring(stringIndex, stringIndex + Helpers.msgSize);
            message+= agreement;
            packets.add(message);
            stringIndex += Helpers.msgSize;
        }
        for (String s : packets) {
            encryptedPackets.add(Helpers.xorEncode(s.getBytes(), Helpers.key));
        }

        throughputTCP();;
    }

    /**
     * Using Sockets (TCP), we send and receive packets to and from the server in a connection-based protocol.
     */
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

    static void throughputTCP() {
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
                System.out.println("Throughput: " + Helpers.iterations/diffInSeconds + " op/s");
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

    /**
     * Using Datagrams, (UDP Connection), we send packets to and from the server through a connectionless protocol.
     */
    static void UDPConnection() {
        try (DatagramSocket socket = new DatagramSocket(26881)) {
            InetAddress address = InetAddress.getByName(host);
            for (int i = 0; i < encryptedPackets.size(); i++) {
                long sendTime = System.nanoTime();
                DatagramPacket packet = new DatagramPacket(encryptedPackets.get(i), encryptedPackets.get(i).length, address, 26882);
                socket.send(packet);

                // Receive the response from the server
                byte[] buffer = new byte[Helpers.msgSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                long receiveTime = System.nanoTime();
                double diffInSeconds = (receiveTime - sendTime) * 1e-9;

                byte[] data = receivePacket.getData();
                System.out.println("Datagram " + (i + 1) + " sent and received in " + diffInSeconds + " seconds");
                System.out.println(new String(Helpers.xorDecode(data, Helpers.key)));
            }
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        setup();
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