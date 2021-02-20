package com.rosscon.llce.utils;

import java.util.Arrays;

/**
 * Wrapper for byte[] so we can have a custom hashing function
 */
public final class ByteArrayWrapper {

    /**
     * byte[] being wrapped
     */
    private final byte[] data;

    public ByteArrayWrapper(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteArrayWrapper)) {
            return false;
        }
        return Arrays.equals(data, ((ByteArrayWrapper)other).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public int getLength(){
        return data.length;
    }

    public byte[] getData(){
        return data;
    }
}
