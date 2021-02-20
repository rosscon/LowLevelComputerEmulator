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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests the functionality of BIT instruction
 */
public class MOS6502TestBIT {

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
    @DisplayName("BIT should set the zero flag if result is 0x00, Accumulator should not be changed")
    public void testBITZero() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0b11110000,
                MOS6502Instructions.INS_BIT_ZP, 0x04, (byte)0b00001111
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(5);
        assertEquals((byte)0b11110000, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("BIT should set the overflow flag if result is 0x00, Accumulator should not be changed")
    public void testBITOverflow() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0xFF,
                MOS6502Instructions.INS_BIT_ZP, 0x04, (byte)0b01000000
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(5);
        assertEquals((byte)0xFF, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("BIT should set the overflow flag if result is 0x00, Accumulator should not be changed")
    public void testBITNegative() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0xFF,
                MOS6502Instructions.INS_BIT_ZP, 0x04, (byte)0b10000000
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(5);
        assertEquals((byte)0xFF, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
        assertNotEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
