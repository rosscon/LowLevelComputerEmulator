package com.rosscon.llce.components.processors.MOS6502;

/**
 * Constants for the 6502
 */
public class MOS6502Constants {

    /**
     * Error Messages
     */
    public static final String EX_INVALID_INSTRUCTION =
            "Invalid or unknown instruction";

    public static final String EX_TICK_FETCH_ERROR =
            "Error fetching instruction";

    public static final String EX_RESET_ERROR =
            "Unable to perform reset";

    public static final String EX_STACK_PUSH_ERROR =
            "Unable to push to stack";

    /**
     * Vectors / Pages
     */
    public static final byte STACK_PAGE       = (byte)0x01;
    public static final int  VECTOR_NMI       = 0xFFFB;
    public static final int  VECTOR_RESET     = 0xFFFC;
    public static final int  VECTOR_IRQ_BRK   = 0xFFFE;


    /**
     * Masks
     */
    public static final int MASK_LAST_BYTE  = 0x000000FF;
    public static final int MASK_NEGATIVE   = 0x00000080;
    public static final int MASK_OVERFLOWED = 0xFFFFFF00;

}
