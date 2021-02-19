package com.rosscon.llce.components.processors.MOS6502;

public class MOS6502InstructionMapping  {

    private MOS6502InstructionDetails[] details;

    public MOS6502InstructionMapping(){

        this.details = new MOS6502InstructionDetails[256];

        /*
         * ADC
         */
        details[(MOS6502Instructions.INS_ADC_IMM & 0xFF)] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ADC_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ADC_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_ADC_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_ADC_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_ADC_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_ADC_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_ADC_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_ADC_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);


        /*
         * AND
         */
        details[MOS6502Instructions.INS_AND_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_AND_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_AND_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_AND_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_AND_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_AND_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_AND_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_AND_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * ASL
         */
        //TODO ASL Instructions

        /*
         * BCC
         */
        details[MOS6502Instructions.INS_BCC_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BCC, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BCS
         */
        details[MOS6502Instructions.INS_BCS_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BCS, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BEQ
         */
        details[MOS6502Instructions.INS_BEQ_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BEQ, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BIT
         */
        details[MOS6502Instructions.INS_BIT_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BIT, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_BIT_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BIT, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * BMI
         */
        details[MOS6502Instructions.INS_BMI_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BMI, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BNE
         */
        details[MOS6502Instructions.INS_BNE_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BNE, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BPL
         */
        details[MOS6502Instructions.INS_BPL_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BPL, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BRK
         */
        details[MOS6502Instructions.INS_BRK_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BRK, MOS6502AddressingMode.IMPLICIT, 1, 7);

        /*
         * BVC
         */
        details[MOS6502Instructions.INS_BVC_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BVC, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BVS
         */
        details[MOS6502Instructions.INS_BVS_REL & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.BVS, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * CLC
         */
        details[MOS6502Instructions.INS_CLC_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLC, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLD
         */
        details[MOS6502Instructions.INS_CLD_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLD, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLI
         */
        details[MOS6502Instructions.INS_CLI_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLI, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLV
         */
        details[MOS6502Instructions.INS_CLV_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLV, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CMP
         */
        details[MOS6502Instructions.INS_CMP_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CMP_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CMP_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_CMP_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_CMP_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_CMP_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_CMP_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_CMP_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * CPX
         */
        details[MOS6502Instructions.INS_CPX_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CPX_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CPX_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * CPY
         */
        details[MOS6502Instructions.INS_CPY_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CPY_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CPY_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * DEC
         */
        details[MOS6502Instructions.INS_DEC_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_DEC_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_DEC_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ABSOLUTE, 1, 6);
        details[MOS6502Instructions.INS_DEC_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ABSOLUTE_X, 1, 7);

        /*
         * DEX
         */
        details[MOS6502Instructions.INS_DEX_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * DEY
         */
        details[MOS6502Instructions.INS_DEY_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEY, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * EOR
         */
        details[MOS6502Instructions.INS_EOR_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_EOR_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_EOR_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_EOR_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_EOR_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_EOR_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_EOR_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_EOR_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * INC
         */
        //TODO INC Instructions

        /*
         * INX
         */
        details[MOS6502Instructions.INS_INX_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.INX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * INY
         */
        details[MOS6502Instructions.INS_INY_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.INY, MOS6502AddressingMode.IMPLICIT, 1, 2);


        /*
         * JMP
         */
        details[MOS6502Instructions.INS_JMP_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.JMP, MOS6502AddressingMode.ABSOLUTE, 3, 3);
        details[MOS6502Instructions.INS_JMP_IND & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.JMP, MOS6502AddressingMode.INDIRECT, 3, 5);

        /*
         * JSR
         */
        details[MOS6502Instructions.INS_JSR_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.JSR, MOS6502AddressingMode.ABSOLUTE, 3, 6);

        /*
         * LDA
         */
        details[MOS6502Instructions.INS_LDA_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDA_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDA_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDA_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDA_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_LDA_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_LDA_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_LDA_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * LDX
         */
        details[MOS6502Instructions.INS_LDX_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDX_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDX_ZPY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDX_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDX_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);

        /*
         * LDY
         */
        details[MOS6502Instructions.INS_LDY_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDY_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDY_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDY_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDY_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);

        /*
         * LSR
         */
        details[MOS6502Instructions.INS_LSR_ACC & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ACCUMULATOR, 1, 2);
        details[MOS6502Instructions.INS_LSR_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_LSR_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_LSR_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_LSR_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * NOP
         */
        details[MOS6502Instructions.INS_NOP_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * ORA
         */
        details[MOS6502Instructions.INS_ORA_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ORA_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_ORA_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_ORA_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_ORA_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_ORA_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_ORA_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_ORA_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * PHA
         */
        details[MOS6502Instructions.INS_PHA_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.PHA, MOS6502AddressingMode.IMPLICIT, 1, 3);

        /*
         * PHP
         */
        details[MOS6502Instructions.INS_PHP_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.PHP, MOS6502AddressingMode.IMPLICIT, 1, 3);

        /*
         * PLA
         */
        details[MOS6502Instructions.INS_PLA_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.PLA, MOS6502AddressingMode.IMPLICIT, 1, 4);

        /*
         * PLP
         */
        details[MOS6502Instructions.INS_PLP_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.PLP, MOS6502AddressingMode.IMPLICIT, 1, 4);

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
        details[MOS6502Instructions.INS_RTI_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.RTI, MOS6502AddressingMode.IMPLICIT, 1, 6);

        /*
         * RTS
         */
        details[MOS6502Instructions.INS_RTS_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.RTS, MOS6502AddressingMode.IMPLICIT, 1, 6);

        /*
         * SBC
         */
        details[MOS6502Instructions.INS_SBC_IMM & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_SBC_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_SBC_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_SBC_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_SBC_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_SBC_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_SBC_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_SBC_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * SEC
         */
        details[MOS6502Instructions.INS_SEC_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SEC, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * SED
         */
        details[MOS6502Instructions.INS_SED_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SED, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * SEI
         */
        details[MOS6502Instructions.INS_SEI_IMP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.SEI, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * STA
         */
        details[MOS6502Instructions.INS_STA_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STA_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_STA_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_STA_ABX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE_X, 3, 5);
        details[MOS6502Instructions.INS_STA_ABY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 5);
        details[MOS6502Instructions.INS_STA_INX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_STA_INY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 6);

        /*
         * STX
         */
        details[MOS6502Instructions.INS_STX_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STX_ZPY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ZERO_PAGE_Y, 2, 4);
        details[MOS6502Instructions.INS_STX_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * STY
         */
        details[MOS6502Instructions.INS_STY_ZP & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STY_ZPX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_STY_ABS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * TAX
         */
        details[MOS6502Instructions.INS_TAX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TAX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TAY
         */
        details[MOS6502Instructions.INS_TAY & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TAY, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TSX
         */
        details[MOS6502Instructions.INS_TSX & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TSX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TXA
         */
        details[MOS6502Instructions.INS_TXA & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TXA, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TXS
         */
        details[MOS6502Instructions.INS_TXS & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TXS, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TYA
         */
        details[MOS6502Instructions.INS_TYA & 0xFF] =
                new MOS6502InstructionDetails(MOS6502Instruction.TYA, MOS6502AddressingMode.IMPLICIT, 1, 2);
    }


    public MOS6502InstructionDetails getInstructionDetails(byte instruction){
        return this.details[instruction & 0xFF];
    }
}
