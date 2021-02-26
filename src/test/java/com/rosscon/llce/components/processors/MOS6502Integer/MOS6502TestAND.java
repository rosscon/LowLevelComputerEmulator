package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.HaltFlag;
import com.rosscon.llce.components.flags.NMIFlag;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.*;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MOS6502TestAND {

    IntegerBus addressBus;
    IntegerBus dataBus;
    RWFlag rwFlag;
    NMIFlag nmiRWFlag;
    HaltFlag haltFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new RWFlag();
        nmiRWFlag = new NMIFlag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiRWFlag, haltFlag, true);
    }

    @Test
    @DisplayName("AND Immediate Mode logical AND the value in memory and the accumulator")
    public void testANDImmediateMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xFF,
                MOS6502Instructions.INS_AND_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0x42, cpu.getRegACC());
        Assertions.assertEquals(0x00, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(0x00, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("AND Immediate Mode logical AND the value in memory and the accumulator becomes zero should ser zero flag")
    public void testANDImmediateModeZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xF0,
                MOS6502Instructions.INS_AND_IMM, 0x0F
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0x00, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(0x00, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("AND Immediate Mode logical AND the value in memory and the accumulator has bit 7 set should set the negative flag")
    public void testANDImmediateModeNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0xF0,
                MOS6502Instructions.INS_AND_IMM, 0xFF
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0xF0, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
