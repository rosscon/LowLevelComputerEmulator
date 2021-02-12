package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.Memory;
import com.rosscon.llce.components.memory.MemoryException;

public class MirroredMapper extends Mapper {

    /**
     * Memory to be mirrored
     */
    Memory memory;

    public MirroredMapper(Bus addressBus, Bus dataBus, Flag rwFlag, Memory memory) {
        super(addressBus, dataBus, rwFlag);
        this.memory = memory;
        //TODO establish a new bus between banks and this mapper
    }

    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {

    }
}
