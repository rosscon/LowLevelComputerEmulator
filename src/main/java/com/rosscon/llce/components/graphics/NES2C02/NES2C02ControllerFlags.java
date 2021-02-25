package com.rosscon.llce.components.graphics.NES2C02;

/**
 * Flag constants for the controller register
 */
public class NES2C02ControllerFlags {

    public static final int BASE_NAMETABLE_ADDRESS_LOW      = 0b00000001;   // Add 256 to the x scroll position
    public static final int BASE_NAMETABLE_ADDRESS_HIGH     = 0b00000010;   // Add 240 to the y scroll position
    public static final int VRAM_ADDR_INC_PER_RW_PPUDATA    = 0b00000100;
    public static final int SPRITE_PATTERN_ADDRESS          = 0b00001000;
    public static final int BACKGROUND_PATTERN_ADDRESS      = 0b00010000;
    public static final int SPRITE_SIZE                     = 0b00100000;
    public static final int PPU_MASTER_SLAVE_SELECT         = 0b01000000;
    public static final int GENERATE_NMI_START_VBLANK       = 0b10000000;
}
