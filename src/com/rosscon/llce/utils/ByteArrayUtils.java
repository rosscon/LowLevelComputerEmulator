package com.rosscon.llce.utils;

import java.util.Arrays;
import java.util.List;

/**
 * A series of utils for manipulating byte arrays
 */
public class ByteArrayUtils {

    /**
     * Increments the value of the byte array by 1
     * @param input input array
     * @param index index of LSB
     * @return byte array incremented by 1
     */
    public static byte[] increment (byte[] input, int index) {

        if (input[index] == Byte.MAX_VALUE) {
            input[index] = 0;
            if (index > 0){
                increment(input, index - 1);
            }
         } else {
            input[index] ++;
        }

        return input;
    }

    /**
     * Increments the value of the byte array by 1
     * @param input input array
     * @return byte array incremented by 1
     */
    public static byte[] increment (byte[] input) {
        return increment(Arrays.copyOf(input, input.length), input.length - 1);
    }


    /**
     * Java does not provide a toArray(byte) function so we must create one
     * @param input input list
     * @return byte[]
     */
    public static byte[] listToArray (List<Byte> input){
        byte[] output = new byte[input.size()];

        for (int i = 0; i < input.size(); i++){
            output[i] = input.get(i).byteValue();
        }

        return output;
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
