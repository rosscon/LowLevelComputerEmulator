package com.rosscon.llce.components.flags;


import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a flag between components, has two roles, to wrap a boolean and to notify watchers of a change to the value
 */
public class Flag {

    /**
     * Value of flag true = (on, high, yes), false = (off, low, no)
     */
    private boolean flagValue;

    /**
     * List of listeners
     */
    private List<FlagListener> listeners = new ArrayList<>();

    /**
     * Default to holding false as value
     */
    public Flag(){
        flagValue = false;
    }

    /**
     * Allows for flag to be created with a default state
     * @param flagValue default flag value
     */
    public Flag(boolean flagValue){
        this.flagValue = flagValue;
    }

    /**
     * Allows a listener to request to be notified on flag change
     * @param toAdd Lister to be notified of flag changes
     */
    public void addListener(FlagListener toAdd){
        listeners.add(toAdd);
    }

    /**
     * Sets the flag value
     * @param flagValue value to set flag to
     */
    public void setFlagValue (boolean flagValue){
        this.flagValue = flagValue;

        for (FlagListener fl : listeners){
            fl.onFlagChange(flagValue == true);
        }
    }

    /**
     * Get the current value of the flag
     * @return the current value of the flag
     */
    public boolean getFlagValue (){
        return flagValue == true;
    }

}
