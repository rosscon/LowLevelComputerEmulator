package com.rosscon.llce.components.processors.NMOS6502;

/**
 * Class holder of details for an instruction
 */
public class NMOS6502InstructionDetails {

    /**
     * Addressing Mode
     */
    public NMOS6502AddressingMode addressingMode;

    /**
     * Instruction Size
     */
    public int instructionSize;

    /**
     * Instruction Cycles
     */
    public int cycles;

    public NMOS6502InstructionDetails(NMOS6502AddressingMode addressingMode,
                                      int instructionSize,
                                      int cycles ){
        this.addressingMode = addressingMode;
        this.instructionSize = instructionSize;
        this.cycles = cycles;
    }
}
