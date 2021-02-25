package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.FlagValueRW;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests ROR instruction
 */
public class MOS6502TestROR {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;
    Flag nmiFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new Flag();
        nmiFlag = new Flag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                0x0010, 0x00FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiFlag, true);
    }

    @Test
    @DisplayName("ROR Accumulator mode should shift bits left loading and writing to accumulator")
    public void testRORAccumulator() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b000000010,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b00000001, cpu.getRegACC());
    }

    @Test
    @DisplayName("ROR Zero Page mode shift to the left writing to memory")
    public void testRORZeroPage() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ROR_ZP, 0x10
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        // write a value into ram
        addressBus.writeDataToBus(0x0010);
        dataBus.writeDataToBus(0b000000010);
        rwFlag.setFlagValue(FlagValueRW.WRITE);

        clock.tick(5);
        assertEquals(0b000000001, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("ROR should set the accumulator if the result is 0x00")
    public void testRORZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b00000000,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b00000000, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertNotEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ROR should set the carry flag if bit 7 was a 1")
    public void testRORSetCarryFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b10000011,
                MOS6502Instructions.INS_CLC_IMP,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);


        clock.tick(6);
        assertEquals(0b01000001, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ROR should increment by 1 if the carry flag was set")
    public void testRORFromCarryFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b10000011,
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);


        clock.tick(6);
        assertEquals(0b11000001, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
