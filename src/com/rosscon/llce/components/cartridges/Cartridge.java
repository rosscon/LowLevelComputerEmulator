package com.rosscon.llce.components.cartridges;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;

/**
 * A generic Cartridge class
 */
public abstract class Cartridge {

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

    public Cartridge(Bus addressBus, Bus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
    }

}
