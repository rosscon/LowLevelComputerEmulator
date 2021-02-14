package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.utils.ByteArrayUtils;
import com.rosscon.llce.utils.ByteArrayWrapper;

import java.util.Arrays;

/**
 * Emulate behaviour of random access memory
 */
public class RandomAccessMemory extends Memory {


    /**
     * Constructor for Random Access Memory
     * @param addressBus Address Bus
     * @param dataBus Data Bus
     * @param rwFlag Read Write Flag
     * @param startAddress First address of memory
     * @param endAddress Last address of memory
     */
    public RandomAccessMemory(Bus addressBus, Bus dataBus, Flag rwFlag, byte[] startAddress, byte[] endAddress){
        super(addressBus, dataBus, rwFlag);

        this.start = ByteArrayUtils.byteArrayToLong(startAddress);
        long end = ByteArrayUtils.byteArrayToLong(endAddress);
        int size = (int) ((end - start) + 1);
        int dataByteWidth = dataBus.readDataFromBus().length;
        this.contentsArr = new byte[size][dataByteWidth];
    }


    /**
     *
     * @param newValue new flag value
     * @param flag flag that fired the event
     * @throws MemoryException can throw a memory exception if there are issues with the busses
     */
    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {

        if (flag == rwFlag) {

            long address = ByteArrayUtils.byteArrayToLong(this.addressBus.readDataFromBus());

            if (address >= this.start && address < this.start + contentsArr.length){
                address -= start;

                if (newValue) {
                    try {
                        this.dataBus.writeDataToBus(this.contentsArr[(int) address]);
                    } catch (InvalidBusDataException ex) {
                        throw new MemoryException(ex.getMessage());
                    }
                } else {
                    this.contentsArr[(int)address] = this.dataBus.readDataFromBus();
                }
            }

        }

    }
}
