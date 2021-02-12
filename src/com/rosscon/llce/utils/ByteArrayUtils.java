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

        if (ByteUtils.willCarryOnAddition(input[index], (byte) 0x01)){
            input[index] = 0x00;

            if (index > 0){
                increment(input, index - 1);
            }
        } else {
            input[index] = (byte)(input[index] + 0x01);
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

}
