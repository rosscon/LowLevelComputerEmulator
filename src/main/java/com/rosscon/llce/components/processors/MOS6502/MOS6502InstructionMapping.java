package com.rosscon.llce.components.processors.MOS6502;

public class MOS6502InstructionMapping  {

    private MOS6502InstructionDetails[] details;

    public MOS6502InstructionMapping(){

        this.details = new MOS6502InstructionDetails[256];

        /*
         * ADC
         */
        details[(MOS6502Instructions.INS_ADC_IMM)] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ADC_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ADC_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_ADC_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_ADC_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_ADC_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_ADC_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_ADC_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_ADC_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.ADC, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);


        /*
         * AND
         */
        details[MOS6502Instructions.INS_AND_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_AND_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_AND_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_AND_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_AND_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_AND_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_AND_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_AND_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.AND, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * ASL
         */
        details[MOS6502Instructions.INS_ASL_ACC] =
                new MOS6502InstructionDetails(MOS6502Instruction.ASL, MOS6502AddressingMode.ACCUMULATOR, 1, 2);
        details[MOS6502Instructions.INS_ASL_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.ASL, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_ASL_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ASL, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_ASL_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.ASL, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_ASL_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ASL, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * BCC
         */
        details[MOS6502Instructions.INS_BCC_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BCC, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BCS
         */
        details[MOS6502Instructions.INS_BCS_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BCS, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BEQ
         */
        details[MOS6502Instructions.INS_BEQ_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BEQ, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BIT
         */
        details[MOS6502Instructions.INS_BIT_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.BIT, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_BIT_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.BIT, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * BMI
         */
        details[MOS6502Instructions.INS_BMI_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BMI, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BNE
         */
        details[MOS6502Instructions.INS_BNE_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BNE, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BPL
         */
        details[MOS6502Instructions.INS_BPL_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BPL, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BRK
         */
        details[MOS6502Instructions.INS_BRK_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.BRK, MOS6502AddressingMode.IMPLICIT, 1, 7);

        /*
         * BVC
         */
        details[MOS6502Instructions.INS_BVC_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BVC, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * BVS
         */
        details[MOS6502Instructions.INS_BVS_REL] =
                new MOS6502InstructionDetails(MOS6502Instruction.BVS, MOS6502AddressingMode.RELATIVE, 2, 2);

        /*
         * CLC
         */
        details[MOS6502Instructions.INS_CLC_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLC, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLD
         */
        details[MOS6502Instructions.INS_CLD_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLD, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLI
         */
        details[MOS6502Instructions.INS_CLI_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLI, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CLV
         */
        details[MOS6502Instructions.INS_CLV_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CLV, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * CMP
         */
        details[MOS6502Instructions.INS_CMP_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CMP_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CMP_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_CMP_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_CMP_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_CMP_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_CMP_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_CMP_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.CMP, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * CPX
         */
        details[MOS6502Instructions.INS_CPX_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CPX_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CPX_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPX, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * CPY
         */
        details[MOS6502Instructions.INS_CPY_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_CPY_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_CPY_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.CPY, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * DEC
         */
        details[MOS6502Instructions.INS_DEC_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_DEC_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_DEC_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ABSOLUTE, 1, 6);
        details[MOS6502Instructions.INS_DEC_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEC, MOS6502AddressingMode.ABSOLUTE_X, 1, 7);

        /*
         * DEX
         */
        details[MOS6502Instructions.INS_DEX_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * DEY
         */
        details[MOS6502Instructions.INS_DEY_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.DEY, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * EOR
         */
        details[MOS6502Instructions.INS_EOR_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_EOR_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_EOR_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_EOR_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_EOR_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_EOR_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_EOR_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_EOR_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.EOR, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * INC
         */
        details[MOS6502Instructions.INS_INC_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.INC, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_INC_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.INC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_INC_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.INC, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_INC_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.INC, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * INX
         */
        details[MOS6502Instructions.INS_INX_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.INX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * INY
         */
        details[MOS6502Instructions.INS_INY_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.INY, MOS6502AddressingMode.IMPLICIT, 1, 2);


        /*
         * JMP
         */
        details[MOS6502Instructions.INS_JMP_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.JMP, MOS6502AddressingMode.ABSOLUTE, 3, 3);
        details[MOS6502Instructions.INS_JMP_IND] =
                new MOS6502InstructionDetails(MOS6502Instruction.JMP, MOS6502AddressingMode.INDIRECT, 3, 5);

        /*
         * JSR
         */
        details[MOS6502Instructions.INS_JSR_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.JSR, MOS6502AddressingMode.ABSOLUTE, 3, 6);

        /*
         * LDA
         */
        details[MOS6502Instructions.INS_LDA_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDA_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDA_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDA_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDA_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_LDA_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_LDA_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_LDA_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * LDX
         */
        details[MOS6502Instructions.INS_LDX_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDX_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDX_ZPY] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDX_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDX_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDX, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);

        /*
         * LDY
         */
        details[MOS6502Instructions.INS_LDY_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_LDY_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_LDY_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_LDY_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_LDY_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LDY, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);

        /*
         * LSR
         */
        details[MOS6502Instructions.INS_LSR_ACC] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ACCUMULATOR, 1, 2);
        details[MOS6502Instructions.INS_LSR_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_LSR_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_LSR_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_LSR_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.LSR, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * NOP
         */
        details[MOS6502Instructions.INS_NOP_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * ORA
         */
        details[MOS6502Instructions.INS_ORA_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_ORA_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_ORA_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_ORA_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_ORA_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_ORA_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_ORA_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_ORA_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.ORA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * PHA
         */
        details[MOS6502Instructions.INS_PHA_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.PHA, MOS6502AddressingMode.IMPLICIT, 1, 3);

        /*
         * PHP
         */
        details[MOS6502Instructions.INS_PHP_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.PHP, MOS6502AddressingMode.IMPLICIT, 1, 3);

        /*
         * PLA
         */
        details[MOS6502Instructions.INS_PLA_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.PLA, MOS6502AddressingMode.IMPLICIT, 1, 4);

        /*
         * PLP
         */
        details[MOS6502Instructions.INS_PLP_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.PLP, MOS6502AddressingMode.IMPLICIT, 1, 4);

        /*
         * ROL
         */
        details[MOS6502Instructions.INS_ROL_ACC] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROL, MOS6502AddressingMode.ACCUMULATOR, 1, 2);
        details[MOS6502Instructions.INS_ROL_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROL, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_ROL_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROL, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_ROL_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROL, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_ROL_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROL, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * ROR
         */
        details[MOS6502Instructions.INS_ROR_ACC] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROR, MOS6502AddressingMode.ACCUMULATOR, 1, 2);
        details[MOS6502Instructions.INS_ROR_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROR, MOS6502AddressingMode.ZERO_PAGE, 2, 5);
        details[MOS6502Instructions.INS_ROR_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROR, MOS6502AddressingMode.ZERO_PAGE_X, 2, 6);
        details[MOS6502Instructions.INS_ROR_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROR, MOS6502AddressingMode.ABSOLUTE, 3, 6);
        details[MOS6502Instructions.INS_ROR_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.ROR, MOS6502AddressingMode.ABSOLUTE_X, 3, 7);

        /*
         * RTI
         */
        details[MOS6502Instructions.INS_RTI_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.RTI, MOS6502AddressingMode.IMPLICIT, 1, 6);

        /*
         * RTS
         */
        details[MOS6502Instructions.INS_RTS_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.RTS, MOS6502AddressingMode.IMPLICIT, 1, 6);

        /*
         * SBC
         */
        details[MOS6502Instructions.INS_SBC_IMM] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[MOS6502Instructions.INS_SBC_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_SBC_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_SBC_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_SBC_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE_X, 3, 4);
        details[MOS6502Instructions.INS_SBC_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.ABSOLUTE_Y, 3, 4);
        details[MOS6502Instructions.INS_SBC_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_SBC_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 5);

        /*
         * SEC
         */
        details[MOS6502Instructions.INS_SEC_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.SEC, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * SED
         */
        details[MOS6502Instructions.INS_SED_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.SED, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * SEI
         */
        details[MOS6502Instructions.INS_SEI_IMP] =
                new MOS6502InstructionDetails(MOS6502Instruction.SEI, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * STA
         */
        details[MOS6502Instructions.INS_STA_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STA_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_STA_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[MOS6502Instructions.INS_STA_ABX] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE_X, 3, 5);
        details[MOS6502Instructions.INS_STA_ABY] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.ABSOLUTE_Y, 3, 5);
        details[MOS6502Instructions.INS_STA_INX] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.INDEXED_INDIRECT_X, 2, 6);
        details[MOS6502Instructions.INS_STA_INY] =
                new MOS6502InstructionDetails(MOS6502Instruction.STA, MOS6502AddressingMode.INDIRECT_INDEXED_Y, 2, 6);

        /*
         * STX
         */
        details[MOS6502Instructions.INS_STX_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STX_ZPY] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ZERO_PAGE_Y, 2, 4);
        details[MOS6502Instructions.INS_STX_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.STX, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * STY
         */
        details[MOS6502Instructions.INS_STY_ZP] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ZERO_PAGE, 2, 3);
        details[MOS6502Instructions.INS_STY_ZPX] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ZERO_PAGE_X, 2, 4);
        details[MOS6502Instructions.INS_STY_ABS] =
                new MOS6502InstructionDetails(MOS6502Instruction.STY, MOS6502AddressingMode.ABSOLUTE, 3, 4);

        /*
         * TAX
         */
        details[MOS6502Instructions.INS_TAX] =
                new MOS6502InstructionDetails(MOS6502Instruction.TAX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TAY
         */
        details[MOS6502Instructions.INS_TAY] =
                new MOS6502InstructionDetails(MOS6502Instruction.TAY, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TSX
         */
        details[MOS6502Instructions.INS_TSX] =
                new MOS6502InstructionDetails(MOS6502Instruction.TSX, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TXA
         */
        details[MOS6502Instructions.INS_TXA] =
                new MOS6502InstructionDetails(MOS6502Instruction.TXA, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TXS
         */
        details[MOS6502Instructions.INS_TXS] =
                new MOS6502InstructionDetails(MOS6502Instruction.TXS, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * TYA
         */
        details[MOS6502Instructions.INS_TYA] =
                new MOS6502InstructionDetails(MOS6502Instruction.TYA, MOS6502AddressingMode.IMPLICIT, 1, 2);

        /*
         * Undocumented / Illegal Opcodes
         * http://www.ffd2.com/fridge/docs/6502-NMOS.extra.opcodes
         *
         */
        //NOP, perform no memory operations and take 2 cycles to execute
        details[0x1A] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);
        details[0x3A] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);
        details[0x5A] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);
        details[0x7A] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);
        details[0xDA] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);
        details[0xFA] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMPLICIT, 1, 2);

        //SBC 0xEB is an undocumented instruction that appears to do the same as SBC Immediate mode
        details[0xEB] =
                new MOS6502InstructionDetails(MOS6502Instruction.SBC, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        // SKB Opcodes - Skip Next byte, can take 2,3 or 4 cycles to execute
        details[0x80] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x82] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0xC2] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0xE2] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x04] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x14] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x34] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x44] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x54] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x64] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0x74] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0xD4] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);
        details[0xF4] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.IMMEDIATE, 2, 2);

        // SKW Opcodes - Skip next word (2, bytes) always takes 4 cycles
        details[0x0C] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0x1C] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0x3C] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0x5C] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0x7C] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0xDC] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);
        details[0xFC] =
                new MOS6502InstructionDetails(MOS6502Instruction.NOP, MOS6502AddressingMode.ABSOLUTE, 3, 4);

    }


    public MOS6502InstructionDetails getInstructionDetails(int instruction){
        return this.details[instruction & MOS6502Constants.MASK_LAST_BYTE];
    }
}
