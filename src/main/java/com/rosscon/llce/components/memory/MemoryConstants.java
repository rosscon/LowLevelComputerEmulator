package com.rosscon.llce.components.memory;

/**
 * Constants for memory
 */
public class MemoryConstants {

    /**
     * Error Messages
     */
    public static final String EX_INVALID_START_ADDRESS =
            "Provided start address is invalid";

    public static final String EX_INVALID_END_ADDRESS =
            "Provided last address is invalid";

    public static final String EX_LAST_ADDRESS_BEFORE_START =
            "Provided last address cannot be lower than start address";

    public static final String EX_NULL_ADDRESS_BUS =
            "Address bus cannot be null";

    public static final String EX_NULL_DATA_BUS =
            "Data bus cannot be null";

    public static final String EX_NULL_RW_FLAG =
            "RW flag annot be null";
}
