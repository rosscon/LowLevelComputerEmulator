package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.HaltFlag;
import com.rosscon.llce.components.flags.NMIFlag;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.memory.*;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the JSR instruction
 */
public class MOS6502TestJSR {

    IntegerBus addressBus;
    IntegerBus dataBus;
    RWFlag rwFlag;
    NMIFlag nmiRWFlag;
    HaltFlag haltFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;
    RandomAccessMemory randomAccessMemory;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new RWFlag();
        nmiRWFlag = new NMIFlag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});

        randomAccessMemory = new RandomAccessMemory(addressBus, dataBus,rwFlag,
                0x0010, 0x02FF);

        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiRWFlag, haltFlag, true);
    }

    @Test
    @DisplayName("JSR should put the PC (minus 1) to the stack then set the PC to the address read")
    public void testJSR() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_JSR_ABS, 0x34, 0x12
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(6);
        assertEquals(0xFD, cpu.getRegSP());
        assertEquals(0x1234, cpu.getRegPC());
    }


}
