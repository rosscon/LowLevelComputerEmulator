package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.*;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Flags;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the PHA and PLA instructions
 */
public class MOS6502TestPHA_PLA {

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
                0x0110, 0x02FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, true);
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it")
    public void testPHAPLA() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(7);
        assertEquals(0x00, cpu.getRegACC());
        clock.tick(4);
        assertEquals(0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it, PLA should set the negative flag if bit 7 set")
    public void testPLANegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xFF,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(7);
        assertEquals(0x00, cpu.getRegACC());
        clock.tick(4);
        assertEquals(0xFF, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it, PLA should set the zero flag if accumulator == 0")
    public void testPLAZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, 0xF0,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(7);
        assertEquals(0xF0, cpu.getRegACC());
        clock.tick(4);
        assertEquals(0x00, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }
}
