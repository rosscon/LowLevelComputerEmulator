package com.rosscon.llce.components.processors.NMOS6502;

public class NMOS6502Flags {

    /**
     * CPU Flags
     */
    public static final byte NEGATIVE_FLAG  = (byte) 0b10000000;    // Negative Flag
    public static final byte OVERFLOW_FLAG  = (byte) 0b01000000;    // Overflow Flag
    // Ignored Flag
    public static final byte BREAK_COMMAND  = (byte) 0b00010000;    // Break Command
    public static final byte DECIMAL_MODE   = (byte) 0b00001000;    // Decimal Mode Flag
    public static final byte INTERRUPT_DIS  = (byte) 0b00000100;    // Interrupt Disable
    public static final byte ZERO_FLAG      = (byte) 0b00000010;    // Zero Flag
    public static final byte CARRY_FLAG     = (byte) 0b00000001;    // Carry Flag
}
