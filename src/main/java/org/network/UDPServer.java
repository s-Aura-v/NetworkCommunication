package org.network;

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
                DatagramPacket packet = new DatagramPacket(new byte[maxBufferSize], maxBufferSize);
                serverSocket.receive(packet);

                byte[] data = packet.getData();
                maxBufferSize = packet.getLength();
                String message = new String(Helpers.xorEncode(data, Helpers.key));
                System.out.println("Decoded byte array: " + message);
                if (message.length() > 8 && message.substring(message.length() - 8).equals(Client.agreement)) {
                    numHits++;
                }

                DatagramPacket responsePacket = new DatagramPacket(
                        data, data.length, packet.getAddress(), packet.getPort());
                serverSocket.send(responsePacket);
            }
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("[ONLY FOR THROUGHPUT] 8-bit key verification hits: " + numHits);

        }
    }
}
