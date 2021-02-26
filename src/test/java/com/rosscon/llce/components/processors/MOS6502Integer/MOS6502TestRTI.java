package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.*;
import com.rosscon.llce.components.memory.*;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the RTI function
 */
public class MOS6502TestRTI {

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
    @DisplayName("RTI should pull the processor flags and mask them followed by PC from the stack")
    public void testRTI() throws MemoryException, ClockException, InvalidBusDataException, FlagException {

        int[] data = new int[]{
                MOS6502Instructions.INS_RTI_IMP, 0x00
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        // Writing direct to stack
        addressBus.writeDataToBus(0x0100);
        dataBus.writeDataToBus(0xFF);
        rwFlag.setFlagValue(RWFlag.WRITE);

        addressBus.writeDataToBus(0x0101);
        dataBus.writeDataToBus(0x42);
        rwFlag.setFlagValue(RWFlag.WRITE);

        addressBus.writeDataToBus(0x0102);
        dataBus.writeDataToBus(0x43);
        rwFlag.setFlagValue(RWFlag.WRITE);

        clock.tick(6);
        assertEquals(0x02, cpu.getRegSP());
        assertEquals(0xCF, cpu.getRegStatus());

        // Is 0x4343 as PC is incremented post instructions
        assertEquals(0x4342, cpu.getRegPC());
    }
}
