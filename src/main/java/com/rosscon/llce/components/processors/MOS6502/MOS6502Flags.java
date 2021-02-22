package com.rosscon.llce.components.processors.MOS6502;

public class MOS6502Flags {

    /**
     * CPU Flags
     */
    public static final int NEGATIVE_FLAG  = 0b10000000;    // Negative Flag
    public static final int OVERFLOW_FLAG  = 0b01000000;    // Overflow Flag
    public static final int IGNORED_FLAG   = 0b00100000;    // Ignored Flag (Ironically isn't ignored)
    public static final int BREAK_COMMAND  = 0b00010000;    // Break Command
    public static final int DECIMAL_MODE   = 0b00001000;    // Decimal Mode Flag
    public static final int INTERRUPT_DIS  = 0b00000100;    // Interrupt Disable
    public static final int ZERO_FLAG      = 0b00000010;    // Zero Flag
    public static final int CARRY_FLAG     = 0b00000001;    // Carry Flag
}
