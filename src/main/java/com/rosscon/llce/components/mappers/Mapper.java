package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagListener;

public abstract class Mapper implements FlagListener {

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
    protected RWFlag rwFlag;

    /**
     * First address mapper can respond to
     */
    protected int firstAddress;

    /**
     * Last address mapper can respond to
     */
    protected long lastAddress;

    public Mapper(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
        rwFlag.addListener(this);
    }
}
