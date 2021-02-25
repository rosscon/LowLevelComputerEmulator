package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertEquals;

public class MOS6502TestJMP {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;
    Flag nmiFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new Flag();
        nmiFlag = new Flag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiFlag, true);
    }

    @Test
    @DisplayName("JMP Absolute mode")
    public void testJMPAbsoluteMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_JMP_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(3);
        assertEquals(0x0201, cpu.getRegPC());
    }

    @Test
    @DisplayName("JMP Indirect mode")
    public void testJMPIndirectMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_JMP_IND, 0x03, 0x00,
                0x05, 0x00
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(5);
        assertEquals(0x0005, cpu.getRegPC());
    }
}
