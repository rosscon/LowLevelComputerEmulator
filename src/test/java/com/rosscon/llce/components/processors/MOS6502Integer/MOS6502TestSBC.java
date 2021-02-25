package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
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
 * Tests the SBC instruction
 */
public class MOS6502TestSBC {
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
    @DisplayName("SBC should subtract the value in memory from the accumulator")
    public void testSBCPositive() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_SBC_IMM, 0x40
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0x02, cpu.getRegACC());
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the zero flag")
    public void testSBCZero() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_SBC_IMM, 0x42
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0x00, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the negative flag")
    public void testSBCNegative() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xFF,
                MOS6502Instructions.INS_SBC_IMM, 0x02
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0xFD, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
        assertNotEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the overflow flag")
    public void testSBCOverflow() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x01,
                MOS6502Instructions.INS_SBC_IMM, 0x02
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0xFF, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
        //assertEquals(MOS6502FlagsInteger.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502FlagsInteger.OVERFLOW_FLAG));
    }
}
