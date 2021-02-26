package com.rosscon.llce.components.cartridges.NES;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.utils.ByteUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Handles iNES file formats and can parse them into a Cartridge Object
 * https://wiki.nesdev.com/w/index.php/INES
 *
 * iNES format
 * File Format
 *
 * Size (Bytes)             Purpose
 * 16                       Header
 * 0 or 512                 Optional Trainer
 * 16384 x PRG ROM size     PRG ROM data
 * 8192  x CHR ROM size     CHR ROM Data (Can be 0)
 * 0 or 127 or 128          Optional ASCII title
 *
 * Header Format
 * Bytes            Purpose
 * 0 - 3            NES[EOL]
 * 4                PRG ROM size in 16KB units
 * 5                CHR ROM size in 8KB units (0 means CHR RAM used instead)
 * 6                Flags 6
 * 7                Flags 7
 * 8                Flags 8
 * 9                Flags 9
 * 10               Flags 10
 * 11-15            Padding (Not always 0's but can be ignored)
 *
 * Flags
 * Flags 6
 * 76543210
 * ||||||||
 * |||||||+- Mirroring: 0: horizontal (vertical arrangement) (CIRAM A10 = PPU A11)
 * |||||||              1: vertical (horizontal arrangement) (CIRAM A10 = PPU A10)
 * ||||||+-- 1: Cartridge contains battery-backed PRG RAM ($6000-7FFF) or other persistent memory
 * |||||+--- 1: 512-byte trainer at $7000-$71FF (stored before PRG data)
 * ||||+---- 1: Ignore mirroring control or above mirroring bit; instead provide four-screen VRAM
 * ++++----- Lower nybble of mapper number
 *
 * Flags 7
 * 76543210
 * ||||||||
 * |||||||+- VS Unisystem
 * ||||||+-- PlayChoice-10 (8KB of Hint Screen data stored after CHR data)
 * ||||++--- If equal to 2, flags 8-15 are in NES 2.0 format
 * ++++----- Upper nybble of mapper number
 *
 * Flags 8
 * 76543210
 * ||||||||
 * ++++++++- PRG RAM size
 *
 * Flags 9
 * 76543210
 * ||||||||
 * |||||||+- TV system (0: NTSC; 1: PAL)
 * +++++++-- Reserved, set to zero
 *
 * Flags 10
 * 76543210
 *   ||  ||
 *   ||  ++- TV system (0: NTSC; 2: PAL; 1/3: dual compatible)
 *   |+----- PRG RAM ($6000-$7FFF) (0: present; 1: not present)
 *   +------ 0: Board has no bus conflicts; 1: Board has bus conflicts
 *
 * REF: https://wiki.nesdev.com/w/index.php/INES
 */
public class NESCartridgeFactory {

    /**
     * Default start location for PRG ROM
     */
    private static final int PRG_ROM_START = 16;

    /**
     * Size of trainer if it is present
     */
    private static final int TRAINER_SIZE = 512;

    /**
     * Unit size for PRG ROM
     */
    private static final int PRG_ROM_UNITS = 16384;

    /**
     * Unit size for CHR ROM
     */
    private static final int CHR_ROM_UNITS = 8192;


    /*
     * PARSE FLAG 6
     */

    /**
     * Determines the mirroring mode set by the cart (bit 0)
     * unset = horizontal
     * set = vertical
     * @param flag6 flag6
     * @return NESNametableMirroring
     */
    private static NESNametableMirroring getNametableMirroring (byte flag6){
        return (flag6 & 0b00000001) != 0 ? NESNametableMirroring.VERTICAL : NESNametableMirroring.HORIZONTAL;
    }

    /**
     * Determines if the cart has batter backed ram or some kind of persistent memory (bit 1)
     * @param flag6 flag6
     * @return true = set, false if not set
     */
    private static boolean hasBatteryPrgRam(byte flag6){
        return (flag6 & 0b00000010) == 0b00000010;
    }

    /**
     * Determines if the trainer flag is set (bit 2)
     * @param flag6 flag6
     * @return true = set, false if not set
     */
    private static boolean hasTrainer(byte flag6){
        return (flag6 & 0b00000100) == 0b00000100;
    }


    /**
     * Determines the mapper number from flags 6 and 7
     * the 4 MSB's from flag 6 are the 4 LSB's
     * the 4 MSB's from flag 7 are the 4 MSB's
     * @param flag6
     * @param flag7
     * @return
     */
    private static int parseMapperNumber(byte flag6, byte flag7){
        byte result = (byte)((flag6 >>> 4) + (flag7 & 0x11110000));
        return ByteUtils.byteToIntUnsigned(result);
    }


    public static NESCartridge cartridgeFromINESFile (
            String filename,
            IntegerBus cpuAddressBus, IntegerBus cpuDataBus, RWFlag rwRWFlagCPU,
            IntegerBus ppuAddressBus, IntegerBus ppuDataBus, RWFlag rwRWFlagPPU
    ) throws IOException, CartridgeException {

        byte[] array = Files.readAllBytes(Paths.get(filename));

        /*
         * Parse the 16 byte header
         */
        int prgRomSize = array[4];
        int chrRomSize = array[5];

        byte flag6 = array[6];
        byte flag7 = array[7];
        byte flag8 = array[8];
        byte flags = array[9];
        byte flag10 = array[10];


        int prgRomStart = hasTrainer(flag6) ? (PRG_ROM_START + TRAINER_SIZE) : PRG_ROM_START;
        int prgRomEnd   = prgRomStart + (prgRomSize * PRG_ROM_UNITS);

        int chrRomStart = prgRomEnd;
        int chrRomEnd = chrRomStart + (chrRomSize * CHR_ROM_UNITS);

        byte [] prgRom = Arrays.copyOfRange(array, prgRomStart, prgRomEnd);
        byte [] chrRom = Arrays.copyOfRange(array, chrRomStart, chrRomEnd);

        int mapper = parseMapperNumber(flag6, flag7);

        System.out.println("NMI     : " + String.format("%02X", prgRom[prgRom.length - 5]) + String.format("%02X", prgRom[prgRom.length - 6]));
        System.out.println("RST     : " + String.format("%02X", prgRom[prgRom.length - 3]) + String.format("%02X", prgRom[prgRom.length - 4]));
        System.out.println("IRQ/BRK : " + String.format("%02X", prgRom[prgRom.length - 1]) + String.format("%02X", prgRom[prgRom.length - 2]));

        NESNametableMirroring mirroring = getNametableMirroring(flag6);

        if (mapper == 0){
            return new NESCartridge_001(
                    cpuAddressBus, cpuDataBus, rwRWFlagCPU,
                    ppuAddressBus, ppuDataBus, rwRWFlagPPU,
                    prgRom, new byte[]{}, chrRom, mirroring);
        }



        return null;
    }
}
