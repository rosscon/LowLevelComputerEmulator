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
 * Tests for the CMP CPX CPY instructions
 */
public class MOS6502TestCMP_PX_PY {

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
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCMPGT() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0x02,
                MOS6502Instructions.INS_CMP_IMM, (byte)0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        Assertions.assertEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCMPZero() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0x02,
                MOS6502Instructions.INS_CMP_IMM, (byte)0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCMPNegative() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDA_IMM, (byte)0x01,
                MOS6502Instructions.INS_CMP_IMM, (byte)0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }



    @Test
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCPXGT() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0x02,
                MOS6502Instructions.INS_CPX_IMM, (byte)0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCPXZero() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0x02,
                MOS6502Instructions.INS_CPX_IMM, (byte)0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCPXNegative() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0x01,
                MOS6502Instructions.INS_CPX_IMM, (byte)0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }



    @Test
    @DisplayName("CMP should set the carry flag if acc > value")
    public void testCPYGT() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0x02,
                MOS6502Instructions.INS_CPY_IMM, (byte)0x01
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.CARRY_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("CMP should set the zero flag if acc == value")
    public void testCPYZero() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0x02,
                MOS6502Instructions.INS_CPY_IMM, (byte)0x02
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("CMP should set the negative flag if bit 7 would be set")
    public void testCPYNegative() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0x01,
                MOS6502Instructions.INS_CPY_IMM, (byte)0x04
        };

        ReadOnlyMemory testRTSRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() &MOS6502Flags.NEGATIVE_FLAG));
    }
}
