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
 * Tests the INX and INY instructions
 */
public class MOS6502TestIN_X_Y {

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
    @DisplayName("INX should increase the X register by 1")
    public void testINX() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x43, cpu.getRegX());
    }

    @Test
    @DisplayName("INY should increase the Y register by 1")
    public void testINY() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x43, cpu.getRegY());
    }

    @Test
    @DisplayName("INX should increase the X register by 1 and set the 0 flag when register becomes 0")
    public void testINXZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0xFF,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x00, cpu.getRegX());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("INX should increase the X register by 1 and set the negative flag if bit 7 is set")
    public void testINXNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x7F,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x80, cpu.getRegX());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("INY should increase the Y register by 1 and set the 0 flag when register becomes 0")
    public void testINYZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0xFF,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x00, cpu.getRegY());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("INY should increase the Y register by 1 and set the negative flag if bit 7 is set")
    public void testINYNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x7F,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(5);
        assertEquals(0x80, cpu.getRegY());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
