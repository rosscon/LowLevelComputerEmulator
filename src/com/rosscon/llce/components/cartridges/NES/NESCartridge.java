package com.rosscon.llce.components.cartridges.NES;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.cartridges.Cartridge;
import com.rosscon.llce.components.flags.Flag;

/**
 * An abstract NES cartridge extends the Cartridge class.
 * NES cartridges have connections to both the main bus and the PPU bus
 *
 * On the Main bus NES cartridges sit in the address space 0x4020 - 0xFFFF, attached to the PRG ROM/RAM
 * On the PPU bus 0x0000 - 0x1FFF attached to the CHR ROM/RAM
 */
public abstract class NESCartridge extends Cartridge {

    /**
     * PPU Address Bus
     */
    protected Bus ppuAddressBus;

    /**
     * PPU Data Bus
     */
    protected Bus ppuDataBus;

    /**
     * First address of PRG ROM
     */
    protected final byte[] PRG_ROM_START = new byte[]{ (byte)0x80, (byte)0x00};

    /**
     * Last address of PRG ROM
     */
    protected final byte[] PRG_ROM_END = new byte[]{ (byte)0xFF, (byte)0xFF};

    /**
     * Maximum (Addressable) size for PRG ROM, May be larger with bank switching
     */
    protected final int PRG_ROM_MAX_SIZE = 32768;

    /**
     * Error message for when provided PRG ROM is invalid
     */
    protected final String EX_INVALID_PRG_ROM_SIZE =
            "Provided PRG ROM has an invalid size";

    /**
     * First address of CHR ROM
     */
    protected final byte[] CHR_ROM_START = new byte[]{ (byte)0x00, (byte)0x00};

    /**
     * Last address of CHR_ROM
     */
    protected final byte[] CHR_ROM_END = new byte[]{ (byte)0x01, (byte)0xFF};



    /**
     * Base constructor for an NES cartridge
     * @param addressBus Main CPU address bus
     * @param dataBus Main CPU data bus
     * @param rwFlagCPU R/W Flag for the main CPU bus
     * @param ppuAddressBus PPU Address bus
     * @param ppuDataBus PPU Data Bus
     * @param rwFlagPPU R/W Flag for PPU bus
     * @param prgROM Program rom as byte array
     * @param prgRAM Program RAM ad byte array
     * @param chrROM Character ROM
     */
    public NESCartridge(Bus addressBus, Bus dataBus, Flag rwFlagCPU,
                        Bus ppuAddressBus, Bus ppuDataBus, Flag rwFlagPPU,
                        byte[] prgROM, byte[] prgRAM, byte[] chrROM) {
        super(addressBus, dataBus, rwFlagCPU);
        this.ppuAddressBus = ppuAddressBus;
        this.ppuDataBus = ppuDataBus;
    }
}
