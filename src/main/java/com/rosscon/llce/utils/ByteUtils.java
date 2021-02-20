package com.rosscon.llce.utils;

/**
 * Some useful utils for handing bytes
 */
public class ByteUtils {

    /**
     * Converts an unsigned byte to an integer
     * @param inByte input unsigned byte
     * @return integer value of the byte as an int if it was unsigned
     */
    public static int byteToIntUnsigned (byte inByte){

        int answer = 0;

        if ((inByte & 0b00000001) == 0b00000001) answer += 1;
        if ((inByte & 0b00000010) == 0b00000010) answer += 2;
        if ((inByte & 0b00000100) == 0b00000100) answer += 4;
        if ((inByte & 0b00001000) == 0b00001000) answer += 8;
        if ((inByte & 0b00010000) == 0b00010000) answer += 16;
        if ((inByte & 0b00100000) == 0b00100000) answer += 32;
        if ((inByte & 0b01000000) == 0b01000000) answer += 64;
        if ((inByte & 0b10000000) == 0b10000000) answer += 128;

        return answer;
    }

    /**
     * Determines whether adding two bytes will result in a carry
     * @param a_in byte a
     * @param b_in byte b
     * @return true = carry, false = no carry
     */
    public static boolean willCarryOnAddition (byte a_in, byte b_in){

        // Carry
        byte c = 0x00;

        // Mask for final bit
        byte lastBit = 0b00000001;

        for (int i = 0; i < 8; i ++){

            byte a = (byte) ((a_in & 0xFF) >>> i);
            byte b = (byte) ((b_in & 0xFF) >>> i);

            /*
                Carry when;
                    a = 1, b = 1, c = 0
                    a = 1, b = 1, c = 1
                    a = 1, b = 0, c = 1
                    a = 0, b = 1, c = 1
             */
            if (
                    (((a & lastBit) == lastBit) && ((b & lastBit) == lastBit) && ((c & lastBit) == 0x00)) ||
                            (((a & lastBit) == lastBit) && ((b & lastBit) == lastBit) && ((c & lastBit) == lastBit)) ||
                            (((a & lastBit) == lastBit) && ((b & lastBit) == 0x00)    && ((c & lastBit) == lastBit)) ||
                            (((a & lastBit) == 0x00)    && ((b & lastBit) == lastBit) && ((c & lastBit) == lastBit))
            ) {
                c = 0b00000001;
            } else {
                c = 0x00;
            }
        }

        return (c & lastBit) == lastBit;
    }
}
