package com.rosscon.llce.components.busses;

/**
 * An exception to be thrown for invalid data
 */
public class InvalidBusDataException extends Exception {

    public InvalidBusDataException (String errorMessage){
        super (errorMessage);
    }
}
