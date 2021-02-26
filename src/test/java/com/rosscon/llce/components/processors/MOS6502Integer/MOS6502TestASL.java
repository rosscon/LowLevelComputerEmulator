package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.*;
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
 * Tests the ASL instruction
 */
public class MOS6502TestASL {

    IntegerBus addressBus;
    IntegerBus dataBus;
    RWFlag rwFlag;
    NMIFlag nmiRWFlag;
    HaltFlag haltFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new RWFlag();
        nmiRWFlag = new NMIFlag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                0x0010, 0x00FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiRWFlag, haltFlag, true);
    }

    @Test
    @DisplayName("ASL Accumulator mode should multiply the accumulator by 2")
    public void testASLAccumulator() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b000000010,
                MOS6502Instructions.INS_ASL_ACC
        };

        ReadOnlyMemory testASLRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b00000100, cpu.getRegACC());
    }

    @Test
    @DisplayName("LSR Zero Page mode should divide the value in memory by 2")
    public void testASLZeroPage() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ASL_ZP, 0x10
        };

        ReadOnlyMemory testASLRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        // write a value into ram
        addressBus.writeDataToBus(0x0010);
        dataBus.writeDataToBus(0b000000010);
        rwFlag.setFlagValue(RWFlag.WRITE);

        clock.tick(5);
        assertEquals(0b00000100, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("ASL should set the accumulator if the result is 0x00")
    public void testASLZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b00000000,
                MOS6502Instructions.INS_ASL_ACC
        };

        ReadOnlyMemory testASLRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b00000000, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG);
        assertNotEquals(MOS6502Flags.CARRY_FLAG, cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG);
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG);
    }

    @Test
    @DisplayName("ASL should set the carry flag if bit 7 was a 1")
    public void testASLCarryFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b10000011,
                MOS6502Instructions.INS_ASL_ACC
        };

        ReadOnlyMemory testASLRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b00000110, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG);
        assertEquals(MOS6502Flags.CARRY_FLAG, cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG);
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG);
    }

    @Test
    @DisplayName("ASL should set the negative flag if bit 7 becomes set to 1")
    public void testASLNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDA_IMM, 0b01000000,
                MOS6502Instructions.INS_ASL_ACC
        };

        ReadOnlyMemory testASLRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);


        clock.tick(4);
        assertEquals(0b10000000, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG);
        assertNotEquals(MOS6502Flags.CARRY_FLAG, cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG);
    }
}
