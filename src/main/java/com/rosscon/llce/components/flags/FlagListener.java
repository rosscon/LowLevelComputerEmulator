package com.rosscon.llce.components.flags;

import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.mappers.MapperException;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.ProcessorException;

public interface FlagListener {
    void onFlagChange(FlagValueRW newValue, Flag flag) throws MemoryException, InvalidBusDataException, MapperException, ProcessorException;
}
