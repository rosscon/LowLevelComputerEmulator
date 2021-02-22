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

/**
 * Tests the functionality of the ORA instruction
 */
public class MOS6502TestORA {

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
    @DisplayName("ORA should populate the accumulator with an OR'd value")
    public void testORA() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b10101010,
                MOS6502Instructions.INS_ORA_IMM, 0b00001111
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0b10101111, cpu.getRegACC());
    }

    @Test
    @DisplayName("ORA should populate the accumulator with an OR'd value and set the negative flag on bit 7")
    public void testORANegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b10101010,
                MOS6502Instructions.INS_ORA_IMM, 0b00001111
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0b10101111, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ORA should populate the accumulator with an OR'd value and set the zero flag on 0x00")
    public void testORAZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_ORA_IMM, 0x00
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0x00, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }


}
