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

        // Pre populate RAM with all 0's
        byte[] current = Arrays.copyOf(startAddress, startAddress.length);
        while (! Arrays.equals(current, endAddress)){
            ByteArrayWrapper wrappedKey = new ByteArrayWrapper(current);
            this.contents.put(wrappedKey, new byte[dataBus.readDataFromBus().length]);
            current = ByteArrayUtils.increment(current);
        }
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
            byte[] key = this.addressBus.readDataFromBus();
            ByteArrayWrapper wrappedKey = new ByteArrayWrapper(key);

            // Check if in our memory address range
            if(this.contents.containsKey(wrappedKey)){

                //Read (Write a value to data bus)
                if (newValue){
                    try {
                        this.dataBus.writeDataToBus(contents.get(wrappedKey));
                    } catch (InvalidBusDataException ex) {
                        throw new MemoryException(ex.getMessage());
                    }
                }
                //Write (Read value from data bus and write to contents)
                else {
                    this.contents.put(wrappedKey, this.dataBus.readDataFromBus());
                }
            }

        }

    }
}
