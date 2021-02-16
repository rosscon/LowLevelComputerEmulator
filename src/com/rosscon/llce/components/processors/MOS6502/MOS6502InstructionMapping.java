package com.rosscon.llce.components.processors.MOS6502;

import java.util.HashMap;

public class MOS6502InstructionMapping extends HashMap {

    public MOS6502InstructionMapping(){

        /*
         * ADC
         */
        this.put(MOS6502Instructions.INS_ADC_IMM,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(MOS6502Instructions.INS_ADC_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_ADC_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_ADC_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_ADC_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(MOS6502Instructions.INS_ADC_ABY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(MOS6502Instructions.INS_ADC_INX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(MOS6502Instructions.INS_ADC_INY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));


        /*
         * AND
         */
        this.put(MOS6502Instructions.INS_AND_IMM,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(MOS6502Instructions.INS_AND_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_AND_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_AND_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_AND_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(MOS6502Instructions.INS_AND_ABY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(MOS6502Instructions.INS_AND_INX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(MOS6502Instructions.INS_AND_INY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));

        /*
         * ASL
         */
        //TODO ASL Instructions

        /*
         * BCC
         */
        this.put(MOS6502Instructions.INS_BCC_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BCS
         */
        this.put(MOS6502Instructions.INS_BCS_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BEQ
         */
        this.put(MOS6502Instructions.INS_BEQ_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BIT
         */
        //TODO BIT Instructions

        /*
         * BMI
         */
        this.put(MOS6502Instructions.INS_BMI_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BNE
         */
        this.put(MOS6502Instructions.INS_BNE_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BPL
         */
        this.put(MOS6502Instructions.INS_BPL_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BRK
         */
        this.put(MOS6502Instructions.INS_BRK_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 7));

        /*
         * BVC
         */
        this.put(MOS6502Instructions.INS_BVC_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * BVS
         */
        this.put(MOS6502Instructions.INS_BVS_REL,
                new MOS6502InstructionDetails(MOS6502AddressingMode.RELATIVE, 2, 2));

        /*
         * CLC
         */
        this.put(MOS6502Instructions.INS_CLC_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * CLD
         */
        this.put(MOS6502Instructions.INS_CLD_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * CLI
         */
        this.put(MOS6502Instructions.INS_CLI_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * CLV
         */
        this.put(MOS6502Instructions.INS_CLV_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * CMP
         */
        //TODO CMP Instructions

        /*
         * CPX
         */
        //TODO CPX Instructions

        /*
         * CPY
         */
        //TODO CPY Instructions

        /*
         * DEC
         */
        this.put(MOS6502Instructions.INS_DEC_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 5));
        this.put(MOS6502Instructions.INS_DEC_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 6));
        this.put(MOS6502Instructions.INS_DEC_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 1, 6));
        this.put(MOS6502Instructions.INS_DEC_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 1, 7));

        /*
         * DEX
         */
        this.put(MOS6502Instructions.INS_DEX_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 1, 2));

        /*
         * DEY
         */
        this.put(MOS6502Instructions.INS_DEY_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 1, 2));

        /*
         * EOR
         */
        //TODO INC Instructions

        /*
         * INC
         */
        //TODO INC Instructions

        /*
         * INX
         */
        this.put(MOS6502Instructions.INS_INX_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * INY
         */
        this.put(MOS6502Instructions.INS_INY_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));


        /*
         * JMP
         */
        this.put(MOS6502Instructions.INS_JMP_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 3));
        this.put(MOS6502Instructions.INS_JMP_IND,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDIRECT, 3, 5));

        /*
         * JSR
         */
        this.put(MOS6502Instructions.INS_JSR_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 6));

        /*
         * LDA
         */
        this.put(MOS6502Instructions.INS_LDA_IMM,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(MOS6502Instructions.INS_LDA_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_LDA_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_LDA_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_LDA_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 4));
        this.put(MOS6502Instructions.INS_LDA_ABY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_Y, 3, 4));
        this.put(MOS6502Instructions.INS_LDA_INX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(MOS6502Instructions.INS_LDA_INY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5));

        /*
         * LDX
         */
        this.put(MOS6502Instructions.INS_LDX_IMM,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(MOS6502Instructions.INS_LDX_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_LDX_ZPY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_LDX_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_LDX_ABY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 4));

        /*
         * LDY
         */
        this.put(MOS6502Instructions.INS_LDY_IMM,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMMEDIATE, 2, 2));
        this.put(MOS6502Instructions.INS_LDY_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_LDY_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_LDY_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_LDY_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 4));

        /*
         * LSR
         */
        //TODO LSR Instructions

        /*
         * NOP
         */
        this.put(MOS6502Instructions.INS_NOP_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * ORA
         */
        //TODO ORA Instructions

        /*
         * PHA
         */
        this.put(MOS6502Instructions.INS_PHA_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 3));

        /*
         * PHP
         */
        //TODO PHP Instructions

        /*
         * PLA
         */
        this.put(MOS6502Instructions.INS_PLA_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 4));

        /*
         * PLP
         */
        //TODO PLP Instructions

        /*
         * ROL
         */
        //TODO ROL Instructions

        /*
         * ROR
         */
        //TODO ROR Instructions

        /*
         * RTI
         */
        this.put(MOS6502Instructions.INS_RTI_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 6));

        /*
         * RTS
         */
        //TODO RTS Instructions

        /*
         * SBC
         */
        //TODO SBC Instructions

        /*
         * SEC
         */
        this.put(MOS6502Instructions.INS_SEC_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * SED
         */
        this.put(MOS6502Instructions.INS_SED_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * SEI
         */
        this.put(MOS6502Instructions.INS_SEI_IMP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * STA
         */
        this.put(MOS6502Instructions.INS_STA_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_STA_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_STA_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));
        this.put(MOS6502Instructions.INS_STA_ABX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_X, 3, 5));
        this.put(MOS6502Instructions.INS_STA_ABY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE_Y, 3, 5));
        this.put(MOS6502Instructions.INS_STA_INX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6));
        this.put(MOS6502Instructions.INS_STA_INY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 6));

        /*
         * STX
         */
        this.put(MOS6502Instructions.INS_STX_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_STX_ZPY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_Y, 2, 4));
        this.put(MOS6502Instructions.INS_STX_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));

        /*
         * STY
         */
        this.put(MOS6502Instructions.INS_STY_ZP,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE, 2, 3));
        this.put(MOS6502Instructions.INS_STY_ZPX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ZERO_PAGE_X, 2, 4));
        this.put(MOS6502Instructions.INS_STY_ABS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.ABSOLUTE, 3, 4));

        /*
         * TAX
         */
        this.put(MOS6502Instructions.INS_TAX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * TAY
         */
        this.put(MOS6502Instructions.INS_TAY,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * TSX
         */
        this.put(MOS6502Instructions.INS_TSX,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * TXA
         */
        this.put(MOS6502Instructions.INS_TXA,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * TXS
         */
        this.put(MOS6502Instructions.INS_TXS,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));

        /*
         * TYA
         */
        this.put(MOS6502Instructions.INS_TYA,
                new MOS6502InstructionDetails(MOS6502AddressingMode.IMPLICIT, 1, 2));
    }

}
