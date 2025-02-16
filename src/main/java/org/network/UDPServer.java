package org.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {
    public static void main(String[] args) {
        int MAX_BUFFER_SIZE = 512;
        try (DatagramSocket serverSocket = new DatagramSocket(26882)) {
            System.out.println("Datagram listening...?");
            int index = 0;
            for (;;) {
                DatagramPacket packet = new DatagramPacket(new byte[MAX_BUFFER_SIZE], MAX_BUFFER_SIZE);
                serverSocket.receive(packet);
                System.out.println(index);
                index++;

                byte[] data = packet.getData();
                System.out.println("Decoded byte array: " + new String(Helpers.xorDecode(data, Helpers.key)));

                DatagramPacket responsePacket = new DatagramPacket(
                        data, data.length, packet.getAddress(), packet.getPort());
                serverSocket.send(responsePacket);
                System.out.println("Sent response to client.");

            }
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        }
    }
}
