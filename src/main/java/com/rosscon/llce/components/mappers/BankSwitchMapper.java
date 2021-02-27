package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.memory.Memory;

/**
 * A mapper that can handle bank switching between multiple ROMs
 */
public class BankSwitchMapper extends Mapper {

    /**
     * Memory Banks to switch between
     */
    private Memory[] banks;

    public BankSwitchMapper(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, Memory[] banks) {
        super(addressBus, dataBus, rwFlag);
        this.banks = banks;
        //TODO establish a new bus between banks and this mapper
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {

    }
}
