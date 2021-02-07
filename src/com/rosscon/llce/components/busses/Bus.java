package com.rosscon.llce.components.busses;

import java.util.Arrays;

/**
 * Abstract bus
 * https://en.wikipedia.org/wiki/Bus_(computing)
 * A bus is simply a data highway, in its simplest form it merely acts as a pipeline between components.
 * Devices connected to a basic bus simply receive whatever value it happens to hold
 */
public class Bus {

    /**
     * Default value used when no bus width has been provided
     */
    private final int DEFAULT_BUS_WIDTH = 16;

    /**
     *
     */
    private final String INVALID_BUS_WIDTH_INIT_MISMATCH_MSG =
            "Provided bus width does not match the initial value provided, bus width must be 8 multiplied by number of bytes";

    /**
     *
     */
    private final String INVALID_BUS_WIDTH_MSG =
            "Provided bus width is not a multiple of 8";

    /**
     *
     */
    private final String INVALID_DATA_SIZE_MSG =
            "Provided data size does not match the bus width";

    /**
     * Width of the bus in bits
     */
    private int busWidth;

    /**
     * Data currently represented on the bus
     */
    private byte[] data;


    /**
     * Instantiate a bus of a predefined bit width with an initial value
     * @param busWidth width of bus in bits
     * @param initValue initial value represented on the bus
     * @throws InvalidBusWidthException thrown when bus width does not match init data 
     */
    public Bus (int busWidth, byte[] initValue) throws InvalidBusWidthException {
        if (initValue.length * 8 != busWidth) throw new InvalidBusWidthException(INVALID_BUS_WIDTH_INIT_MISMATCH_MSG);

        this.busWidth = busWidth;
        this.data = Arrays.copyOf(data, data.length);
    }

    /**
     * Instantiate the bus for a given width with all zeros
     * @param busWidth width of bus in bits
     * @throws InvalidBusWidthException thrown when bus width is invalid              
     */
    public Bus (int busWidth) throws InvalidBusWidthException {
        if ( busWidth % 8 != 0 ) throw new InvalidBusWidthException(INVALID_BUS_WIDTH_MSG);

        this.busWidth = busWidth;
        data = new byte[(busWidth / 8)];
    }

    /**
     * Instantiate a default bus of default width
     */
    public Bus () {
        this.busWidth = DEFAULT_BUS_WIDTH;
        data = new byte[(this.busWidth / 8)];
    }

    /**
     * Used to obtain a copy of the data currently being represented on the bus
     * @return copy of the data held on the bus
     */
    private byte[] readDataFromBus() {
        return Arrays.copyOf(this.data, this.data.length);
    }

    /**
     * Sets the data currently being represented on the bus
     * @param data input data
     * @throws InvalidBusDataException
     */
    private void writeDataToBus(byte[] data) throws InvalidBusDataException {
        if ( data.length != this.data.length ) throw new InvalidBusDataException(INVALID_DATA_SIZE_MSG);        
        this.data = Arrays.copyOf(data, data.length);
    }
}
