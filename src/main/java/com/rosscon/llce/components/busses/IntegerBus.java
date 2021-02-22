package com.rosscon.llce.components.busses;

/**
 * A bus that purely uses integers to represent the va;ues held upon it
 * This Limits the bus to a width of 32bits
 */
public class IntegerBus {

    /**
     * Width of the bus in bits
     */
    private int busWidth;

    /**
     * Data currently held on the bus
     */
    private int data;

    /**
     * Masks for the bus, inverse is precomputed
     */
    private final int busMask;
    private final int inverseMask;

    /**
     * Default constructor sets the bus width to the default
     * held in BusConstants. mask used for the bus also set
     */
    public IntegerBus(){
        this.busWidth = BusConstants.DEFAULT_BUS_WIDTH;
        this.inverseMask = (0xFFFFFFFF << this.busWidth);
        this.busMask = ~ this.inverseMask;
        this.data = 0x00000000;
    }

    /**
     * Constructor with a provided bus width
     * @param busWidth Width of bus in number of bits
     * @throws InvalidBusWidthException Thrown when an invalid bus width is provided; valid when 31 >= width > 0
     */
    public IntegerBus(int busWidth) throws InvalidBusWidthException{

        if (busWidth < 1) throw new InvalidBusWidthException(BusConstants.EX_INVALID_BUS_WIDTH_TOO_SMALL_MSG);
        if (busWidth > 31) throw new InvalidBusWidthException(BusConstants.EX_INVALID_BUS_WIDTH_TOO_LARGE_MSG);

        this.busWidth = busWidth;
        this.inverseMask = (0xFFFFFFFF << this.busWidth);
        this.busMask = ~ this.inverseMask;
        this.data = 0x00000000;
    }

    /**
     * Get the bus width
     * @return width of the bus in bits
     */
    public int getBusWidth(){
        return this.busWidth;
    }

    /**
     * Writes data to the bus allowing the bus to handle masking. If the data provided
     * overflows the bus width returns true to warn
     * @param data data to write to bus
     * @return true = overflowed the mask, false = no overflow
     */
    public boolean writeDataToBusMask(int data) {
        this.data = (data & this.busMask);
        return (data & this.inverseMask) != 0;
    }

    /**
     * Writes data to bus and applies the bus mask, however throws exception
     * if the data provided populates more bits than the bus' width
     * @param data data to write to bus
     * @throws InvalidBusDataException Thrown when data written to the bus populates more bis than the bus' width
     */
    public void writeDataToBus(int data) throws InvalidBusDataException {
        this.data = (data & this.busMask);
        int tmp = (data & this.inverseMask);
        if ((data & this.inverseMask) != 0) {
            throw new InvalidBusDataException(BusConstants.EX_INVALID_DATA_SIZE_MSG);
        }
    }

    /**
     * Reads the data on the bus
     * @return Masked copy of the data currently on the bus
     */
    public int readDataFromBus(){
        return this.data & this.busMask;
    }

}
