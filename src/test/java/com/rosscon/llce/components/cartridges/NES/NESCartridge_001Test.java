package com.rosscon.llce.components.cartridges.NES;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.cartridges.NES.NESCartridge_001;
import com.rosscon.llce.components.cartridges.NES.NESNametableMirroring;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.MemoryException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NESCartridge_001Test {


    Bus addressBus32;
    Bus dataBus32;

    Bus addressBus16;
    Bus dataBus16;

    Flag rwFlag;

    NESCartridge_001 cart32; // 32KB cart
    NESCartridge_001 cart16; // 16KB cart (should be mirrored)

    private MirroredMapper mapper;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, CartridgeException {

        /*
         * Main bus side of mapper
         */
        this.addressBus32 = new Bus(16);
        this.dataBus32 = new Bus(8);
        this.addressBus16 = new Bus(16);
        this.dataBus16 = new Bus(8);
        this.rwFlag = new Flag();

        byte[] prg_rom32 = new byte[32768];
        prg_rom32[0]        = 0x42;
        prg_rom32[16383]    = 0x43;
        prg_rom32[16384]    = 0x44;
        prg_rom32[32767]    = 0x45;

        cart32 = new NESCartridge_001(addressBus32,
                dataBus32,
                rwFlag,
                new Bus(16),
                new Bus(8),
                new Flag(),
                prg_rom32,
                new byte[]{},
                new byte[]{},
                NESNametableMirroring.HORIZONTAL);

        byte[] prg_rom16 = new byte[16384];
        prg_rom16[0]        = 0x42;
        prg_rom16[16383]    = 0x43;

        cart16 = new NESCartridge_001(addressBus16,
                dataBus16,
                rwFlag,
                new Bus(16),
                new Bus(8),
                new Flag(),
                prg_rom16,
                new byte[]{},
                new byte[]{},
                NESNametableMirroring.HORIZONTAL);
    }


    @Test
    @DisplayName("Non mirrored ROM should not perform any mirroring")
    public void TestMapperWorksNonMirroredAddress() throws InvalidBusDataException, MemoryException {

        addressBus32.writeDataToBus(new byte[]{(byte)0x80, (byte)0x00});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus32.readDataFromBus()[0]);

        addressBus32.writeDataToBus(new byte[]{(byte)0xBF, (byte)0xFF});
        rwFlag.setFlagValue(true);
        assertEquals(0x43, dataBus32.readDataFromBus()[0]);

        addressBus32.writeDataToBus(new byte[]{(byte)0xC0, (byte)0x00});
        rwFlag.setFlagValue(true);
        assertEquals(0x44, dataBus32.readDataFromBus()[0]);

        addressBus32.writeDataToBus(new byte[]{(byte)0xFF, (byte)0xFF});
        rwFlag.setFlagValue(true);
        assertEquals(0x45, dataBus32.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("Mirrored ROM should perform any mirroring")
    public void TestMapperWorksMirroredAddress() throws InvalidBusDataException, MemoryException {

        addressBus16.writeDataToBus(new byte[]{(byte)0x80, (byte)0x00});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus16.readDataFromBus()[0]);

        addressBus16.writeDataToBus(new byte[]{(byte)0xBF, (byte)0xFF});
        rwFlag.setFlagValue(true);
        assertEquals(0x43, dataBus16.readDataFromBus()[0]);

        addressBus16.writeDataToBus(new byte[]{(byte)0xC0, (byte)0x00});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus16.readDataFromBus()[0]);

        addressBus16.writeDataToBus(new byte[]{(byte)0xFF, (byte)0xFF});
        rwFlag.setFlagValue(true);
        assertEquals(0x43, dataBus16.readDataFromBus()[0]);
    }

}
