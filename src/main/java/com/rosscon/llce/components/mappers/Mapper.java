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
    protected Bus addressBus;

    /**
     * Data Bus
     */
    protected Bus dataBus;

    /**
     * Read Write Flag
     */
    protected Flag rwFlag;

    /**
     * First address mapper can respond to
     */
    protected long start;

    /**
     * Last address mapper can respond to
     */
    protected long end;

    public Mapper(Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
        rwFlag.addListener(this);
    }
}
