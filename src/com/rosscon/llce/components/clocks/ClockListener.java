package com.rosscon.llce.components.clocks;

import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.memory.MemoryException;

public interface ClockListener {
    void onTick() throws InvalidBusDataException, MemoryException;
}
