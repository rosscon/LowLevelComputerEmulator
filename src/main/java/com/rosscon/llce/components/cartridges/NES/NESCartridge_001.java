package com.rosscon.llce.components.cartridges.NES;


import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;

/**
 * This is the most basic type of NES cartridge with no bank switching. Only mirroring
 * https://wiki.nesdev.com/w/index.php/NROM
 */
public class NESCartridge_001 extends NESCartridge {

    /**
     * Mirroring mode for nametable, normally set by solder points for thus type of cart
     */
    private NametableMirror nametableMirror;

    /**
     * In mirrored mode when cartridge only has 16KB of PRG data
     */
    private final int MIRRORED_MASK = 0xBFFF;

    /**
     * Mirrored mapper for handling the mirroring
     */
    private MirroredMapper internalRAMMapper;



    public NESCartridge_001(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwRWFlagCPU,
                            IntegerBus ppuAddressBus, IntegerBus ppuDataBus, RWFlag rwRWFlagPPU,
                            byte[] prgROM, byte[] prgRAM, byte[] chrROM,
                            NESNametableMirroring mirroring) throws CartridgeException {
        super(addressBus, dataBus, rwRWFlagCPU, ppuAddressBus, ppuDataBus, rwRWFlagPPU, prgROM, prgRAM, chrROM);

        this.nametableMirror = new NametableMirror(mirroring);

        try {

            if (prgROM.length != 16384 && prgROM.length != this.PRG_ROM_MAX_SIZE)
                throw new CartridgeException(this.EX_INVALID_PRG_ROM_SIZE);

            int prgEnd = (prgROM.length == this.PRG_ROM_MAX_SIZE) ? this.PRG_ROM_END : this.MIRRORED_MASK;

            /*
             * Convert program rom to int[]
             */
            int[] convertedPrgRom = new int[prgROM.length];
            for (int i = 0; i < prgROM.length; i++){
                convertedPrgRom[i] = prgROM[i] & 0x000000FF;
            }

            ReadOnlyMemory programROM = new ReadOnlyMemory(
                    new IntegerBus(16), new IntegerBus(8),
                    new RWFlag(), this.PRG_ROM_START, prgEnd, convertedPrgRom );

            internalRAMMapper = new MirroredMapper(this.addressBus, this.dataBus, rwRWFlagCPU, programROM,
                    this.PRG_ROM_START, this.PRG_ROM_END, prgEnd);


            int[] convertedCharacterRom = new int[chrROM.length];
            for (int i = 0; i < chrROM.length; i++) {
                convertedCharacterRom[i] = chrROM[i] & 0x000000FF;
            }

            ReadOnlyMemory characterRom = new ReadOnlyMemory(ppuAddressBus,
                    ppuDataBus, rwRWFlagPPU, 0x0000, 0x1FFF, convertedCharacterRom);


        } catch (MemoryException | InvalidBusWidthException ex) {
            throw new CartridgeException(ex.getMessage());
        }
    }

    @Override
    public void onFlagChange(Flag Flag) throws FlagException {
        // This is a very simple mapper so relies on the MirroredMapper to handle the onFlagChange
    }

    /**
     * Gets the nametable mapper mode of the cartridge (Horizontal / Vertical)
     * @return Nametable
     */
    @Override
    public NametableMirror getNametableMirror() {
        return nametableMirror;
    }
}
