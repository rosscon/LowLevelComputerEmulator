package com.rosscon.llce.components.processors.NMOS6502;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NMOS6502TestJMP {

    Bus addressBus;
    Bus dataBus;
    Flag rwFlag;
    Clock clock;
    NMOS6502 cpu;
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
        cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);
    }

    @Test
    @DisplayName("JMP Absolute mode")
    public void testJMPAbsoluteMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                NMOS6502Instructions.INS_JMP_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(3);
        assertArrayEquals(new byte[]{0x02, 0x01}, cpu.getRegPC());
    }

    @Test
    @DisplayName("JMP Indirect mode")
    public void testJMPIndirectMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                NMOS6502Instructions.INS_JMP_IND, 0x03, 0x00,
                0x05, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(5);
        assertArrayEquals(new byte[]{0x00, 0x05}, cpu.getRegPC());
    }
}
