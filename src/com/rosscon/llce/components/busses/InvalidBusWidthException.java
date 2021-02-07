package com.rosscon.llce.components.busses;

/**
 * An exception to throw when a bus indicates that an invalid bus size been instantiated.
 */
public class InvalidBusWidthException extends Exception {

    public InvalidBusWidthException (String errorMessage){
        super (errorMessage);
    }
}
