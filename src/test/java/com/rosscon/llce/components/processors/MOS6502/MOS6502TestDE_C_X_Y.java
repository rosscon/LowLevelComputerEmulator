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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the decrement functions DEX DEX DEY
 */
public class MOS6502TestDE_C_X_Y {

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
    @DisplayName("DEC should reduce the value at a given memory address")
    public void testDEC() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        dataBus.writeDataToBus(new byte[]{(byte) 0x42});
        rwFlag.setFlagValue(false);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        rwFlag.setFlagValue(true);

        assertArrayEquals(new byte[]{(byte)0x41}, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("DEC should set the zero flag if the result is zero")
    public void testDECZeroFlag() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        dataBus.writeDataToBus(new byte[]{(byte) 0x01});
        rwFlag.setFlagValue(false);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        rwFlag.setFlagValue(true);

        // Check decrement
        assertArrayEquals(new byte[]{(byte)0x00}, dataBus.readDataFromBus());

        //Check zero flag
        Assertions.assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEC should set the negative flag if the result has bit 7 set")
    public void testDECNegativeFlag() throws MemoryException, ClockException, InvalidBusDataException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_DEC_ABS, 0x01, 0x02
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        // Writing direct to RAM
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        dataBus.writeDataToBus(new byte[]{(byte) 0xF1});
        rwFlag.setFlagValue(false);

        clock.tick(6);
        addressBus.writeDataToBus(new byte[]{(byte)0x02, (byte)0x01});
        rwFlag.setFlagValue(true);

        // Check decrement
        assertArrayEquals(new byte[]{(byte)0xF0}, dataBus.readDataFromBus());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEX() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x45, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(2);

        // Check decrement
        assertEquals((byte)0x45, cpu.getRegX());
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1 and set the zero flag")
    public void testDEXZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);

        // Check decrement
        assertEquals((byte)0x00, cpu.getRegX());

        //Check negative flag
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEXNegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x00, MOS6502Instructions.INS_DEX_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);

        // Check decrement
        assertEquals((byte)0xFF, cpu.getRegX());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }


    @Test
    @DisplayName("DEY should decrement the value of Y register by 1")
    public void testDEY() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x45, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);

        // Check decrement
        assertEquals((byte)0x44, cpu.getRegY());
    }

    @Test
    @DisplayName("DEY should decrement the value of Y register by 1 and set the zero flag")
    public void testDEYZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x01, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);

        // Check decrement
        assertEquals((byte)0x00, cpu.getRegY());

        //Check negative flag
        assertEquals(MOS6502Flags.ZERO_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("DEX should decrement the value of X register by 1")
    public void testDEYNegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x00, MOS6502Instructions.INS_DEY_IMP,
        };

        ReadOnlyMemory testJSRRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(5);

        // Check decrement
        assertEquals((byte)0xFF, cpu.getRegY());

        //Check negative flag
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (byte)(cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

}
