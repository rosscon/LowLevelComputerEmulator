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
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
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
 * Tests the RTI function
 */
public class MOS6502TestRTI {

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
    @DisplayName("RTI should pull the processor flags and mask them followed by PC from the stack")
    public void testRTI() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_RTI_IMP
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x00}, data);

        // Writing direct to stack
        addressBus.writeDataToBus(new byte[]{(byte)0x01, (byte)0x00});
        dataBus.writeDataToBus(new byte[]{(byte) 0xFF});
        rwFlag.setFlagValue(false);

        addressBus.writeDataToBus(new byte[]{(byte)0x01, (byte)0x01});
        dataBus.writeDataToBus(new byte[]{(byte) 0x42});
        rwFlag.setFlagValue(false);

        addressBus.writeDataToBus(new byte[]{(byte)0x01, (byte)0x02});
        dataBus.writeDataToBus(new byte[]{(byte) 0x43});
        rwFlag.setFlagValue(false);

        clock.tick(6);
        assertEquals((byte)0x02, cpu.getRegSP());
        assertEquals((byte)0xCF, cpu.getRegStatus());

        // Is 0x4343 as PC is incremented post instructions
        assertArrayEquals(new byte[]{(byte)0x43, (byte)0x42}, cpu.getRegPC());
    }
}
