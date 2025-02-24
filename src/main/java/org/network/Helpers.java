package org.network;

import java.util.ArrayList;

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

    public static int numberOfMessages = 0;

    /**
     * The message that will be divided into bytes
     */
    public static String msg = "initialsinitials notintitals";

    /**
     * The amount of times the message should be sent.
     * 30 is the default value in case of errors, but it is never used.
     * The value depends on the Scanner input in Client.
     */
    public static int iterations = 30;

    public static short test = 0;

    /**
     * Data gathered
     */
    private ArrayList<Double> tcpTime = new ArrayList<>();
    private ArrayList<Double> udpTime = new ArrayList<>();

    /**
     * Encrypted the message using a XOR sequence.
     * @param message - the input to be encrypted
     * @param key - the seed that determines how it is encrypted
     * @return encrypted byte[] - the input, encrypted.
     */
    public static byte[] xorEncode(byte[] message, long key) {
        byte[] encrypted = new byte[message.length];
        for (int i = 0; i < message.length; i++) {
            encrypted[i] = (byte) (message[i] ^ ((key >> (8 * (i % 8))) & 0xFF));
        }
        return encrypted;
    }

    /**
     * Changes the initial key for encoding and decoding purposes
     * @param r - initial key
     * @return r - new key
     */
    static long xorShift(long r) {
        r ^= r << 13;
        r ^= r >>> 7;
        r ^= r << 17;
        return r;
    }
}

