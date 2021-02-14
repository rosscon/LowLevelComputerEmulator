package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.Memory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.utils.ByteArrayUtils;
import com.rosscon.llce.utils.ByteArrayWrapper;

import java.util.*;

/**
 * A simple mirrored mapper to mirror addresses in memory.
 * Simple uses a bit mask to determine new address.
 */
public class MirroredMapper extends Mapper {

    /**
     * Memory to be mirrored
     */
    Memory memory;

    /**
     * Mask to AND with requested address in order to address the memory.
     */
    private long mask;

    /**
     * Set of valid addresses, this is to simplify the check if an address is associated with mapper
     */
    protected Set<ByteArrayWrapper> validAddresses = new HashSet<>();


    /**
     * A mirrored mapper, Works by using a bitmask to determine the true memory address
     * @param addressBus System address bus
     * @param dataBus System data bus
     * @param rwFlag System RW flag
     * @param memory Memory to map addresses to
     * @param mapperLow Lowest address for mapper
     * @param mapperHigh Highest address for the mapper
     * @param mask Mask used to determine the mirrored address.
     */
    public MirroredMapper(Bus addressBus, Bus dataBus, Flag rwFlag, Memory memory,
                          byte[] mapperLow, byte[] mapperHigh, byte[] mask) {
        super(addressBus, dataBus, rwFlag);
        this.memory = memory;

        this.mask = ByteArrayUtils.byteArrayToLong(mask);

        this.start = ByteArrayUtils.byteArrayToLong(mapperLow);
        this.end = ByteArrayUtils.byteArrayToLong(mapperHigh);

    }

    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {

        if (flag == rwFlag) {

            long address = ByteArrayUtils.byteArrayToLong(this.addressBus.readDataFromBus());

            if (address >= this.start && address <= this.end){
                long maskedAddress = address & this.mask;

                try {
                    this.memory.getAddressBus().writeDataToBus(
                            ByteArrayUtils.longToByteArray(maskedAddress, this.addressBus.readDataFromBus().length)
                    );
                } catch (InvalidBusDataException ex) {
                    throw new MemoryException(ex.getMessage());
                }

                // If received flag is false (WRITE) transfer incoming data bus value
                if (!newValue) {
                    try {
                        this.memory.getDataBus().writeDataToBus(this.dataBus.readDataFromBus());
                    } catch (InvalidBusDataException ex) {
                        throw new MemoryException(ex.getMessage());
                    }
                }

                this.memory.getRwFlag().setFlagValue(newValue);

                if (newValue){
                    try {
                        this.dataBus.writeDataToBus(this.memory.getDataBus().readDataFromBus());
                    }
                    catch (InvalidBusDataException ex) {
                        throw new MemoryException(ex.getMessage());
                    }
                }
            }
        }

    }
}
