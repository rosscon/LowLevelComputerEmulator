package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Flags;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests the functionality of BIT instruction
 */
public class MOS6502TestBIT {

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
                0xFFFC, 0xFFFD, new int[]{0, 0});
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, true);
    }

    @Test
    @DisplayName("BIT should set the zero flag if result is 0x00, Accumulator should not be changed")
    public void testBITZero() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b11110000,
                MOS6502Instructions.INS_BIT_ZP, 0x04, 0b00001111
        };

        ReadOnlyMemory testBITRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(5);
        assertEquals(0b11110000, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("BIT should set the overflow flag if result is 0x00, Accumulator should not be changed")
    public void testBITOverflow() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xFF,
                MOS6502Instructions.INS_BIT_ZP, 0x04, 0b01000000
        };

        ReadOnlyMemory testBITRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(5);
        assertEquals(0xFF, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("BIT should set the overflow flag if result is 0x00, Accumulator should not be changed")
    public void testBITNegative() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xFF,
                MOS6502Instructions.INS_BIT_ZP, 0x04, 0b10000000
        };

        ReadOnlyMemory testBITRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(5);
        assertEquals(0xFF, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertNotEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
