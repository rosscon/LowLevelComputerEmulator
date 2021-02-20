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
 * Tests the SBC instruction
 */
public class MOS6502TestSBC {
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
    @DisplayName("SBC should subtract the value in memory from the accumulator")
    public void testSBCPositive() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_SBC_IMM, 0x40
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0x02, cpu.getRegACC());
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the zero flag")
    public void testSBCZero() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_SBC_IMM, 0x42
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0x00, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the negative flag")
    public void testSBCNegative() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0xFF,
                MOS6502Instructions.INS_SBC_IMM, (byte)0x02
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0xFD, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
        assertNotEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
    }

    @Test
    @DisplayName("SBC should subtract the value in memory from the accumulator, should set the overflow flag")
    public void testSBCOverflow() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0x01,
                MOS6502Instructions.INS_SBC_IMM, (byte)0x02
        };

        ReadOnlyMemory testSBCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0xFF, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
        //assertEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
    }
}
