package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Flags;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * LDA, LDX, LDY are simple instructions so tests grouped together
 */

public class MOS6502TestLD_AXY {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new Bus(16);
        dataBus = new Bus(8);
        rwFlag = new Flag();
        clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ 0x00 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ 0x00 });
        }};

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag);
    }

    @Test
    @DisplayName("LDA Immediate Mode should load the next memory value into the accumulator")
    public void testLDAImmediate() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("LDA Immediate Mode should set the ZERO flag when writing a zero")
    public void testLDAImmediateZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x00, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("LDA Immediate Mode should set the NEGATIVE flag when writing a zero")
    public void testLDAImmediateNegFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0xFF
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0xFF, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("LDX Immediate Mode should load the next memory value into the accumulator")
    public void testLDXImmediate() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x42, cpu.getRegX());
    }

    @Test
    @DisplayName("LDX Immediate Mode should set the ZERO flag when writing a zero")
    public void testLDXImmediateZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x00, cpu.getRegX());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("LDA Immediate Mode should set the NEGATIVE flag when writing a zero")
    public void testLDXImmediateNegFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0xFF
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0xFF, cpu.getRegX());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("LDY Immediate Mode should load the next memory value into the accumulator")
    public void testLDYImmediate() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x42, cpu.getRegY());
    }

    @Test
    @DisplayName("LDY Immediate Mode should set the ZERO flag when writing a zero")
    public void testLDYImmediateZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals(0x00, cpu.getRegY());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("LDA Immediate Mode should set the NEGATIVE flag when writing a zero")
    public void testLDYImmediateNegFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0xFF
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0xFF, cpu.getRegY());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
