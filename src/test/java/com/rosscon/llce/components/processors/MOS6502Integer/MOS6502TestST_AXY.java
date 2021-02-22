package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.memory.*;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for STA, STX & STY instructions
 */
public class MOS6502TestST_AXY {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new Flag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                0x0010, 0x02FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, true);
    }

    @Test
    @DisplayName("The contents of the accumulator should be stored in memory when STA called")
    public void testSTAStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_STA_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(6);
        addressBus.writeDataToBus(0x0100);
        dataBus.writeDataToBus(0x09);
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("The contents of the X register should be stored in memory when STX called")
    public void testSTXStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_STX_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(6);
        addressBus.writeDataToBus(0x0100);
        dataBus.writeDataToBus(0x09);
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("The contents of the Y register should be stored in memory when STA called")
    public void testSTYStoresAccumulatorToMemory() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42,
                MOS6502Instructions.INS_STY_ABS, 0x00, 0x01
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(6);
        addressBus.writeDataToBus(0x0100);
        dataBus.writeDataToBus(0x09);
        rwFlag.setFlagValue(true);
        assertEquals(0x42, dataBus.readDataFromBus());
    }
}
