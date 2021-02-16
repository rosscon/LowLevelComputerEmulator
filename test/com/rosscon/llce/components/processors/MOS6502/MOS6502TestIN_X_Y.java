package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the INX and INY instructions
 */
public class MOS6502TestIN_X_Y {

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
    @DisplayName("INX should increase the X register by 1")
    public void testINX() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x42,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals(0x43, cpu.getRegX());
    }

    @Test
    @DisplayName("INY should increase the Y register by 1")
    public void testINY() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x42,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals(0x43, cpu.getRegY());
    }

    @Test
    @DisplayName("INX should increase the X register by 1 and set the 0 flag when register becomes 0")
    public void testINXZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0xFF,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals(0x00, cpu.getRegX());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("INX should increase the X register by 1 and set the negative flag if bit 7 is set")
    public void testINXNegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte)0x7F,
                MOS6502Instructions.INS_INX_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals((byte)0x80, cpu.getRegX());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("INY should increase the Y register by 1 and set the 0 flag when register becomes 0")
    public void testINYZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0xFF,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals(0x00, cpu.getRegY());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("INY should increase the Y register by 1 and set the negative flag if bit 7 is set")
    public void testINYNegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, (byte)0x7F,
                MOS6502Instructions.INS_INY_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);
        assertEquals((byte)0x80, cpu.getRegY());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }
}
