package com.rosscon.llce.components.busses;

public class BusConstants {

    /**
     * Constants
     */
    public static final int DEFAULT_BUS_WIDTH = 16;


    /**
     * Error Messages
     */
    public static final String EX_INVALID_BUS_WIDTH_TOO_LARGE_MSG =
            "Provided bus width is too large";

    public static final String EX_INVALID_BUS_WIDTH_TOO_SMALL_MSG =
            "Provided bus width is too small, minimum bus width is 1";

    public static final String EX_INVALID_DATA_SIZE_MSG =
            "Data written to the bus is larger than the bus width";

}
