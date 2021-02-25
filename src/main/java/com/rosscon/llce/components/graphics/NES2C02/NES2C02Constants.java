package com.rosscon.llce.components.graphics.NES2C02;

/**
 * Constants for NES 2C02 PPU
 */
public class NES2C02Constants {

    /**
     * Drawing to Screen
     */
    public static final int GRAPHICS_SCALING = 1;
    public static final int WIDTH_VISIBLE_PIXELS = 256;
    public static final int HEIGHT_VISIBLE_SCANLINES = 240;
    public static final int WIDTH_TOTAL_PIXELS = 320;
    public static final int HEIGHT_TOTAL_SCANLINES = 261;


    /**
     * Color palette of the NES only has 0x40 colours
     * available 0x00 to 3f inclusive. these have been
     * converted to ARGB values using a spreadsheet
     * https://wiki.nesdev.com/w/index.php/PPU_palettes
     */
    public static final int[] PALETTE = new int[] {
            // 0x00 - 0x0F
            0xFF545454,0xFF001E74,0xFF081090,0xFF300088,0xFF440064,0xFF5C0030,0xFF540400,0xFF3C1800,
            0xFF202A00,0xFF083A00,0xFF004000,0xFF003C00,0xFF00323C,0xFF000000,0xFF000000,0xFF000000,

            // 0x10 - 0x1F
            0xFF989698,0xFF084CC4,0xFF3032EC,0xFF5C1EE4,0xFF8814B0,0xFFA01464,0xFF982220,0xFF783C00,
            0xFF545A00,0xFF287200,0xFF087C00,0xFF007628,0xFF006678,0xFF000000,0xFF000000,0xFF000000,

            // 0x20 - 0x2F
            0xFFECEEEC,0xFF4C9AEC,0xFF787CEC,0xFFB062EC,0xFFE454EC,0xFFEC58B4,0xFFEC6A64,0xFFD48820,
            0xFFA0AA00,0xFF74C400,0xFF4CD020,0xFF38CC6C,0xFF38B4CC,0xFF3C3C3C,0xFF000000,0xFF000000,

            // 0x30 - 0x3F
            0xFFECEEEC,0xFFA8CCEC,0xFFBCBCEC,0xFFD4B2EC,0xFFECAEEC,0xFFECAED4,0xFFECB4B0,0xFFE4C490,
            0xFFCCD278,0xFFB4DE78,0xFFA8E290,0xFF98E2B4,0xFFA0D6E4,0xFFA0A2A0,0xFF000000,0xFF000000,
    };

    /**
     * Registers
     * The PPU has 8 memory mapped registers.
     * Registers are mirrored every 8 bytes from 0x2000 to 0x3FFF
     * https://wiki.nesdev.com/w/index.php/PPU_registers
     */
    public static final int REG_PPUCTRL     = 0x00002000;
    public static final int REG_PPUMASK     = 0x00002001;
    public static final int REG_PPUSTATUS   = 0x00002002;
    public static final int REG_OAMADDR     = 0x00002003;
    public static final int REG_OAMDATA     = 0x00002004;
    public static final int REG_PPUSCROLL   = 0x00002005;
    public static final int REG_PPUADDR     = 0x00002006;
    public static final int REG_PPUDATA     = 0x00002007;

    /**
     * Mask and min/max address for registers
     */
    public static final int REG_MINIMUM_ADDRESS = 0x00002000;
    public static final int REG_MAXIMUM_ADDRESS = 0x00003FFF;
    public static final int REG_MASK            = 0x00002007;

    /**
     * Special register for DMA
     */
    public static final int REG_OAMDMA      = 0x00004014;


    /**
     * Exception error messages
     */
    public static final String EX_CPU_NMI =
            "Error interrupting the cpu";

    public static final String EX_PPU_READ_FAIL =
            "Error reading from ppu memory";

    public static final String EX_PPU_WRITE_FAIL =
            "Error writing to ppu memory";
}
