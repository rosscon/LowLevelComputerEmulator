package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for STA, STX & STY instructions
 */
public class MOS6502TestST_AXY {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new Bus(16);
        dataBus = new Bus(8);
        rwFlag = new Flag();
        clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ 0x00 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ 0x00 });
        }};

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag);

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                new byte[] {0x01, 0x00}, new byte[] { 0x01, (byte)0x0F});
    }

    @Test
    @DisplayName("The contents of the accumulator should be stored in memory when STA called")
    public void testSTAStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_STA_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{0x01, 0x00});
        dataBus.writeDataToBus(new byte[]{0x09});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("The contents of the X register should be stored in memory when STX called")
    public void testSTXStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_STX_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{0x01, 0x00});
        dataBus.writeDataToBus(new byte[]{0x09});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("The contents of the Y register should be stored in memory when STA called")
    public void testSTYStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42,
                MOS6502Instructions.INS_STY_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{0x01, 0x00});
        dataBus.writeDataToBus(new byte[]{0x09});
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus()[0]);
    }
}
