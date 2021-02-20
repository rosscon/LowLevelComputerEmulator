package com.rosscon.llce.components.processors.MOS6502;

/**
 * Addressing Mode
 * http://www.obelisk.me.uk/6502/addressing.html#IMP
 */
public enum MOS6502AddressingMode {
    IMPLICIT, ACCUMULATOR, IMMEDIATE, ZERO_PAGE, ZERO_PAGE_X,
    ZERO_PAGE_Y, RELATIVE, ABSOLUTE, ABSOLUTE_X , ABSOLUTE_Y,
    INDIRECT, INDEXED_INDIRECT_X, INDIRECT_INDEXED_Y
}
