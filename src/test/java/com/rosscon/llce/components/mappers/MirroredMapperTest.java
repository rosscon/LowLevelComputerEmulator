package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests all the functions of a mirrored mapper
 */
public class MirroredMapperTest {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;

    private Flag rwFlagMapper;
    private ReadOnlyMemory mappedRom;
    private IntegerBus internalROMAddressBus;
    private IntegerBus internalROMDataBus;

    private MirroredMapper mapper;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException {

        /*
         * Main bus side of mapper
         */
        this.addressBus = new IntegerBus(16);
        this.dataBus = new IntegerBus(8);
        this.rwFlag = new Flag();

        /*
         * Mapper to ROM side
         */
        this.rwFlagMapper = new Flag();
        this.internalROMAddressBus = new IntegerBus(16);
        this.internalROMDataBus = new IntegerBus(8);

        /*
         * Test ROM
         */
        int[] data = new int[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
        };
        this.mappedRom = new ReadOnlyMemory(internalROMAddressBus, internalROMDataBus, rwFlagMapper,
                0x0000, 0x000F, data);

        this.mapper = new MirroredMapper(this.addressBus, this.dataBus, this.rwFlag,
                this.mappedRom, 0x0000,
                0x1FFF, 0x07FF);
    }

    @Test
    @DisplayName("Mapper should return what is in ROM when address is in original range")
    public void TestMapperWorksNonMirroredAddress() throws InvalidBusDataException, MemoryException, FlagException {

        addressBus.writeDataToBus(0x0001);
        rwFlag.setFlagValue(true);

        assertEquals(0x01, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("Mapper should return what is in ROM when address is a mirror location")
    public void TestMapperWorksMirroredAddress() throws InvalidBusDataException, MemoryException, FlagException {

        addressBus.writeDataToBus(0x0802);
        rwFlag.setFlagValue(true);

        assertEquals(0x02, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("Mapper should return not respond if the address is outside of its range")
    public void TestMapperIgnoreOutOfRangeAddress() throws InvalidBusDataException, MemoryException, FlagException {

        // Need to do an in range one first to populate the data bus with an old value
        addressBus.writeDataToBus(0x0802);
        rwFlag.setFlagValue(true);
        assertEquals(0x02, dataBus.readDataFromBus());

        // Now the out of range
        addressBus.writeDataToBus(0x4003);
        rwFlag.setFlagValue(true);
        assertEquals(0x02, dataBus.readDataFromBus());
    }

}
