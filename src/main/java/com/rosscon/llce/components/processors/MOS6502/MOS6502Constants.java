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

    public static final String EX_ERROR_READING_MEMORY =
            "Unable to read from memory";

    public static final String EX_ERROR_WRITING_MEMORY =
            "Unable to wrtie to memory";

    public static final String EX_ERROR_LISTENING_TO_FLAG =
            "Unable to register as listener with interrupt flag";

    /**
     * Vectors / Pages
     */
    public static final byte STACK_PAGE       = (byte)0x01;
    public static final int  VECTOR_NMI       = 0xFFFA;
    public static final int  VECTOR_RESET     = 0xFFFC;
    public static final int  VECTOR_IRQ_BRK   = 0xFFFE;


    /**
     * Masks
     */
    public static final int MASK_LAST_BYTE  = 0x000000FF;
    public static final int MASK_NEGATIVE   = 0b10000000;
    public static final int MASK_OVERFLOWED = 0xFFFFFF00;

}
