package com.rosscon.llce.components.processors.MOS6502;

public class MOS6502Instructions {

    /**
     * ADC Instructions
     */
    public static final int INS_ADC_IMM  = 0x69;
    public static final int INS_ADC_ZP   = 0x65;
    public static final int INS_ADC_ZPX  = 0x75;
    public static final int INS_ADC_ABS  = 0x6D;
    public static final int INS_ADC_ABX  = 0x7D;
    public static final int INS_ADC_ABY  = 0x79;
    public static final int INS_ADC_INX  = 0x61;
    public static final int INS_ADC_INY  = 0x71;

    /**
     * AND Instructions
     */
    public static final int INS_AND_IMM  = 0x29;
    public static final int INS_AND_ZP   = 0x25;
    public static final int INS_AND_ZPX  = 0x35;
    public static final int INS_AND_ABS  = 0x2D;
    public static final int INS_AND_ABX  = 0x3D;
    public static final int INS_AND_ABY  = 0x39;
    public static final int INS_AND_INX  = 0x21;
    public static final int INS_AND_INY  = 0x31;

    /**
     * ASL Instructions
     */
    public static final int INS_ASL_ACC    = 0x0A;
    public static final int INS_ASL_ZP     = 0x06;
    public static final int INS_ASL_ZPX    = 0x16;
    public static final int INS_ASL_ABS    = 0x0E;
    public static final int INS_ASL_ABX    = 0x1E;

    /**
     * BCC Instructions
     */
    public static final int INS_BCC_REL  = 0x90;

    /**
     * BCS Instructions
     */
    public static final int INS_BCS_REL  = 0xB0;

    /**
     * BEQ Instructions
     */
    public static final int INS_BEQ_REL  = 0xF0;

    /**
     * BIT Instructions
     */
    public static final int INS_BIT_ZP   = 0x24;
    public static final int INS_BIT_ABS  = 0x2C;

    /**
     * BMI Instructions
     */
    public static final int INS_BMI_REL  = 0x30;

    /**
     * BNE Instructions
     */
    public static final int INS_BNE_REL  = 0xD0;

    /**
     * BPL Instructions
     */
    public static final int INS_BPL_REL  = 0x10;

    /**
     * BRK Instructions
     */
    public static final int INS_BRK_IMP  = 0x00;

    /**
     * BVC Instructions
     */
    public static final int INS_BVC_REL  = 0x50;

    /**
     * BVS Instructions
     */
    public static final int INS_BVS_REL  = 0x70;

    /**
     * CLC Instructions
     */
    public static final int INS_CLC_IMP  = 0x18;

    /**
     * CLD Instructions
     */
    public static final int INS_CLD_IMP  = 0xD8;

    /**
     * CLI Instructions
     */
    public static final int INS_CLI_IMP  = 0x58;

    /**
     * CLV Instructions
     */
    public static final int INS_CLV_IMP  = 0xB8;

    /**
     * CMP Instructions
     */
    public static final int INS_CMP_IMM  = 0xC9;
    public static final int INS_CMP_ZP   = 0xC5;
    public static final int INS_CMP_ZPX  = 0xD5;
    public static final int INS_CMP_ABS  = 0xCD;
    public static final int INS_CMP_ABX  = 0xDD;
    public static final int INS_CMP_ABY  = 0xD9;
    public static final int INS_CMP_INX  = 0xC1;
    public static final int INS_CMP_INY  = 0xD1;

    /**
     * CPX Instructions
     */
    public static final int INS_CPX_IMM  = 0xE0;
    public static final int INS_CPX_ZP   = 0xE4;
    public static final int INS_CPX_ABS  = 0xEC;

    /**
     * CPY Instructions
     */
    public static final int INS_CPY_IMM  = 0xC0;
    public static final int INS_CPY_ZP   = 0xC4;
    public static final int INS_CPY_ABS  = 0xCC;

    /**
     * DEC Instructions
     */
    public static final int INS_DEC_ZP   = 0xC6;
    public static final int INS_DEC_ZPX  = 0xD6;
    public static final int INS_DEC_ABS  = 0xCE;
    public static final int INS_DEC_ABX  = 0xDE;

    /**
     * DEX Instructions
     */
    public static final int INS_DEX_IMP  = 0xCA;

    /**
     * DEY Instructions
     */
    public static final int INS_DEY_IMP  = 0x88;

    /**
     * EOR Instructions
     */
    public static final int INS_EOR_IMM    = 0x49;
    public static final int INS_EOR_ZP     = 0x45;
    public static final int INS_EOR_ZPX    = 0x55;
    public static final int INS_EOR_ABS    = 0x4D;
    public static final int INS_EOR_ABX    = 0x5D;
    public static final int INS_EOR_ABY    = 0x59;
    public static final int INS_EOR_INX    = 0x41;
    public static final int INS_EOR_INY    = 0x51;

    /**
     * INC Instructions
     */
    public static final int INS_INC_ZP     = 0xE6;
    public static final int INS_INC_ZPX    = 0xF6;
    public static final int INS_INC_ABS    = 0xEE;
    public static final int INS_INC_ABX    = 0xFE;

    /**
     * INX Instructions
     */
    public static final int INS_INX_IMP    = 0xE8;

    /**
     * INY Instructions
     */
    public static final int INS_INY_IMP    = 0xC8;

    /**
     * JMP Instructions
     */
    public static final int INS_JMP_ABS    = 0x4C;
    public static final int INS_JMP_IND    = 0x6C;


    /**
     * JSR Instructions
     */
    public static final int INS_JSR_ABS    = 0x20;

    /**
     * LDA Instructions
     */
    public static final int INS_LDA_IMM    = 0xA9;
    public static final int INS_LDA_ZP     = 0xA5;
    public static final int INS_LDA_ZPX    = 0xB5;
    public static final int INS_LDA_ABS    = 0xAD;
    public static final int INS_LDA_ABX    = 0xBD;
    public static final int INS_LDA_ABY    = 0xB9;
    public static final int INS_LDA_INX    = 0xA1;
    public static final int INS_LDA_INY    = 0xB1;

    /**
     * LDX Instructions
     */
    public static final int INS_LDX_IMM    = 0xA2;
    public static final int INS_LDX_ZP     = 0xA6;
    public static final int INS_LDX_ZPY    = 0xB6;
    public static final int INS_LDX_ABS    = 0xAE;
    public static final int INS_LDX_ABY    = 0xBE;

    /**
     * LDY Instructions
     */
    public static final int INS_LDY_IMM    = 0xA0;
    public static final int INS_LDY_ZP     = 0xA4;
    public static final int INS_LDY_ZPX    = 0xB4;
    public static final int INS_LDY_ABS    = 0xAC;
    public static final int INS_LDY_ABX    = 0xBC;

    /**
     * LSR Instructions
     */
    public static final int INS_LSR_ACC    = 0x4A;
    public static final int INS_LSR_ZP     = 0x46;
    public static final int INS_LSR_ZPX    = 0x56;
    public static final int INS_LSR_ABS    = 0x4E;
    public static final int INS_LSR_ABX    = 0x5E;

    /**
     * NOP Instructions
     */
    public static final int INS_NOP_IMP    = 0xEA;

    /**
     * ORA Instructions
     */
    public static final int INS_ORA_IMM    = 0x09;
    public static final int INS_ORA_ZP     = 0x05;
    public static final int INS_ORA_ZPX    = 0x15;
    public static final int INS_ORA_ABS    = 0x0D;
    public static final int INS_ORA_ABX    = 0x1D;
    public static final int INS_ORA_ABY    = 0x19;
    public static final int INS_ORA_INX    = 0x01;
    public static final int INS_ORA_INY    = 0x11;


    /**
     * PHA Instructions
     */
    public static final int INS_PHA_IMP    = 0x48;

    /**
     * PHP Instructions
     */
    public static final int INS_PHP_IMP    = 0x08;

    /**
     * PLA Instructions
     */
    public static final int INS_PLA_IMP    = 0x68;

    /**
     * PLP Instructions
     */
    public static final int INS_PLP_IMP    = 0x28;

    /**
     * ROL Instructions
     */
    public static final int INS_ROL_ACC    = 0x2A;
    public static final int INS_ROL_ZP     = 0x26;
    public static final int INS_ROL_ZPX    = 0x36;
    public static final int INS_ROL_ABS    = 0x2E;
    public static final int INS_ROL_ABX    = 0x3E;

    /**
     * ROR Instructions
     */
    public static final int INS_ROR_ACC    = 0x6A;
    public static final int INS_ROR_ZP     = 0x66;
    public static final int INS_ROR_ZPX    = 0x76;
    public static final int INS_ROR_ABS    = 0x6E;
    public static final int INS_ROR_ABX    = 0x7E;

    /**
     * RTI Instructions
     */
    public static final int INS_RTI_IMP    = 0x40;

    /**
     * RTS Instructions
     */
    public static final int INS_RTS_IMP    = 0x60;

    /**
     * SBC Instructions
     */
    public static final int INS_SBC_IMM    = 0xE9;
    public static final int INS_SBC_ZP     = 0xE5;
    public static final int INS_SBC_ZPX    = 0xF5;
    public static final int INS_SBC_ABS    = 0xED;
    public static final int INS_SBC_ABX    = 0xFD;
    public static final int INS_SBC_ABY    = 0xF9;
    public static final int INS_SBC_INX    = 0xE1;
    public static final int INS_SBC_INY    = 0xF1;

    /**
     * SEC Instructions
     */
    public static final int INS_SEC_IMP    = 0x38;

    /**
     * SED Instructions
     */
    public static final int INS_SED_IMP    = 0xF8;

    /**
     * SEI Instructions
     */
    public static final int INS_SEI_IMP    = 0x78;

    /**
     * STA Instructions
     */
    public static final int INS_STA_ZP    = 0x85;
    public static final int INS_STA_ZPX   = 0x95;
    public static final int INS_STA_ABS   = 0x8D;
    public static final int INS_STA_ABX   = 0x9D;
    public static final int INS_STA_ABY   = 0x99;
    public static final int INS_STA_INX   = 0x81;
    public static final int INS_STA_INY   = 0x91;

    /**
     * STX Instructions
     */
    public static final int INS_STX_ZP    = 0x86;
    public static final int INS_STX_ZPY   = 0x96;
    public static final int INS_STX_ABS   = 0x8E;

    /**
     * STY Instructions
     */
    public static final int INS_STY_ZP    = 0x84;
    public static final int INS_STY_ZPX   = 0x94;
    public static final int INS_STY_ABS   = 0x8C;

    /**
     * TAX Instructions
     */
    public static final int INS_TAX   = 0xAA;

    /**
     * TAY Instructions
     */
    public static final int INS_TAY   = 0xA8;

    /**
     * TSX Instructions
     */
    public static final int INS_TSX   = 0xBA;

    /**
     * TXA Instructions
     */
    public static final int INS_TXA   = 0x8A;

    /**
     * TXS Instructions
     */
    public static final int INS_TXS   = 0x9A;

    /**
     * TYA Instructions
     */
    public static final int INS_TYA   = 0x98;
}
