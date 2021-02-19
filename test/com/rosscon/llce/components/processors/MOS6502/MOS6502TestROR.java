package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests ROR instruction
 */
public class MOS6502TestROR {

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

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                new byte[] {0x00, 0x10}, new byte[] { 0x00, (byte)0xFF});

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag);
    }

    @Test
    @DisplayName("ROR Accumulator mode should shift bits left loading and writing to accumulator")
    public void testRORAccumulator() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0b000000010,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);


        clock.tick(4);
        assertEquals((byte)0b00000001, cpu.getRegACC());
    }

    @Test
    @DisplayName("ROR Zero Page mode shift to the left writing to memory")
    public void testRORZeroPage() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ROR_ZP, 0x10
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        // write a value into ram
        addressBus.writeDataToBus(new byte[]{(byte)0x00, (byte)0x10});
        dataBus.writeDataToBus(new byte[]{(byte)0b000000010});
        rwFlag.setFlagValue(false);

        clock.tick(5);
        assertEquals((byte)0b000000001, dataBus.readDataFromBus()[0]);
    }

    @Test
    @DisplayName("ROR should set the accumulator if the result is 0x00")
    public void testRORZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0b00000000,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);


        clock.tick(4);
        assertEquals((byte)0b00000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
        assertNotEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ROR should set the carry flag if bit 7 was a 1")
    public void testRORSetCarryFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0b10000011,
                MOS6502Instructions.INS_CLC_IMP,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);


        clock.tick(6);
        assertEquals((byte)0b01000001, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
        assertNotEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ROR should increment by 1 if the carry flag was set")
    public void testRORFromCarryFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0b10000011,
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_ROR_ACC
        };

        ReadOnlyMemory testRORRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);


        clock.tick(6);
        assertEquals((byte)0b11000001, cpu.getRegACC());
        assertNotEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
        assertEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }
}
