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
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(NMOS6502Instructions.INS_ADC_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));


        /**
         * AND
         */
        this.put(NMOS6502Instructions.INS_AND_IMM,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(NMOS6502Instructions.INS_AND_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_AND_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_AND_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_AND_ABX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(NMOS6502Instructions.INS_AND_ABY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(NMOS6502Instructions.INS_AND_INX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(NMOS6502Instructions.INS_AND_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));

        /**
         * ASL
         */
        //TODO ASL Instructions

        /**
         * BCC
         */
        //TODO BCC Instructions

        /**
         * BCS
         */
        //TODO BCS Instructions

        /**
         * BEQ
         */
        //TODO BEQ Instructions

        /**
         * BIT
         */
        //TODO BIT Instructions

        /**
         * BMI
         */
        //TODO BMI Instructions

        /**
         * BNE
         */
        //TODO BNE Instructions

        /**
         * BPL
         */
        //TODO BPL Instructions

        /**
         * BRK
         */
        //TODO BRK Instructions

        /**
         * BVC
         */
        //TODO BVC Instructions

        /**
         * BVS
         */
        //TODO BVS Instructions

        /**
         * CLC
         */
        this.put(NMOS6502Instructions.INS_CLC_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * CLD
         */
        this.put(NMOS6502Instructions.INS_CLD_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * CLI
         */
        this.put(NMOS6502Instructions.INS_CLI_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * CLV
         */
        this.put(NMOS6502Instructions.INS_CLV_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * CMP
         */
        //TODO CMP Instructions

        /**
         * CPX
         */
        //TODO CPX Instructions

        /**
         * CPY
         */
        //TODO CPY Instructions

        /**
         * DEC
         */
        //TODO DEC Instructions

        /**
         * DEX
         */
        //TODO DEX Instructions

        /**
         * DEY
         */
        //TODO DEY Instructions

        /**
         * EOR
         */
        //TODO INC Instructions

        /**
         * INC
         */
        //TODO INC Instructions

        /**
         * INX
         */
        //TODO INX Instructions

        /**
         * INY
         */
        //TODO INY Instructions


        /**
         * JMP
         */
        this.put(NMOS6502Instructions.INS_JMP_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 3));
        this.put(NMOS6502Instructions.INS_JMP_IND,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT, 3, 5));

        /**
         * JSR
         */
        //TODO JSR Instructions

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
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(NMOS6502Instructions.INS_LDA_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));

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

        /**
         * LSR
         */
        //TODO LSR Instructions

        /**
         * NOP
         */
        //TODO NOP Instructions

        /**
         * ORA
         */
        //TODO ORA Instructions

        /**
         * PHA
         */
        //TODO PHA Instructions

        /**
         * PHP
         */
        //TODO PHP Instructions

        /**
         * PLA
         */
        //TODO PLA Instructions

        /**
         * PLP
         */
        //TODO PLP Instructions

        /**
         * ROL
         */
        //TODO ROL Instructions

        /**
         * ROR
         */
        //TODO ROR Instructions

        /**
         * RTI
         */
        //TODO RTI Instructions

        /**
         * RTS
         */
        //TODO RTS Instructions

        /**
         * SBC
         */
        //TODO SBC Instructions

        /**
         * SEC
         */
        this.put(NMOS6502Instructions.INS_SEC_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * SED
         */
        this.put(NMOS6502Instructions.INS_SED_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * SEI
         */
        this.put(NMOS6502Instructions.INS_SEI_IMP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.IMPLICIT, 1, 2));

        /**
         * STA
         */
        this.put(NMOS6502Instructions.INS_STA_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_STA_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_STA_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(NMOS6502Instructions.INS_STA_ABX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_X, 3, 5));
        this.put(NMOS6502Instructions.INS_STA_ABY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE_Y, 3, 5));
        this.put(NMOS6502Instructions.INS_STA_INX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(NMOS6502Instructions.INS_STA_INY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 6));

        /**
         * STX
         */
        this.put(NMOS6502Instructions.INS_STX_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_STX_ZPY,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_Y, 2, 4));
        this.put(NMOS6502Instructions.INS_STX_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));

        /**
         * STY
         */
        this.put(NMOS6502Instructions.INS_STY_ZP,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(NMOS6502Instructions.INS_STY_ZPX,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(NMOS6502Instructions.INS_STY_ABS,
                new NMOS6502InstructionDetails(NMOS6502AddressingMode.ABSOLUTE, 3, 4));

        /**
         * TAX
         */
        //TODO TAX Instructions

        /**
         * TAY
         */
        //TODO TAY Instructions

        /**
         * TSX
         */
        //TODO TSX Instructions

        /**
         * TXA
         */
        //TODO TXA Instructions

        /**
         * TXS
         */
        //TODO TXS Instructions

        /**
         * TYA
         */
        //TODO TYA Instructions
    }

}
