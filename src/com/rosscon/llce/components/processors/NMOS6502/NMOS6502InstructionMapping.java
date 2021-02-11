package com.rosscon.llce.components.processors.NMOS6502;

import java.util.HashMap;

public class NMOS6502InstructionMapping extends HashMap {

    public NMOS6502InstructionMapping(){

        /**
         * ADC
         */
        this.put(NMOS6502Instructions.INS_ADC_IMM,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(NMOS6502Instructions.INS_ADC_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_ADC_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_ADC_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_ADC_ABX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(NMOS6502Instructions.INS_ADC_ABY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(NMOS6502Instructions.INS_ADC_INX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT, 2, 6));
        this.put(NMOS6502Instructions.INS_ADC_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED, 2, 5));

        /**
         * JMP
         */
        this.put(NMOS6502Instructions.INS_JMP_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 3));
        this.put(NMOS6502Instructions.INS_JMP_IND,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT, 3, 5));

        /**
         * LDA
         */
        this.put(NMOS6502Instructions.INS_LDA_IMM,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(NMOS6502Instructions.INS_LDA_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_LDA_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_LDA_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_LDA_ABX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(NMOS6502Instructions.INS_LDA_ABY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(NMOS6502Instructions.INS_LDA_INX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT, 2, 6));
        this.put(NMOS6502Instructions.INS_LDA_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED, 2, 5));

        /**
         * LDX
         */
        this.put(NMOS6502Instructions.INS_LDX_IMM,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(NMOS6502Instructions.INS_LDX_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_LDX_ZPY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_LDX_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_LDX_ABY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 4));

        /**
         * LDY
         */
        this.put(NMOS6502Instructions.INS_LDY_IMM,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(NMOS6502Instructions.INS_LDY_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_LDY_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_LDY_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_LDY_ABX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 4));
    }

}
