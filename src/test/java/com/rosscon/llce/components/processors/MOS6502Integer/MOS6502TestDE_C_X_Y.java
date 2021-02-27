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

/**
 * Test the decrement functions DEX DEX DEY
 */
public class MOS6502TestDE_C_X_Y {

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
                0x0010, 0x02FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiRWFlag, haltFlag, true);
    }

    @Test
    @DisplayName("DEC should reduce the value at a given memory address")
    public void testDEC() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(0x0201);
        dataBus.writeDataToBus(0x42);
        rwFlag.setFlagValue(RWFlag.WRITE);

        clock.tick(6);
        addressBus.writeDataToBus(0x0201);
        rwFlag.setFlagValue(RWFlag.READ);

        assertEquals(0x41, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("DEC should set the zero flag if the result is zero")
    public void testDECZeroFlag() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(0x0201);
        dataBus.writeDataToBus(0x01);
        rwFlag.setFlagValue(RWFlag.WRITE);

        clock.tick(6);
        addressBus.writeDataToBus(0x0201);
        rwFlag.setFlagValue(RWFlag.READ);

        // Check decrement
        assertEquals(0x00, dataBus.readDataFromBus());

        //Check zero flag
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEC should set the negative flag if the result has bit 7 set")
    public void testDECNegativeFlag() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(0x0201);
        dataBus.writeDataToBus(0xF1);
        rwFlag.setFlagValue(RWFlag.WRITE);

        clock.tick(6);
        addressBus.writeDataToBus(0x0201);
        rwFlag.setFlagValue(RWFlag.READ);

        // Check decrement
        assertEquals(0xF0, dataBus.readDataFromBus());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEX() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x45, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(2);

        // Check decrement
        assertEquals(0x45, cpu.getRegX());
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1 and set the zero flag")
    public void testDEXZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);

        // Check decrement
        assertEquals(0x00, cpu.getRegX());

        //Check negative flag
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEXNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x00, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);

        // Check decrement
        assertEquals(0xFF, cpu.getRegX());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }


    @Test
    @DisplayName("DEY should decrement the value of Y register by 1")
    public void testDEY() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x45, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);

        // Check decrement
        assertEquals(0x44, cpu.getRegY());
    }

    @Test
    @DisplayName("DEY should decrement the value of Y register by 1 and set the zero flag")
    public void testDEYZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x01, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);

        // Check decrement
        assertEquals(0x00, cpu.getRegY());

        //Check negative flag
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEYNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x00, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testDECRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);

        // Check decrement
        assertEquals(0xFF, cpu.getRegY());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

}
