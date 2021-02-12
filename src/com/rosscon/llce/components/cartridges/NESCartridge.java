package com.rosscon.llce.components.cartridges;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;

public class NESCartridge extends Cartridge{

    /**
     *
     */
    private ReadOnlyMemory[] romBanks;

    public NESCartridge(Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(addressBus, dataBus, rwFlag);
    }
}
