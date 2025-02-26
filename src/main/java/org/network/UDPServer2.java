package org.network;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class UDPServer2 {
    public static void main(String[] args) {
        int maxBufferSize = 512;
        try (DatagramSocket serverSocket = new DatagramSocket(26882)) {

            // if i can't continue, kill the current data for now.
            serverSocket.setSoTimeout(30000);
            System.out.println("Datagram listening...?");

            for (; ; ) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[maxBufferSize], maxBufferSize);
                    serverSocket.receive(packet);

                    maxBufferSize = packet.getLength();
                    byte[] data = packet.getData();
                    String message = new String(Helpers.xorEncode(data, Helpers.key));

                    DatagramPacket responsePacket = new DatagramPacket(
                            data, maxBufferSize, packet.getAddress(), packet.getPort());
                    serverSocket.send(responsePacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout, continuing to listen...");
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (IOException e) {
                    System.err.println("Error reading from client: " + e.getMessage());
                    break;
                }
            }
            throw new IOException();
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        }
    }
}
