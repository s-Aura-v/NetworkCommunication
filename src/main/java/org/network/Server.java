package org.network;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    static final int PORT = 26880;

    public static void main(String[] args) {
        System.out.println("Waiting for Connection");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            for (; ; ) {
                Socket client = serverSocket.accept();
                System.out.println("Server Connected");


                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                DataInputStream in = new DataInputStream(client.getInputStream());

                for (int i = 0; i < 3; i++) {
                    int length = in.readInt();
                    byte[] byteArray = new byte[length];
                    in.readFully(byteArray);
                    out.writeInt(byteArray.length);
                    out.write(byteArray);
                    System.out.println(Arrays.toString(byteArray));
                }





//                System.out.println(cmd);
//
//                byte[] msg = cmd.getBytes();
//                byte[] encodedMsg = TCP.xorEncode(msg, TCP.key);
//                out.write(encodedMsg);

//                out.close();
                in.close();
                client.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
