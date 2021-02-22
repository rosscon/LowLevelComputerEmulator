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
 * Since all the set and clear flag commands are so simple have grouped together.
 */
public class MOS6502TestSE_CL {

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
    @DisplayName("SEC and CLC")
    public void testSECandCLC() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_CLC_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        Assertions.assertEquals((cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG), MOS6502Flags.CARRY_FLAG);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG), MOS6502Flags.CARRY_FLAG);
    }

    @Test
    @DisplayName("SED and CLD")
    public void testSEDandCLD() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_SED_IMP,
                MOS6502Instructions.INS_CLD_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.DECIMAL_MODE), MOS6502Flags.DECIMAL_MODE);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.DECIMAL_MODE), MOS6502Flags.DECIMAL_MODE);
    }

    @Test
    @DisplayName("SEI and CLI")
    public void testSEIandCLI() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_SEI_IMP,
                MOS6502Instructions.INS_CLI_IMP
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
    }

    @Test
    @DisplayName("CLV")
    public void testCLV() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_CLV_IMP,
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(4);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG), MOS6502Flags.OVERFLOW_FLAG);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
    }
}
