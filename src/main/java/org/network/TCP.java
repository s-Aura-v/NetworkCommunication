package org.network;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TCP {
    byte[] sendBytes(int size) {
        return new byte[size];
    }

    public static void main(String[] args) {
        // Shared none zero initial key
        long key = 123456789L;

        String message = "Hello, World! 912 42 18 2 542 1 z Z . 🤷‍♀️";
        byte[] messageBytes = message.getBytes();

        // Encrypt the message
        byte[] ciphertext = xorEncode(messageBytes, key);
        System.out.println("Ciphertext: " + new String(ciphertext));
        byte[] decryptedBytes = xorDecode(ciphertext, key);

        // Update the key for the next message
        key = xorShift(key);

        // Decrypt the message
        String decryptedMessage = new String(decryptedBytes);
        System.out.println("Decrypted Message: " + decryptedMessage);

        System.out.println(message.getBytes().length + " " + decryptedMessage.getBytes().length + " " + ciphertext.length);
    }

    public static byte[] xorEncode(byte[] message, long key) {
        byte[] encrypted = new byte[message.length];
        for (int i = 0; i < message.length; i++) {
            encrypted[i] = (byte) (message[i] ^ ((key >> (8 * (i % 8))) & 0xFF));
        }
        return encrypted;
    }

    // Method is a bit redundant, but I like having nicer method names
    public static byte[] xorDecode(byte[] ciphertext, long key) {
        return xorEncode(ciphertext, key);
    }

    static long xorShift(long r) {
        r ^= r << 13;
        r ^= r >>> 7;
        r ^= r << 17;
        return r;
    }
}

