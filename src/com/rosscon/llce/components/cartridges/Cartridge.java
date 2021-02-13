package com.rosscon.llce.components.cartridges;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;

/**
 * A generic Cartridge class
 */
public abstract class Cartridge implements FlagListener {

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
    Flag rwFlag;

    public Cartridge(Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
    }

}
