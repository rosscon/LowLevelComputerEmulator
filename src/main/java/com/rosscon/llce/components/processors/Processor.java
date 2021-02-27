package com.rosscon.llce.components.processors;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockListener;
import com.rosscon.llce.components.flags.RWFlag;

public abstract class Processor implements ClockListener  {

    /**
     * Clock
     */
    protected Clock clock;

    /**
     * Address bus
     */
    protected IntegerBus addressBus;

    /**
     * Data bus
     */
    protected IntegerBus dataBus;


    /**
     * R/W flag
     */
    protected RWFlag flgRW;

    /**
     * Default constructor connects the minimum requirements to communicate with busses
     * @param clock Clock
     * @param addressBus Address bus to attach to
     * @param dataBus Data bus to attach to
     * @param flgRW R/W Flag to attach to
     */
    public Processor(Clock clock, IntegerBus addressBus, IntegerBus dataBus, RWFlag flgRW){
        this.clock = clock;
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.flgRW = flgRW;

        this.clock.addListener(this);
    }
}
