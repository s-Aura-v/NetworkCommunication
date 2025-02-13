package org.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    static final int PORT = 26880;
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
//            DataOutputStream
            for (;;) {
                Socket client = serverSocket.accept();

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(client.getInputStream()));

                String cmd = in.readLine();
                System.out.println(cmd);

                byte[] msg = cmd.getBytes();
                byte[] encodedMsg = TCP.xorEncode(msg, TCP.key);
                out.println(Arrays.toString(encodedMsg));

                out.close();
                in.close();
                client.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
