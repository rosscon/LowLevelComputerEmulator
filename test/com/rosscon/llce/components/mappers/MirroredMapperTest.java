package com.rosscon.llce.components.mappers;


import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.NMOS6502.NMOS6502Instructions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests all the functions of a mirrored mapper
 */
public class MirroredMapperTest {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;

    private Flag rwFlagMapper;
    private ReadOnlyMemory mappedRom;
    private Bus internalROMAddressBus;
    private Bus internalROMDataBus;

    private MirroredMapper mapper;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException {

        /*
         * Main bus side of mapper
         */
        this.addressBus = new Bus(16);
        this.dataBus = new Bus(8);
        this.rwFlag = new Flag();

        /*
         * Mapper to ROM side
         */
        this.rwFlagMapper = new Flag();
        this.internalROMAddressBus = new Bus(16);
        this.internalROMDataBus = new Bus(8);

        /*
         * Test ROM
         */
        byte[] data = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
        };
        this.mappedRom = new ReadOnlyMemory(internalROMAddressBus, internalROMDataBus, rwFlagMapper,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x0F}, data);

        this.mapper = new MirroredMapper(this.addressBus, this.dataBus, this.rwFlag,
                this.mappedRom, new byte[] {0x00, 0x00},
                new byte[] { 0x1F, (byte)0xFF}, new byte[] { 0x07, (byte)0xFF});
    }

    @Test
    @DisplayName("Mapper should return what is in ROM when address is in original range")
    public void TestMapperWorksNonMirroredAddress() throws InvalidBusDataException, MemoryException {

        addressBus.writeDataToBus(new byte[]{0x00, 0x01});
        rwFlag.setFlagValue(true);

        assertEquals(0x01, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("Mapper should return what is in ROM when address is a mirror location")
    public void TestMapperWorksMirroredAddress() throws InvalidBusDataException, MemoryException {

        addressBus.writeDataToBus(new byte[]{0x08, 0x02});
        rwFlag.setFlagValue(true);

        assertEquals(0x02, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("Mapper should return not respond if the address is outside of its range")
    public void TestMapperIgnoreOutOfRangeAddress() throws InvalidBusDataException, MemoryException {

        // Need to do an in range one first to populate the data bus with an old value
        addressBus.writeDataToBus(new byte[]{0x08, 0x02});
        rwFlag.setFlagValue(true);
        assertEquals(0x02, dataBus.readDataFromBus()[0]);

        // Now the out of range
        addressBus.writeDataToBus(new byte[]{0x40, 0x03});
        rwFlag.setFlagValue(true);
        assertEquals(0x02, dataBus.readDataFromBus()[0]);
    }

}
