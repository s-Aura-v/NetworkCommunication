package org.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Client {
    /**
     * The server where the data will be sent to.
     * Host: server url
     * Port: port where the networking occurs
     */
    static String host = "moxie.cs.oswego.edu";
    static int echoServicePortNumber = 26880;
    static int udpServicePortNumber = 26881;
    static String agreement = "13610152"; // Triangular Numbers
    static byte[] agreementBytes = new byte[]{1,3,6,1,0,1,5,2};

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
    static void setup() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Test Scenario: \n" +
                "TRUE: Latency \n" +
                "FALSE: Throughput");
        boolean isTestingLatency = scanner.nextBoolean();
        scanner.nextLine();
        if (isTestingLatency) {
            System.out.println("Enter the message you would like to send: ");
            Helpers.msg = scanner.nextLine();
            System.out.println("Enter the amount of bytes each packet should send: ");
            Helpers.msgSize = scanner.nextInt();
            System.out.println("Enter the number of iterations to run: ");
            Helpers.iterations = scanner.nextInt();
            System.out.println("Select Connection Type: \n" +
                    "TRUE: TCP \n" +
                    "FALSE: UDP");
            if (scanner.nextBoolean()) {
                Helpers.test = 1;
            } else {
                Helpers.test = 3;
            }
        } else {
            char[] megabyteString = new char[1048576];
            Arrays.fill(megabyteString, 's');
            Helpers.msg = new String(megabyteString);
            System.out.println("Enter the amount of bytes each packet should send: ");
            Helpers.msgSize = scanner.nextInt();
            System.out.println("Enter the number of messages you would like to send: ");
            Helpers.iterations = scanner.nextInt();
            System.out.println("Enter the number of iterations to run: ");
            Helpers.numberOfMessages = scanner.nextInt();
            System.out.println("Select Connection Type: \n" +
                    "TRUE: TCP \n" +
                    "FALSE: UDP");
            if (scanner.nextBoolean()) {
                Helpers.test = 2;
            } else {
                Helpers.test = 4;
            }
        }
        scanner.close();

        int stringIndex = 0;

        for (int i = 0; i < Helpers.iterations; i++) {
            packets.add(Helpers.msg.substring(stringIndex, stringIndex + Helpers.msgSize));
            stringIndex += Helpers.msgSize;
        }

        for (String s : packets) {
            encryptedPackets.add(Helpers.xorEncode(s.getBytes(), Helpers.key));
        }

        if (Helpers.test == 2 || Helpers.test == 4) {
            for (int i = 0; i < encryptedPackets.size(); i++) {
                byte[] encryptedPacket = encryptedPackets.get(i);
                byte[] newPacket = new byte[encryptedPacket.length + agreementBytes.length];

                System.arraycopy(encryptedPacket, 0, newPacket, 0, encryptedPacket.length);
                System.arraycopy(agreementBytes, 0, newPacket, encryptedPacket.length, agreementBytes.length);
                encryptedPackets.set(i, newPacket);
            }
        }

        System.out.println(packets);
        printPackets(encryptedPackets);

        // TODO: UNCOMMENT AND FIX WHEN THROUGHPUT ENDS
        switch (Helpers.test) {
            case (1):
                TCPConnection();
                break;
            case (2):
                throughputTCP();
                break;
            case (3):
                UDPConnection();
                break;
            case (4):
                throughputUDP();
                break;
        }
    }

    /**
     * Using Sockets (TCP), we send and receive packets to and from the server in a connection-based protocol.
     */
    static void TCPConnection() throws IOException {
        ArrayList<Double> tcpLatencyData = new ArrayList<>();
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
                tcpLatencyData.add(diffInSeconds);

                System.out.println(Helpers.msgSize + " byte packet " + (i + 1) + " sent and received in " + diffInSeconds + " seconds");
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
        } finally {
            Graphing.graph(tcpLatencyData, "TCPLatencyBenchmark");

        }
    }

    static void throughputTCP() throws IOException {
        ArrayList<Double> tcpThroughputData = new ArrayList<>();
        try (Socket echoSocket = new Socket(host, echoServicePortNumber);
             DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());
             DataInputStream in = new DataInputStream(echoSocket.getInputStream())) {

            echoSocket.setSoTimeout(30000);

            for (int numberOfMessages = 0; numberOfMessages < Helpers.numberOfMessages; numberOfMessages++) {
                long sendTime = System.nanoTime();
                for (int i = 0; i < encryptedPackets.size(); i++) {
                    out.writeInt(encryptedPackets.get(i).length);
                    out.write(encryptedPackets.get(i));
                    out.flush();

                    int length = in.readInt();
                    byte[] byteArray = new byte[length];
                    in.readFully(byteArray);
                }
                long receiveTime = System.nanoTime();
                double diffInSeconds = (receiveTime - sendTime)  * 1e-9;
                double throughput = (Helpers.msgSize * Byte.SIZE) / (diffInSeconds);
                tcpThroughputData.add(throughput);
                System.out.println("All " + Helpers.numberOfMessages + " packets sent and received in " + diffInSeconds +
                        " seconds with a throughput of " + Helpers.iterations / diffInSeconds + " op/s");
            }

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
        } finally {
            Graphing.graph(tcpThroughputData, "TCPThroughputBenchmark");
        }
    }

    /**
     * Using Datagrams, (UDP Connection), we send packets to and from the server through a connectionless protocol.
     */
    static void UDPConnection() throws IOException {
        ArrayList<Double> udpLatencyData = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket(udpServicePortNumber)) {
            InetAddress address = InetAddress.getByName(host);
            for (int i = 0; i < encryptedPackets.size(); i++) {
                DatagramPacket packet = new DatagramPacket(encryptedPackets.get(i), encryptedPackets.get(i).length, address, 26882);
                socket.send(packet);
                long sendTime = System.nanoTime();

                // Receive the response from the server
                byte[] buffer = new byte[Helpers.msgSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                long receiveTime = System.nanoTime();
                double diffInSeconds = (receiveTime - sendTime) * 1e-9;
                udpLatencyData.add(diffInSeconds);

                byte[] data = receivePacket.getData();
                System.out.println("Datagram " + (i + 1) + " sent and received in " + diffInSeconds + " seconds");
                System.out.println(new String(Helpers.xorEncode(data, Helpers.key)));
            }
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Graphing.graph(udpLatencyData, "UDPLatencyBenchmark");
        }
    }

    static void throughputUDP() throws IOException {
        ArrayList<Double> udpThroughputData = new ArrayList<>();
        boolean loopBroken = false;
        try (DatagramSocket socket = new DatagramSocket(udpServicePortNumber)) {
            socket.setSoTimeout(30000);
            InetAddress address = InetAddress.getByName(host);

            for (int numberOfMessages = 0; numberOfMessages < Helpers.numberOfMessages; numberOfMessages++) {
                long sendTime = System.nanoTime();
                for (int i = 0; i < encryptedPackets.size(); i++) {
                    try {
                        DatagramPacket packet = new DatagramPacket(encryptedPackets.get(i), encryptedPackets.get(i).length, address, 26882);
                        socket.send(packet);

                        // Receive the response from the server
                        byte[] buffer = new byte[Helpers.msgSize];
                        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(receivePacket);

                        byte[] data = receivePacket.getData();
                        String decoded = new String(Helpers.xorEncode(data, Helpers.key));
                    } catch (SocketTimeoutException e) {
                        // Handle timeout (packet lost)
                        System.err.println("Packet lost or no response from server, skipping to next message...");
                        loopBroken = true;
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!loopBroken) {
                    long receiveTime = System.nanoTime();
                    double diffInSeconds = (receiveTime - sendTime) * 1e-9;
                    double throughput = (Helpers.msgSize * Byte.SIZE) / (diffInSeconds);

                    udpThroughputData.add(throughput);
                    System.out.println("All " + Helpers.numberOfMessages + " packets sent and received in " + diffInSeconds +
                            " seconds with a throughput of " + throughput + " op/s");
                } else {
                    udpThroughputData.add(0.0);
                    loopBroken = false;
                }
            }
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } finally {
            Graphing.graph(udpThroughputData, "UDPThroughputBenchmark");
        }
    }

    public static void main(String[] args) throws IOException {
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