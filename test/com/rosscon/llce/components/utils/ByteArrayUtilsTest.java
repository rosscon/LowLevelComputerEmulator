package com.rosscon.llce.components.utils;

import com.rosscon.llce.utils.ByteArrayUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteArrayUtilsTest {

    @Test
    @DisplayName("Byte Array Utils should increment value in byte array")
    public void TestIncrementByteArrayWithCarry() {

        byte[] test = new byte[]{ 0x00, (byte) 0xFF};
        byte[] incremented = ByteArrayUtils.increment(test);
        assertArrayEquals(new byte[]{ 0x01, (byte) 0x00}, incremented);

        test = new byte[]{ 0x00, (byte) 0xFE};
        incremented = ByteArrayUtils.increment(test);
        assertArrayEquals(new byte[]{ 0x00, (byte) 0xFF}, incremented);
    }

    @Test
    @DisplayName("Byte Array Utils should increment value in byte array")
    public void TestIncrementByteArrayOverALargeRange() {

        byte[] test = new byte[]{ 0x00, 0x00, (byte) 0xFF};

        for (int i = 0; i < 32768; i++){
            BigInteger previous = new BigInteger(test);
            test = ByteArrayUtils.increment(test);
            BigInteger next = new BigInteger(test);

            int difference = next.intValue() - previous.intValue();
            assertEquals(1, difference);
        }
    }
}
