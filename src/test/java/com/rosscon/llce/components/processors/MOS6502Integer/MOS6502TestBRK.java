package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.*;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the BRK instruction is functioning correctly
 */
public class MOS6502TestBRK {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new Flag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFF, new int[]{0, 0, 0x34, 0x12});
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, true);
    }

    @Test
    @DisplayName("BRK should cause the PC and status to be pushed to the stack, then set the PC to the interrupt vector stored at 0xFFFE/F")
    public void testBRK() throws MemoryException, ClockException {

        ReadOnlyMemory testBRKRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x00001, new int[]{MOS6502Instructions.INS_BRK_IMP , 0x000});

        clock.tick(7);
        Assertions.assertEquals(MOS6502Flags.BREAK_COMMAND, (byte)(cpu.getRegSP() & MOS6502Flags.BREAK_COMMAND));
        assertEquals(0x1234, cpu.getRegPC());
    }
}
