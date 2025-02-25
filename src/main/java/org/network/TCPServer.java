package org.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Receives data from client and decodes it, before sending it back to client.
 */
public class TCPServer {
    static final int PORT = 26880;
    static int numHits = 0;

    public static void main(String[] args) {
        System.out.println("Waiting for Connection");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            for (; ; ) {
                Socket client = serverSocket.accept();
                System.out.println("Server Connected");
                client.setSoTimeout(30000);

                try (DataOutputStream out = new DataOutputStream(client.getOutputStream());
                     DataInputStream in = new DataInputStream(client.getInputStream())) {

                    for (; ; ) {
                        try {
                            int length = in.readInt();

                            byte[] byteArray = new byte[length];
                            in.readFully(byteArray);

                            String message = new String(Helpers.xorEncode(byteArray, Helpers.key));
                            System.out.println("Decoded byte array: " + message);
                            if (message.substring(message.length() - 8).equals(Client.agreement)) {
                                numHits++;
                            }

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
                    out.close();
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                } finally {
                    System.out.println("8-bit key verification hits: " + numHits);
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