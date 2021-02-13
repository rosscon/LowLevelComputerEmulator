package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.flags.Flag;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests all functions of Random Access Memory
 */
public class RandomAccessMemoryTest {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException {

        addressBus = new Bus(16);
        dataBus = new Bus(8);
        rwFlag = new Flag();
        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                new byte[] {0x00, 0x00}, new byte[] { 0x00, (byte)0xFF});
    }

    @Test
    @DisplayName("RAM should initialise as all zero bytes")
    public void TestRAMInit() throws InvalidBusDataException, MemoryException {

        addressBus.writeDataToBus(new byte[]{0x00, 0x01});
        rwFlag.setFlagValue(false);
        assertEquals(0x00, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("RAM should be able to read and write")
    public void TestRAMReadWrite() throws InvalidBusDataException, MemoryException {

        dataBus.writeDataToBus(new byte[] { 0x42 });
        addressBus.writeDataToBus(new byte[]{0x00, 0x00});
        rwFlag.setFlagValue(false);
        addressBus.writeDataToBus(new byte[]{0x00, 0x01});
        rwFlag.setFlagValue(true);

        assertEquals(0x00, dataBus.readDataFromBus()[0]);
        addressBus.writeDataToBus(new byte[]{0x00, 0x00});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("RAM should not write anything to the data bus when address requested outside its range")
    public void TestRAMReadOutOfRange() throws InvalidBusDataException, MemoryException {

        dataBus.writeDataToBus(new byte[] { 0x42 });
        addressBus.writeDataToBus(new byte[]{0x00, 0x00});
        rwFlag.setFlagValue(false);
        dataBus.writeDataToBus(new byte[] { 0x43 });
        addressBus.writeDataToBus(new byte[]{0x01, 0x00});
        rwFlag.setFlagValue(true);

        assertEquals(0x43, dataBus.readDataFromBus()[0]);
    }
}
