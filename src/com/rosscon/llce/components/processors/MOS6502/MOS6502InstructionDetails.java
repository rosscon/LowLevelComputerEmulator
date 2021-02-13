package com.rosscon.llce.components.processors.MOS6502;

/**
 * Class holder of details for an instruction
 */
public class MOS6502InstructionDetails {

    /**
     * Addressing Mode
     */
    public MOS6502AddressingMode addressingMode;

    /**
     * Instruction Size
     */
    public int instructionSize;

    /**
     * Instruction Cycles
     */
    public int cycles;

    public MOS6502InstructionDetails(MOS6502AddressingMode addressingMode,
                                     int instructionSize,
                                     int cycles ){
        this.addressingMode = addressingMode;
        this.instructionSize = instructionSize;
        this.cycles = cycles;
    }
}
