package com.rosscon.llce.components.cartridges;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagListener;

/**
 * A generic Cartridge class
 */
public abstract class Cartridge implements FlagListener {

    /**
     * Address Bus
     */
    protected IntegerBus addressBus;

    /**
     * Data Bus
     */
    protected IntegerBus dataBus;

    /**
     * Read Write Flag
     */
    RWFlag rwFlag;

    public Cartridge(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
    }

}
