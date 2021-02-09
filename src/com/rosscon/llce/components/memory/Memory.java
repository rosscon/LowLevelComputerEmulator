package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockListener;


/**
 * Memory class t handle the core functions of any type of memory
 */
public abstract class Memory implements ClockListener {

    /**
     * Address bus
     */
    protected Bus addressBus;

    /**
     * Data bus
     */
    protected Bus dataBus;


    public Memory (Bus addressBus, Bus dataBus, Clock clock){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        clock.addListener(this::onTick);
    }
}

