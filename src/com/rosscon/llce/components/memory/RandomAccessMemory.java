package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;

/**
 * Emulate behaviour of random access memory
 */
public class RandomAccessMemory extends Memory {

    /**
     * Default instantiation of memory
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag flag to indicate read or write
     */
    public RandomAccessMemory(Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(addressBus, dataBus, rwFlag);
    }


    /**
     *
     * @param newValue new flag value
     * @param flag flag that fired the event
     * @throws MemoryException can throw a memory exception if there are issues with the busses
     */
    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {

    }
}
