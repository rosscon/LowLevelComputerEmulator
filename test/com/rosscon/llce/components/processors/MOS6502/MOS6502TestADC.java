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
 * Tests the ADC (Add with Carry instructions)
 * http://www.obelisk.me.uk/6502/reference.html#ADC
 *
 */
public class MOS6502TestADC {

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
    @DisplayName("ADC Immediate Mode should add the value of next memory location to accumulator")
    public void testADCImmediateMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
            new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Adding values that cause the 7th bit to change should set the Overflow Flag")
    public void testADCOverflowFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_ADC_IMM, 0x40
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0b10000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
    }

    @Test
    @DisplayName("ADC when 0 should set the zero flag")
    public void testADCZeroFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0b00000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("ADC when bit 7 is set negative flag should be set")
    public void testADCNegativeFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, (byte)0x80
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x01}, data);

        clock.tick(2);
        assertEquals((byte)0b10000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ADC when an overflow in bit 7 occurs the carry flag should be set")
    public void testADCCarryFlag() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_IMM, (byte) 0xFF,
                MOS6502Instructions.INS_ADC_IMM, (byte) 0x01
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(4);
        assertEquals((byte)0b00000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("ADC Zero Page mode should add the value to the accumulator")
    public void testADCZeroPageMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_ZP, (byte) 0x03,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x03}, data);

        clock.tick(3);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Zero Page X mode should add the value to the accumulator from combining address with X")
    public void testADCZeroPageXMode() throws MemoryException, ClockException{

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, (byte) 0x01,
                MOS6502Instructions.INS_ADC_ZPX, 0x04,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_ADC_ABS, 0x04, 0x00,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x04}, data);

        clock.tick(4);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute X mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteXMode() throws MemoryException, ClockException {

        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01,
                MOS6502Instructions.INS_ADC_ABX, 0x04, 0x00,
                0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute X mode with carry should add the value to the accumulator from combining address with X and carry")
    public void testADCAbsoluteXCarryMode() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_LDX_IMM});
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte)0x01 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ MOS6502Instructions.INS_ADC_ABX });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0xFF });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x04 }),
                    new byte[]{ (byte)0x00 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x01, (byte) 0x00 }),
                    new byte[]{ (byte)0x43 });
        }};

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(7);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute Y mode should add the value to the accumulator from combining address with Y")
    public void testADCAbsoluteYMode() throws MemoryException, ClockException {
        byte[] data = new byte[]{
                MOS6502Instructions.INS_LDY_IMM, 0x01,
                MOS6502Instructions.INS_ADC_ABY, 0x04, 0x00,
                0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x05}, data);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute Y mode with carry should add the value to the accumulator from combining address with Y and carry")
    public void testADCAbsoluteYCarryMode() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_LDY_IMM});
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte)0x01 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ MOS6502Instructions.INS_ADC_ABY });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0xFF });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x04 }),
                    new byte[]{ (byte)0x00 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x01, (byte) 0x00 }),
                    new byte[]{ (byte)0x43 });
        }};

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(7);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect X mode should add the value to the accumulator from combining address with X")
    public void testADCIndirectXMode() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_LDX_IMM});
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte)0x04 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ MOS6502Instructions.INS_ADC_INX });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0x20 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x24 }),
                    new byte[]{ (byte)0x74 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x25 }),
                    new byte[]{ (byte)0x20 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x20, (byte) 0x74 }),
                    new byte[]{ (byte)0x42 });
        }};

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(8);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect Y mode should add the value to the accumulator from combining address with Y")
    public void testADCIndirectYMode() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_LDY_IMM});
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte)0x10 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ MOS6502Instructions.INS_ADC_INY });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0x86 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x86 }),
                    new byte[]{ (byte)0x28 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x87 }),
                    new byte[]{ (byte)0x40 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x40, (byte) 0x38 }),
                    new byte[]{ (byte)0x43 });
        }};

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(7);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect Y Carry mode should add the value to the accumulator from combining address with Y")
    public void testADCIndirectYCarryMode() throws MemoryException, ClockException {

        Map<ByteArrayWrapper, byte[]> data = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ MOS6502Instructions.INS_LDY_IMM});
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte)0x01 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ MOS6502Instructions.INS_ADC_INY });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0x86 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x86 }),
                    new byte[]{ (byte)0xFF });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x87 }),
                    new byte[]{ (byte)0x40 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x41, (byte) 0x00 }),
                    new byte[]{ (byte)0x42 });
        }};

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, data);

        clock.tick(8);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC When carry bit is set this should also be added to the value")
    public void testADCFromCarry() throws MemoryException, ClockException{

        byte[] data = new byte[]{
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_ADC_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                new byte[]{0x00, 0x00}, new byte[]{0x00, 0x02}, data);

        clock.tick(4);
        assertEquals(0x43, cpu.getRegACC());
    }

}
