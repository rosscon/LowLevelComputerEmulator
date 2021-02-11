package com.rosscon.llce.components.clocks;

import com.rosscon.llce.components.processors.ProcessorException;

public interface ClockListener {
    void onTick() throws ProcessorException;
}
