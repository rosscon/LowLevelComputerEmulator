package com.rosscon.llce.components.processors.NMOS6502;

public class NMOS6502Instructions {

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
    //TODO BCC Instructions

    /**
     * BCS Instructions
     */
    //TODO BCS Instructions

    /**
     * BEQ Instructions
     */
    //TODO BEQ Instructions

    /**
     * BIT Instructions
     */
    //TODO BIT Instructions

    /**
     * BMI Instructions
     */
    //TODO BMI Instructions

    /**
     * BNE Instructions
     */
    //TODO BNE Instructions

    /**
     * BPL Instructions
     */
    //TODO BPL Instructions

    /**
     * BRK Instructions
     */
    //TODO BRK Instructions

    /**
     * BVC Instructions
     */
    //TODO BVC Instructions

    /**
     * BVS Instructions
     */
    //TODO BVS Instructions

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
    //TODO CMP Instructions

    /**
     * CPX Instructions
     */
    //TODO CPX Instructions

    /**
     * CPY Instructions
     */
    //TODO CPY Instructions

    /**
     * DEC Instructions
     */
    //TODO DEC Instructions

    /**
     * DEX Instructions
     */
    //TODO DEX Instructions

    /**
     * DEY Instructions
     */
    //TODO DEY Instructions

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
    //TODO INX Instructions

    /**
     * INY Instructions
     */
    //TODO INY Instructions

    /**
     * JMP Instructions
     */
    public static final byte INS_JMP_ABS    = (byte)0x4C;
    public static final byte INS_JMP_IND    = (byte)0x6C;


    /**
     * JSR Instructions
     */
    //TODO JSR Instructions

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
    //TODO NOP Instructions

    /**
     * ORA Instructions
     */
    //TODO ORA Instructions

    /**
     * PHA Instructions
     */
    //TODO PHA Instructions

    /**
     * PHP Instructions
     */
    //TODO PHP Instructions

    /**
     * PLA Instructions
     */
    //TODO PLA Instructions

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
    //TODO RTI Instructions

    /**
     * RTS Instructions
     */
    //TODO RTS Instructions

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
    //TODO STA Instructions

    /**
     * STX Instructions
     */
    //TODO STX Instructions

    /**
     * STY Instructions
     */
    //TODO STY Instructions

    /**
     * TAX Instructions
     */
    //TODO TAX Instructions

    /**
     * TAY Instructions
     */
    //TODO TAY Instructions

    /**
     * TSX Instructions
     */
    //TODO TSX Instructions

    /**
     * TXA Instructions
     */
    //TODO TXA Instructions

    /**
     * TXS Instructions
     */
    //TODO TXS Instructions

    /**
     * TYA Instructions
     */
    //TODO TYA Instructions
}
