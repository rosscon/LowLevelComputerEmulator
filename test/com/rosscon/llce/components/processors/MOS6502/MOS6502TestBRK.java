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
 * Tests the BRK instruction is functioning correctly
 */
public class MOS6502TestBRK {

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
    @DisplayName("BRK should cause the PC and status to be pushed to the stack, then set the PC to the interrupt vector stored at 0xFFFE/F")
    public void testBRK() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_BRK_IMP });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ 0x34 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFF }),
                    new byte[]{ 0x12 });
        }};
        ReadOnlyMemory brkROM = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(7);
        assertEquals(MOS6502Flags.BREAK_COMMAND, (byte)(cpu.getRegSP() & MOS6502Flags.BREAK_COMMAND));
        assertArrayEquals(new byte[]{(byte)0x12, (byte)0x34}, cpu.getRegPC());
    }
}
