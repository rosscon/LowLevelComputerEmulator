package com.rosscon.llce.components.processors.MOS6502;

public class MOS6502Instructions {

    /**
     * ADC Instructions
     */
    public static final byte INS_ADC_IMM  = (byte)0x69;
    public static final byte INS_ADC_ZP   = (byte)0x65;
    public static final byte INS_ADC_ZPX  = (byte)0x75;
    public static final byte INS_ADC_ABS  = (byte)0x6D;
    public static final byte INS_ADC_ABX  = (byte)0x7D;
    public static final byte INS_ADC_ABY  = (byte)0x79;
    public static final byte INS_ADC_INX  = (byte)0x61;
    public static final byte INS_ADC_INY  = (byte)0x71;

    /**
     * AND Instructions
     */
    public static final byte INS_AND_IMM  = (byte)0x29;
    public static final byte INS_AND_ZP   = (byte)0x25;
    public static final byte INS_AND_ZPX  = (byte)0x35;
    public static final byte INS_AND_ABS  = (byte)0x2D;
    public static final byte INS_AND_ABX  = (byte)0x3D;
    public static final byte INS_AND_ABY  = (byte)0x39;
    public static final byte INS_AND_INX  = (byte)0x21;
    public static final byte INS_AND_INY  = (byte)0x31;

    /**
     * ASL Instructions
     */
    //TODO ASL Instructions

    /**
     * BCC Instructions
     */
    public static final byte INS_BCC_REL  = (byte)0x90;

    /**
     * BCS Instructions
     */
    public static final byte INS_BCS_REL  = (byte)0xB0;

    /**
     * BEQ Instructions
     */
    public static final byte INS_BEQ_REL  = (byte)0xF0;

    /**
     * BIT Instructions
     */
    //TODO BIT Instructions

    /**
     * BMI Instructions
     */
    public static final byte INS_BMI_REL  = (byte)0x30;

    /**
     * BNE Instructions
     */
    public static final byte INS_BNE_REL  = (byte)0xD0;

    /**
     * BPL Instructions
     */
    public static final byte INS_BPL_REL  = (byte)0x10;

    /**
     * BRK Instructions
     */
    public static final byte INS_BRK_IMP  = (byte)0x00;

    /**
     * BVC Instructions
     */
    public static final byte INS_BVC_REL  = (byte)0x50;

    /**
     * BVS Instructions
     */
    public static final byte INS_BVS_REL  = (byte)0x70;

    /**
     * CLC Instructions
     */
    public static final byte INS_CLC_IMP  = (byte)0x18;

    /**
     * CLD Instructions
     */
    public static final byte INS_CLD_IMP  = (byte)0xD8;

    /**
     * CLI Instructions
     */
    public static final byte INS_CLI_IMP  = (byte)0x58;

    /**
     * CLV Instructions
     */
    public static final byte INS_CLV_IMP  = (byte)0xB8;

    /**
     * CMP Instructions
     */
    public static final byte INS_CMP_IMM  = (byte)0xC9;
    public static final byte INS_CMP_ZP   = (byte)0xC5;
    public static final byte INS_CMP_ZPX  = (byte)0xD5;
    public static final byte INS_CMP_ABS  = (byte)0xCD;
    public static final byte INS_CMP_ABX  = (byte)0xDD;
    public static final byte INS_CMP_ABY  = (byte)0xD9;
    public static final byte INS_CMP_INX  = (byte)0xC1;
    public static final byte INS_CMP_INY  = (byte)0xD1;

    /**
     * CPX Instructions
     */
    public static final byte INS_CPX_IMM  = (byte)0xE0;
    public static final byte INS_CPX_ZP   = (byte)0xE4;
    public static final byte INS_CPX_ABS  = (byte)0xEC;

    /**
     * CPY Instructions
     */
    public static final byte INS_CPY_IMM  = (byte)0xC0;
    public static final byte INS_CPY_ZP   = (byte)0xC4;
    public static final byte INS_CPY_ABS  = (byte)0xCC;

    /**
     * DEC Instructions
     */
    public static final byte INS_DEC_ZP   = (byte)0xC6;
    public static final byte INS_DEC_ZPX  = (byte)0xD6;
    public static final byte INS_DEC_ABS  = (byte)0xCE;
    public static final byte INS_DEC_ABX  = (byte)0xDE;

    /**
     * DEX Instructions
     */
    public static final byte INS_DEX_IMP  = (byte)0xCA;

    /**
     * DEY Instructions
     */
    public static final byte INS_DEY_IMP  = (byte)0x88;

    /**
     * EOR Instructions
     */
    //TODO INC Instructions

    /**
     * INC Instructions
     */
    //TODO INC Instructions

    /**
     * INX Instructions
     */
    public static final byte INS_INX_IMP    = (byte)0xE8;

    /**
     * INY Instructions
     */
    public static final byte INS_INY_IMP    = (byte)0xC8;

    /**
     * JMP Instructions
     */
    public static final byte INS_JMP_ABS    = (byte)0x4C;
    public static final byte INS_JMP_IND    = (byte)0x6C;


    /**
     * JSR Instructions
     */
    public static final byte INS_JSR_ABS    = (byte)0x20;

    /**
     * LDA Instructions
     */
    public static final byte INS_LDA_IMM    = (byte)0xA9;
    public static final byte INS_LDA_ZP     = (byte)0xA5;
    public static final byte INS_LDA_ZPX    = (byte)0xB5;
    public static final byte INS_LDA_ABS    = (byte)0xAD;
    public static final byte INS_LDA_ABX    = (byte)0xBD;
    public static final byte INS_LDA_ABY    = (byte)0xB9;
    public static final byte INS_LDA_INX    = (byte)0xA1;
    public static final byte INS_LDA_INY    = (byte)0xB1;

    /**
     * LDX Instructions
     */
    public static final byte INS_LDX_IMM    = (byte)0xA2;
    public static final byte INS_LDX_ZP     = (byte)0xA6;
    public static final byte INS_LDX_ZPY    = (byte)0xB6;
    public static final byte INS_LDX_ABS    = (byte)0xAE;
    public static final byte INS_LDX_ABY    = (byte)0xBE;

    /**
     * LDY Instructions
     */
    public static final byte INS_LDY_IMM    = (byte)0xA0;
    public static final byte INS_LDY_ZP     = (byte)0xA4;
    public static final byte INS_LDY_ZPX    = (byte)0xB4;
    public static final byte INS_LDY_ABS    = (byte)0xAC;
    public static final byte INS_LDY_ABX    = (byte)0xBC;

    /**
     * LSR Instructions
     */
    //TODO LSR Instructions

    /**
     * NOP Instructions
     */
    public static final byte INS_NOP_IMP    = (byte)0xEA;

    /**
     * ORA Instructions
     */
    public static final byte INS_ORA_IMM    = (byte)0x09;
    public static final byte INS_ORA_ZP     = (byte)0x05;
    public static final byte INS_ORA_ZPX    = (byte)0x15;
    public static final byte INS_ORA_ABS    = (byte)0x0D;
    public static final byte INS_ORA_ABX    = (byte)0x1D;
    public static final byte INS_ORA_ABY    = (byte)0x19;
    public static final byte INS_ORA_INX    = (byte)0x01;
    public static final byte INS_ORA_INY    = (byte)0x11;


    /**
     * PHA Instructions
     */
    public static final byte INS_PHA_IMP    = (byte)0x48;

    /**
     * PHP Instructions
     */
    //TODO PHP Instructions

    /**
     * PLA Instructions
     */
    public static final byte INS_PLA_IMP    = (byte)0x68;

    /**
     * PLP Instructions
     */
    //TODO PLP Instructions

    /**
     * ROL Instructions
     */
    //TODO ROL Instructions

    /**
     * ROR Instructions
     */
    //TODO ROR Instructions

    /**
     * RTI Instructions
     */
    public static final byte INS_RTI_IMP    = (byte)0x40;

    /**
     * RTS Instructions
     */
    public static final byte INS_RTS_IMP    = (byte)0x60;

    /**
     * SBC Instructions
     */
    //TODO SBC Instructions

    /**
     * SEC Instructions
     */
    public static final byte INS_SEC_IMP    = (byte)0x38;

    /**
     * SED Instructions
     */
    public static final byte INS_SED_IMP    = (byte)0xF8;

    /**
     * SEI Instructions
     */
    public static final byte INS_SEI_IMP    = (byte)0x78;

    /**
     * STA Instructions
     */
    public static final byte INS_STA_ZP    = (byte)0x85;
    public static final byte INS_STA_ZPX   = (byte)0x95;
    public static final byte INS_STA_ABS   = (byte)0x8D;
    public static final byte INS_STA_ABX   = (byte)0x9D;
    public static final byte INS_STA_ABY   = (byte)0x99;
    public static final byte INS_STA_INX   = (byte)0x81;
    public static final byte INS_STA_INY   = (byte)0x91;

    /**
     * STX Instructions
     */
    public static final byte INS_STX_ZP    = (byte)0x86;
    public static final byte INS_STX_ZPY   = (byte)0x96;
    public static final byte INS_STX_ABS   = (byte)0x8E;

    /**
     * STY Instructions
     */
    public static final byte INS_STY_ZP    = (byte)0x84;
    public static final byte INS_STY_ZPX   = (byte)0x94;
    public static final byte INS_STY_ABS   = (byte)0x8C;

    /**
     * TAX Instructions
     */
    public static final byte INS_TAX   = (byte)0xAA;

    /**
     * TAY Instructions
     */
    public static final byte INS_TAY   = (byte)0xA8;

    /**
     * TSX Instructions
     */
    public static final byte INS_TSX   = (byte)0xBA;

    /**
     * TXA Instructions
     */
    public static final byte INS_TXA   = (byte)0x8A;

    /**
     * TXS Instructions
     */
    public static final byte INS_TXS   = (byte)0x9A;

    /**
     * TYA Instructions
     */
    public static final byte INS_TYA   = (byte)0x98;
}
