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

/**
 * Tests for the CMP CPX CPY instructions
 */
public class MOS6502TestCMP_PX_PY {

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
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCMPGT() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x02,
                MOS6502Instructions.INS_CMP_IMM, 0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        Assertions.assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCMPZero() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x02,
                MOS6502Instructions.INS_CMP_IMM, 0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCMPNegative() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x01,
                MOS6502Instructions.INS_CMP_IMM, 0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }



    @Test
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCPXGT() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x02,
                MOS6502Instructions.INS_CPX_IMM, 0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCPXZero() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x02,
                MOS6502Instructions.INS_CPX_IMM, 0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCPXNegative() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01,
                MOS6502Instructions.INS_CPX_IMM, 0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }



    @Test
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCPYGT() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x02,
                MOS6502Instructions.INS_CPY_IMM, 0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCPYZero() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x02,
                MOS6502Instructions.INS_CPY_IMM, 0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCPYNegative() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x01,
                MOS6502Instructions.INS_CPY_IMM, 0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
