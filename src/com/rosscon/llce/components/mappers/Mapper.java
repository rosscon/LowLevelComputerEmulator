package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;

/**
 * Generic mapper class
 */
public abstract class Mapper implements FlagListener {

    /**
     * Address Bus
     */
    Bus addressBus;

    /**
     * Data Bus
     */
    Bus dataBus;

    /**
     * Read Write Flag
     */
    Flag rwFlag;

    public Mapper(Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
    }
}
