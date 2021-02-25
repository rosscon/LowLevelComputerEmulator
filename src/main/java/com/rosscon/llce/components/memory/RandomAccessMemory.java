package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagValueRW;

public class RandomAccessMemory extends Memory {

    /**
     * Default constructor creates Random Access Memory with the maximum possible range
     * that can be addressed by the attached address bus starting from address 0x00000000
     * @param addressBus Address Bus to attach to
     * @param dataBus Data bus to attach to
     * @param rwFlag RW Flag to attach to
     * @throws MemoryException Thrown when any bus of flag is null
     */
    public RandomAccessMemory(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag) throws MemoryException {
        super(addressBus, dataBus, rwFlag);
    }

    /**
     * Creates Random Access Memory with a start and end address
     * @param addressBus Address Bus to attach to
     * @param dataBus Data bus to attach to
     * @param rwFlag RW Flag to attach to
     * @param startAddress Address of the first value in memory
     * @param lastAddress Address of the last value in memory
     * @throws MemoryException Thrown when any bus of flag is null or an invalid address range provided
     */
    public RandomAccessMemory(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag, int startAddress, int lastAddress) throws MemoryException {
        super(addressBus, dataBus, rwFlag, startAddress, lastAddress);
    }

    /**
     * Random Access Memory can be read from and written to by setting the RW flag
     * @param newValue new value of RW flag
     * @param flag flag that fired event
     * @throws MemoryException thrown when error reading memory
     * @throws InvalidBusDataException thrown by bus
     */
    @Override
    public void onFlagChange(FlagValueRW newValue, Flag flag) throws MemoryException, InvalidBusDataException {
        if (flag == rwFlag) {
            int address = this.addressBus.readDataFromBus();
            if (addressIsInRange(address)){
                if (newValue == FlagValueRW.READ){
                    this.dataBus.writeDataToBus(readValueFromAddress(address));
                } else {
                    writeValueToAddress(address, this.dataBus.readDataFromBus());
                }
            }
        }
    }
}
