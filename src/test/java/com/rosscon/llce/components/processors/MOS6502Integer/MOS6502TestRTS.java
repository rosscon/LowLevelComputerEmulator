package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.FlagValueRW;
import com.rosscon.llce.components.memory.*;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for RTS instruction
 */
public class MOS6502TestRTS {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;
    Flag nmiFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new Flag();
        nmiFlag = new Flag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus, rwFlag, 0x0010, 0xFF00);
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiFlag, true);
    }

    @Test
    @DisplayName("RTS should pull the PC from the stack")
    public void testRTS() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_RTS_IMP, 0x00
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        // Writing direct to stack
        addressBus.writeDataToBus(0x0100);
        dataBus.writeDataToBus(0xF0);
        rwFlag.setFlagValue(FlagValueRW.WRITE);

        addressBus.writeDataToBus(0x0101);
        dataBus.writeDataToBus(0x42);
        rwFlag.setFlagValue(FlagValueRW.WRITE);

        clock.tick(6);
        assertEquals(0x01, cpu.getRegSP());
        assertEquals(0x42F1, cpu.getRegPC());
    }
}
