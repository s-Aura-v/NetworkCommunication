package org.network;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    static int numHits = 0;

    public static void main(String[] args) {
        int maxBufferSize = 512;
        try (DatagramSocket serverSocket = new DatagramSocket(26882)) {
            System.out.println("Datagram listening...?");
            for (; ; ) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[maxBufferSize], maxBufferSize);
                    serverSocket.receive(packet);

                    maxBufferSize = packet.getLength();
                    byte[] data = packet.getData();
                    String message = new String(Helpers.xorEncode(data, Helpers.key));
                    System.out.println("Decoded byte array: " + message);
                    if (message.length() > 8 && message.substring(message.length() - 8).equals(Client.agreement)) {
                        numHits++;
                    }

                    DatagramPacket responsePacket = new DatagramPacket(
                            data, maxBufferSize, packet.getAddress(), packet.getPort());
                    serverSocket.send(responsePacket);
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (IOException e) {
                    System.err.println("Error reading from client: " + e.getMessage());
                    break;
                } finally {
                    System.out.println("[ONLY FOR THROUGHPUT] 8-bit key verification hits: " + numHits);
                }
            }
            serverSocket.close();
            throw new IOException();
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("[ONLY FOR THROUGHPUT] 8-bit key verification hits: " + numHits);
        }
    }
}
