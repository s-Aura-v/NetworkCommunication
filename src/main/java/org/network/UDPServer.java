package org.network;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDP Connection.  * Receives data from client and decodes it and verifies the agreement code, before sending it back to client.
 * Used for latency.
 */
public class UDPServer {
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
                        DatagramPacket responsePacket = new DatagramPacket(
                                data, maxBufferSize, packet.getAddress(), packet.getPort());
                        serverSocket.send(responsePacket);
                    }
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (IOException e) {
                    System.err.println("Error reading from client: " + e.getMessage());
                    break;
                }
            }
            serverSocket.close();
            throw new IOException();
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        }
    }
}
