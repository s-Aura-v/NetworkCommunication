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
     * For UDP, used to store how many times to send a X byte packet.
     */
    public static int numberOfMessages = 0;

    /**
     * The message that will be divided into bytes
     */
    public static String msg = "TESTDEMONOTUSED";

    /**
     * The amount of times the message should be sent.
     * 30 is the default value in case of errors, but it is never used.
     * The value depends on the Scanner input in Client.
     */
    public static int iterations = 30;

    /**
     * Test 1: Measure round-trip latency (RTTs) and how it varies with message size in TCP, by sending and receiving (echoing and validating) messages of size 8, 64, 256, and 512 bytes.
     * Test 2: Measure throughput (bits per second) and how it varies with message size in TCP, by sending 1MByte of data (with a 8-byte acknowledgment in the reverse direction) using different numbers of messages: 1024 1024Byte messages, vs 2048 512Byte messages, vs 4096 X 256Byte messages.
     * Test 3: The same as (1), except using UDP.
     * Test 4: The same as (2), using UDP..
     */
    public static short test = 0;

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

