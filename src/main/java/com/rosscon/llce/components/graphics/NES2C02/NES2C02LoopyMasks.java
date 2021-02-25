package com.rosscon.llce.components.graphics.NES2C02;

/**
 * Masks for parts of the Loopy register
 * https://wiki.nesdev.com/w/index.php/PPU_scrolling
 */
public class NES2C02LoopyMasks {

    public static final int COARSE_X    = 0b000000000011111;
    public static final int COARSE_Y    = 0b000001111100000;
    public static final int NAMETABLE   = 0b000110000000000;
    public static final int NAMETABLE_X = 0b000010000000000;
    public static final int NAMETABLE_Y = 0b000100000000000;
    public static final int FINE_Y      = 0b111000000000000;

}
