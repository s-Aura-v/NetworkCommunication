package org.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    public static void main(String[] args) {
        int maxBufferSize = 512;
        try (DatagramSocket serverSocket = new DatagramSocket(26882)) {
            System.out.println("Datagram listening...?");
            int index = 1;
            for (; ; ) {
                DatagramPacket packet = new DatagramPacket(new byte[maxBufferSize], maxBufferSize);
                serverSocket.receive(packet);

                byte[] data = packet.getData();
                maxBufferSize = packet.getLength();
                System.out.println("Decoded byte array: " + new String(Helpers.xorDecode(data, Helpers.key)));

                DatagramPacket responsePacket = new DatagramPacket(
                        data, data.length, packet.getAddress(), packet.getPort());
                serverSocket.send(responsePacket);
                System.out.println("Sent response to client. " + index);
                index++;
//                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e);
            throw new RuntimeException(e);
        }
    }
}
