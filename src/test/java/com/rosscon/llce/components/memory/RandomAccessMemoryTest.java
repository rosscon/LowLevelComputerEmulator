package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlagValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests all functions of Random Access Memory
 */
public class RandomAccessMemoryTest {

    IntegerBus addressBus;
    IntegerBus dataBus;
    RWFlag rwFlag;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new RWFlag();
        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                0x0000, 0x00FF);
    }

    @Test
    @DisplayName("RAM should initialise as all zero bytes")
    public void TestRAMInit() throws InvalidBusDataException, MemoryException, FlagException {

        addressBus.writeDataToBus(0x0001);
        rwFlag.setFlagValue(RWFlag.WRITE);
        assertEquals(0x00, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("RAM should be able to read and write")
    public void TestRAMReadWrite() throws InvalidBusDataException, MemoryException, FlagException {

        dataBus.writeDataToBus(0x42);
        addressBus.writeDataToBus(0x0000);
        rwFlag.setFlagValue(RWFlag.WRITE);
        addressBus.writeDataToBus(0x0001);
        rwFlag.setFlagValue(RWFlag.READ);

        assertEquals(0x00, dataBus.readDataFromBus());
        addressBus.writeDataToBus(0x0000);
        rwFlag.setFlagValue(RWFlag.READ);
        assertEquals(0x42, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("RAM should not write anything to the data bus when address requested outside its range")
    public void TestRAMReadOutOfRange() throws InvalidBusDataException, MemoryException, FlagException {

        dataBus.writeDataToBus(0x42);
        addressBus.writeDataToBus(0x0000);
        rwFlag.setFlagValue(RWFlag.WRITE);
        dataBus.writeDataToBus(0x43);
        addressBus.writeDataToBus(0x0100);
        rwFlag.setFlagValue(RWFlag.READ);

        assertEquals(0x43, dataBus.readDataFromBus());
    }
}
