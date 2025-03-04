package org.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.network.Helpers.checkLast8Bytes;

/**
 * Receives data from client and decodes it, before sending it back to client.
 */
public class TCPServer2 {
    static final int PORT = 26880;

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
//                            String msg = new String(byteArray);
                            if (checkLast8Bytes(byteArray, Client.agreementBytes)) {
                                out.writeInt(byteArray.length);
                                out.write(byteArray);
                            }

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