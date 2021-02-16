package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the PHA and PLA instructions
 */
public class MOS6502TestPHA_PLA {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

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

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                new byte[] {0x01, 0x00}, new byte[] { 0x02, (byte)0xFF});
        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag);
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it")
    public void testPHAPLA() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, 0x42,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(7);
        assertEquals((byte)0x00, cpu.getRegACC());
        clock.tick(4);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it, PLA should set the negative flag if bit 7 set")
    public void testPLANegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0xFF,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, 0x00,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(7);
        assertEquals((byte)0x00, cpu.getRegACC());
        clock.tick(4);
        assertEquals((byte)0xFF, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("PHA should put the value of the accumulator on the stack and PLA should read it, PLA should set the zero flag if accumulator == 0")
    public void testPLAZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0x00,
                MOS6502Instructions.INS_PHA_IMP,
                MOS6502Instructions.INS_LDA_IMM, (byte)0xF0,
                MOS6502Instructions.INS_PLA_IMP
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(7);
        assertEquals((byte)0xF0, cpu.getRegACC());
        clock.tick(4);
        assertEquals((byte)0x00, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }
}
