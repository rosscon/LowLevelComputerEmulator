package com.rosscon.llce.components.clocks.dividers;

import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.clocks.ClockListener;
import com.rosscon.llce.components.processors.ProcessorException;

/**
 * Basic clock divider, to any observer is a clock
 */
public class Divider extends Clock implements ClockListener {

    /**
     *
     */
    private int waitCycles;

    /**
     *
     */
    private int currentCycle;


    public Divider (int waitCycles, Clock clock){
        this.waitCycles = waitCycles;
        this.currentCycle = 0;
        clock.addListener(this);
    }

    @Override
    public void onTick() throws ProcessorException {

        this.currentCycle ++;

        if (currentCycle >= waitCycles){
            currentCycle = 0;
            try {
                this.tick();
            } catch (ClockException e) {
                e.printStackTrace();
            }
        }

    }
}
