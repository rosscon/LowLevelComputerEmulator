package com.rosscon.llce.components.flags;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a flag between components, has two roles, to wrap a boolean and to notify watchers of a change to the value
 */
public abstract class Flag {

    /**
     * List of listeners
     */
    protected List<FlagListener> listeners = new ArrayList<>();

    /**
     * Value held by the flag
     */
    protected int flagValue;


    public Flag(){
        this.flagValue = -1;
    }

    /**
     * Allows for flag to be created with a default state
     * @param flagValue default flag value
     */
    public Flag(int flagValue){
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
     * Gets the value of the flag
     * @return flag value
     */
    public int getFlagValue(){
        return this.flagValue;
    }


    /**
     * Sets the flags value and notifies all the listeners of the change
     * @param flagValue new flag value
     */
    public void setFlagValue(int flagValue) throws FlagException {

        this.flagValue = flagValue;

        for (FlagListener fl : listeners){
            try {
                fl.onFlagChange(this);
            } catch (Exception ex){
                FlagException fx = new FlagException(ex.getMessage());
                fx.addSuppressed(ex);
                throw fx;
            }
        }
    }
}
