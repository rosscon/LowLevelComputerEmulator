package com.rosscon.llce.components.processors;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockListener;
import com.rosscon.llce.components.flags.Flag;

/**
 * Processor main class
 */
public abstract class Processor implements ClockListener {

    /**
     * Clock
     */
    protected Clock clock;

    /**
     * Address bus
     */
    protected Bus addressBus;

    /**
     * Data bus
     */
    protected Bus dataBus;


    /**
     * R/W flag
     */
    protected Flag rwFlag;


    public Processor (Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag){
        this.clock = clock;
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;

        this.clock.addListener(this);
    }

}
