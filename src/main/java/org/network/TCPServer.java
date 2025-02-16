package org.network;

import java.io.*;
import java.net.*;

public class TCPServer {
    static final int PORT = 26880;

    public static void main(String[] args) {
        System.out.println("Waiting for Connection");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            for (; ; ) {
                Socket client = serverSocket.accept();
                System.out.println("Server Connected");
                client.setSoTimeout(30000); // Set a 30-second timeout

                try (DataOutputStream out = new DataOutputStream(client.getOutputStream());
                     DataInputStream in = new DataInputStream(client.getInputStream())) {

                    for (; ; ) {
                        try {
                            int length = in.readInt();

                            byte[] byteArray = new byte[length];
                            in.readFully(byteArray);

                            System.out.println("Decoded byte array: " + new String(Helpers.xorDecode(byteArray, Helpers.key)));

                            out.writeInt(byteArray.length);
                            out.write(byteArray);
                            out.flush();


                        } catch (EOFException e) {
                            System.out.println("Client disconnected.");
                            break;
                        } catch (IOException e) {
                            System.err.println("Error reading from client: " + e.getMessage());
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        System.err.println("Error closing client socket: " + e.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}