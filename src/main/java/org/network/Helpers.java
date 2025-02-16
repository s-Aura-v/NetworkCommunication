package org.network;

public class Helpers {
    /**
     * Used as a key for XOR shifting.
     * Keys temporarily public.
     */
    public static long key = 214839961L;

    /**
     * The byte size of the message sent per packet
     * [8, 64, 256, 512] are the sizes we are looking at.
     * [8] is default.
     */
    public static int msgSize = 8;

    /**
     * The message that will be divided into bytes
     */
    public static String msg = "initialsinitials notintitals";

    /**
     * The amount of times the message should be sent.
     */
    public static int iterations = 3;


    public static void main(String[] args) {
        // Shared none zero initial key

        String message = "helloasd";
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

