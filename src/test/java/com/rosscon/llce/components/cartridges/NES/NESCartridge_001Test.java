package com.rosscon.llce.components.cartridges.NES;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.FlagValueRW;
import com.rosscon.llce.components.memory.MemoryException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NESCartridge_001Test {


    IntegerBus addressBus32;
    IntegerBus dataBus32;

    IntegerBus addressBus16;
    IntegerBus dataBus16;

    Flag rwFlag;

    NESCartridge_001 cart32; // 32KB cart
    NESCartridge_001 cart16; // 16KB cart (should be mirrored)

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, CartridgeException {

        /*
         * Main bus side of mapper
         */
        this.addressBus32 = new IntegerBus(16);
        this.dataBus32 = new IntegerBus(8);
        this.addressBus16 = new IntegerBus(16);
        this.dataBus16 = new IntegerBus(8);
        this.rwFlag = new Flag();

        byte[] prg_rom32 = new byte[32768];
        prg_rom32[0]        = 0x42;
        prg_rom32[16383]    = 0x43;
        prg_rom32[16384]    = 0x44;
        prg_rom32[32767]    = 0x45;

        byte[] chr_rom = new byte[8192];

        cart32 = new NESCartridge_001(addressBus32,
                dataBus32,
                rwFlag,
                new IntegerBus(16),
                new IntegerBus(8),
                new Flag(),
                prg_rom32,
                new byte[]{},
                chr_rom,
                NESNametableMirroring.HORIZONTAL);

        byte[] prg_rom16 = new byte[16384];
        prg_rom16[0]        = 0x42;
        prg_rom16[16383]    = 0x43;

        cart16 = new NESCartridge_001(addressBus16,
                dataBus16,
                rwFlag,
                new IntegerBus(16),
                new IntegerBus(8),
                new Flag(),
                prg_rom16,
                new byte[]{},
                chr_rom,
                NESNametableMirroring.HORIZONTAL);
    }


    @Test
    @DisplayName("Non mirrored ROM should not perform any mirroring")
    public void TestMapperWorksNonMirroredAddress() throws InvalidBusDataException, MemoryException, FlagException {

        addressBus32.writeDataToBus(0x8000);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x42, dataBus32.readDataFromBus());

        addressBus32.writeDataToBus(0xBFFF);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x43, dataBus32.readDataFromBus());

        addressBus32.writeDataToBus(0xC000);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x44, dataBus32.readDataFromBus());

        addressBus32.writeDataToBus(0xFFFF);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x45, dataBus32.readDataFromBus());
    }

    @Test
    @DisplayName("Mirrored ROM should perform any mirroring")
    public void TestMapperWorksMirroredAddress() throws InvalidBusDataException, MemoryException, FlagException {

        addressBus16.writeDataToBus(0x8000);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x42, dataBus16.readDataFromBus());

        addressBus16.writeDataToBus(0xBFFF);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x43, dataBus16.readDataFromBus());

        addressBus16.writeDataToBus(0xC000);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x42, dataBus16.readDataFromBus());

        addressBus16.writeDataToBus(0xFFFF);
        rwFlag.setFlagValue(FlagValueRW.READ);
        assertEquals(0x43, dataBus16.readDataFromBus());
    }

}
