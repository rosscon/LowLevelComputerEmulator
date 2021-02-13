package com.rosscon.llce.components.cartridges.NES;


import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;

/**
 * This is the most basic type of NES cartridge with no bank switching. Only mirroring
 * https://wiki.nesdev.com/w/index.php/NROM
 */
public class NESCartridge_001 extends NESCartridge {

    /**
     * Mirroring mode for nametable, normally set by solder points for thus type of cart
     */
    private NESNametableMirroring mirroring;

    /**
     * In mirrored mode when cartridge only has 16KB of PRG data
     */
    private final byte[] MIRRORED_MASK = new byte[] { (byte)0xBF, (byte)0xFF };

    /**
     * Mirrored mapper for handling the mirroring
     */
    private MirroredMapper internalRAMMapper;



    public NESCartridge_001(Bus addressBus, Bus dataBus, Flag rwFlagCPU,
                            Bus ppuAddressBus, Bus ppuDataBus, Flag rwFlagPPU,
                            byte[] prgROM, byte[] prgRAM, byte[] chrROM,
                            NESNametableMirroring mirroring) throws CartridgeException {
        super(addressBus, dataBus, rwFlagCPU, ppuAddressBus, ppuDataBus, rwFlagPPU, prgROM, prgRAM, chrROM);

        this.mirroring = mirroring;

        try {

            if (prgROM.length != 16384 && prgROM.length != this.PRG_ROM_MAX_SIZE)
                throw new CartridgeException(this.EX_INVALID_PRG_ROM_SIZE);

            byte[] prgEnd = (prgROM.length == this.PRG_ROM_MAX_SIZE) ? this.PRG_ROM_END : this.MIRRORED_MASK;

            ReadOnlyMemory programROM = new ReadOnlyMemory(new Bus(16), new Bus(8),
                    new Flag(), this.PRG_ROM_START, prgEnd, prgROM );

            internalRAMMapper = new MirroredMapper(this.addressBus, this.dataBus, rwFlagCPU, programROM,
                    this.PRG_ROM_START, this.PRG_ROM_END, prgEnd);

        } catch (MemoryException | InvalidBusWidthException ex) {
            throw new CartridgeException(ex.getMessage());
        }
    }

    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {
        // This is a very simple mapper so relies on the MirroredMapper to handle the onFlagChange
    }
}
