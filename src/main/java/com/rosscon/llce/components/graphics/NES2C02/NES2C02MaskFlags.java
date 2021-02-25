package com.rosscon.llce.components.graphics.NES2C02;

/**
 * Flags for the ppu mask register
 */
public class NES2C02MaskFlags {

    public static final int GREYSCALE                   = 0b00000001;
    public static final int SHOW_BACKGROUND_LEFTMOST    = 0b00000010;
    public static final int SHOW_SPRITES_LEFTMOST       = 0b00000100;
    public static final int SHOW_BACKGROUND             = 0b00001000;
    public static final int SHOW_SPRITES                = 0b00010000;
    public static final int EMPHASIZE_RED               = 0b00100000;
    public static final int EMPHASIZE_GREEN             = 0b01000000;
    public static final int EMPHASIZE_BLUE              = 0b10000000;
}
