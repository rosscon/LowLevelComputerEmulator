package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockListener;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;
import com.rosscon.llce.utils.ByteArrayWrapper;

import java.util.HashMap;
import java.util.Map;


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
     * Contents of the memory <address, data>
     */
    protected Map<ByteArrayWrapper, byte[]> contents = new HashMap<>();

    /**
     * R/W flag, high = read, low = write
     */
    protected Flag rwFlag;


    public Memory (Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;

        rwFlag.addListener(this::onFlagChange);
    }
}

