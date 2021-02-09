package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;

/**
 * Emulate behaviour of random access memory
 */
public class RandomAccessMemory extends Memory {

    /**
     * Default instantiation of memory
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param clock clock to listen for ticks
     */
    public RandomAccessMemory(Bus addressBus, Bus dataBus, Clock clock) {
        super(addressBus, dataBus, clock);
    }

    @Override
    public void onTick() {
        //TODO read and write to memory and data bus. May need to implement some RW flags.
    }
}
