package com.rosscon.llce.components.clocks;

import com.rosscon.llce.components.busses.InvalidBusDataException;

import java.util.ArrayList;
import java.util.List;

/**
 * A very basic clock class with event listeners
 */
public class Clock {

    private List<ClockListener> listeners = new ArrayList<ClockListener>();

    /**
     * Add a listener to this clock
     * @param toAdd Listener to add
     */
    public void addListener(ClockListener toAdd){
        listeners.add(toAdd);
    }

    /**
     * On tick notify each listening device that a tick has occurred
     * @throws ClockException
     */
    public void tick() throws ClockException {
        for (ClockListener cl : listeners){
            try {
                cl.onTick();
            } catch (Exception ex){
                throw new ClockException(ex.getMessage());
            }
        }
    }

}

