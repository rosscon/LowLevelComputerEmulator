package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;

/**
 * Memory that works purely with integers for the bus and data being held
 */
public abstract class Memory implements FlagListener {

    /**
     * Busses
     */
    protected IntegerBus addressBus;
    protected IntegerBus dataBus;

    /**
     * Contents of memory
     */
    protected int[] contents;

    /**
     * Start and End Addresses
     */
    protected int startAddress;
    protected int lastAddress;

    /**
     * R/W Flag high = read, low = write
     */
    protected Flag rwFlag;

    /**
     * Default constructor creates memory with the maximum possible range
     * that can be addressed by the attached address bus starting from address 0x00000000
     * @param addressBus Address Bus to attach to
     * @param dataBus Data bus to attach to
     * @param rwFlag RW Flag to attach to
     * @throws MemoryException Thrown when any bus of flag is null
     */
    public Memory(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag) throws MemoryException {

        if (addressBus == null) throw new MemoryException(MemoryConstants.EX_NULL_ADDRESS_BUS);
        if (dataBus == null) throw new MemoryException(MemoryConstants.EX_NULL_DATA_BUS);
        if (rwFlag == null) throw new MemoryException(MemoryConstants.EX_NULL_RW_FLAG);

        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;


        startAddress = 0;
        lastAddress = 0xFFFFFFFF >>> (32 - addressBus.getBusWidth());

        this.contents = new int[lastAddress];
        this.rwFlag.addListener(this);
    }

    /**
     * Creates with a start and end address
     * @param addressBus Address Bus to attach to
     * @param dataBus Data bus to attach to
     * @param rwFlag RW Flag to attach to
     * @param startAddress Address of the first value in memory
     * @param lastAddress Address of the last value in memory
     * @throws MemoryException Thrown when any bus of flag is null or an invalid address range provided
     */
    public Memory(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag, int startAddress, int lastAddress) throws MemoryException {

        if (addressBus == null) throw new MemoryException(MemoryConstants.EX_NULL_ADDRESS_BUS);
        if (dataBus == null) throw new MemoryException(MemoryConstants.EX_NULL_DATA_BUS);
        if (rwFlag == null) throw new MemoryException(MemoryConstants.EX_NULL_RW_FLAG);

        if (startAddress < 0) throw new MemoryException(MemoryConstants.EX_INVALID_START_ADDRESS);
        if (lastAddress < 0) throw new MemoryException(MemoryConstants.EX_INVALID_END_ADDRESS);
        if (lastAddress <= startAddress) throw new MemoryException(MemoryConstants.EX_LAST_ADDRESS_BEFORE_START);

        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;

        this.startAddress = startAddress;
        this.lastAddress = lastAddress;

        this.contents = new int[lastAddress - startAddress + 1];
        this.rwFlag.addListener(this);
    }

    /**
     * Get the attached address bus
     * @return Address bus memory is attached to
     */
    public IntegerBus getAddressBus(){
        return this.addressBus;
    }

    /**
     * Get the attached data bus
     * @return Data bus memory is attached to
     */
    public IntegerBus getDataBus(){
        return this.dataBus;
    }

    /**
     * Get the RW flag
     * @return RW flag memory is attached to
     */
    public Flag getRwFlag(){
        return this.rwFlag;
    }

    /**
     * Determine if an address is within our address range
     * @param address address to check
     * @return true = in range, false = not in range
     */
    protected boolean addressIsInRange(int address){
        return this.startAddress <= address && this.lastAddress >= address;
    }

    /**
     * Reads the value held at an address by subtracting the start address
     * @param address address to read
     * @return data held at provided address
     */
    protected int readValueFromAddress(int address){
        return this.contents[address - startAddress];
    }

    /**
     * Writes a value to contents at an address by subtracting the start address
     * @param address address to write to
     * @param value value to write
     */
    protected void writeValueToAddress(int address, int value){
        this.contents[address - startAddress] = value;
    }
}
