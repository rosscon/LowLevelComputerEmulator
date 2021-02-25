package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test all the transfer between registers functions
 */
public class MOS6502TestT_AX_AY_SX_XA_XS_YA {

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
    @DisplayName("The contents of the accumulator should be copied to X register")
    public void testTAX() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_TAX
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegX());
    }

    @Test
    @DisplayName("The contents of the accumulator should be copied to Y register")
    public void testTAY() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_TAY
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegY());
    }

    @Test
    @DisplayName("The contents of the stack pointer should be copied to X register")
    public void testTSX() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_TSX, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        assertEquals(0xFF, cpu.getRegX());
    }

    @Test
    @DisplayName("The contents of the x register should be copied to the accumulator")
    public void testTXA() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_TXA
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("The contents of the x register should be copied to the stack pointer")
    public void testTXS() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_TXS
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegSP());
    }

    @Test
    @DisplayName("The contents of the x register should be copied to the accumulator")
    public void testTYA() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42,
                MOS6502Instructions.INS_TYA
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegACC());
    }
}
