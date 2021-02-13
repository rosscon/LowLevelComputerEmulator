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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Since all the set and clear flag commands are so simple have grouped together.
 */
public class MOS6502TestSE_CL {

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
    @DisplayName("SEC and CLC")
    public void testSECandCLC() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_CLC_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG), MOS6502Flags.CARRY_FLAG);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG), MOS6502Flags.CARRY_FLAG);
    }

    @Test
    @DisplayName("SED and CLD")
    public void testSEDandCLD() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_SED_IMP,
                MOS6502Instructions.INS_CLD_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.DECIMAL_MODE), MOS6502Flags.DECIMAL_MODE);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.DECIMAL_MODE), MOS6502Flags.DECIMAL_MODE);
    }

    @Test
    @DisplayName("SEI and CLI")
    public void testSEIandCLI() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_SEI_IMP,
                MOS6502Instructions.INS_CLI_IMP
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
    }

    @Test
    @DisplayName("CLV")
    public void testCLV() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_CLV_IMP,
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(4);
        assertEquals((cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG), MOS6502Flags.OVERFLOW_FLAG);
        clock.tick(2);
        assertNotEquals((cpu.getRegStatus() & MOS6502Flags.INTERRUPT_DIS), MOS6502Flags.INTERRUPT_DIS);
    }
}
