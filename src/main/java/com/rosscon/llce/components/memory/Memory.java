package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;


/**
 * Memory class t handle the core functions of any type of memory
 */
public abstract class Memory implements FlagListener {

    /**
     * Address bus
     */
    protected Bus addressBus;

    /**
     * Data bus
     */
    protected Bus dataBus;

    /**
     * Contents of the memory as an array
     */
    protected byte[][] contentsArr;

    /**
     * First address of memory
     */
    protected long start;

    /**
     * Last address of memory
     */
    protected long end;

    /**
     * R/W flag, high = read, low = write
     */
    protected Flag rwFlag;


    /**
     * Default constructor for all memory kinds
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag rwFlag to listen for events
     */
    public Memory (Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;

        rwFlag.addListener(this::onFlagChange);
    }

    public Bus getAddressBus(){
        return this.addressBus;
    }

    public Bus getDataBus(){
        return this.dataBus;
    }

    public Flag getRwFlag() {
        return this.rwFlag;
    }
}

