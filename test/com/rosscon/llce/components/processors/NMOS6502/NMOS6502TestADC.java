package com.rosscon.llce.components.processors.NMOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.utils.ByteArrayWrapper;
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
public class NMOS6502TestADC {

    @Test
    @DisplayName("ADC Immediate Mode should add the value of next memory location to accumulator")
    public void testADCImmediateMode() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0x42 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(2);
        assertEquals((byte)0x42, cpu.getAcc());
    }

    @Test
    @DisplayName("ADC Adding values that cause the 7th bit to change should set the Overflow Flag")
    public void testADCOverflowFlag() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0b01000000 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFF }),
                    new byte[]{ (byte)0b01000000 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(4);
        assertEquals((byte)0b10000000, cpu.getAcc());
        assertEquals(NMOS6502Flags.OVERFLOW_FLAG, (cpu.getStatus() & NMOS6502Flags.OVERFLOW_FLAG));
    }

    @Test
    @DisplayName("ADC when 0 should set the zero flag")
    public void testADCZeroFlag() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0b00000000 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(2);
        assertEquals((byte)0b00000000, cpu.getAcc());
        assertEquals(NMOS6502Flags.ZERO_FLAG, (cpu.getStatus() & NMOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("ADC when bit 7 is set negative flag should be set")
    public void testADCNegativeFlag() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0b10000000 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(2);
        assertEquals((byte)0b10000000, cpu.getAcc());
        assertEquals(NMOS6502Flags.NEGATIVE_FLAG, (cpu.getStatus() & NMOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ADC when an overflow in bit 7 occurs the carry flag should be set")
    public void testADCCarryFlag() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0b11111111 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFF }),
                    new byte[]{ (byte)0b00000001 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(4);
        assertEquals((byte)0b00000000, cpu.getAcc());
        assertEquals(NMOS6502Flags.CARRY_FLAG, (cpu.getStatus() & NMOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("ADC Zero Page mode should add the value to the accumulator")
    public void testADCZeroPageMode() throws MemoryException, ClockException, InvalidBusWidthException {

        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_ZP });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0x42 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x42 }),
                    new byte[]{ (byte)0x43 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(3);
        assertEquals((byte)0x43, cpu.getAcc());
    }

    @Test
    @DisplayName("ADC Zero Page X mode should add the value to the accumulator from combining address with X")
    public void testADCZeroPageXMode() throws MemoryException, ClockException, InvalidBusWidthException {
        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_LDX_IMM });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0x01 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_ZPX });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFF }),
                    new byte[]{ (byte)0x02 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x03 }),
                    new byte[]{ (byte)0x42 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag, true);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getAcc());
    }

    @Test
    @DisplayName("ADC Absolute mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteMode() throws MemoryException, ClockException, InvalidBusWidthException {
        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>(){{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_ABS });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0x01 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ (byte)0x02 });
            put(new ByteArrayWrapper(new byte[]{ (byte)0x02, (byte) 0x01 }),
                    new byte[]{ (byte)0x42 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(4);
        assertEquals((byte)0x42, cpu.getAcc());
    }

    @Test
    @DisplayName("ADC Absolute X mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteXMode() throws MemoryException, ClockException, InvalidBusWidthException {
        //TODO
    }

    @Test
    @DisplayName("ADC Absolute X mode with carry should add the value to the accumulator from combining address with X and carry")
    public void testADCAbsoluteXCarryMode() throws MemoryException, ClockException, InvalidBusWidthException {
        //TODO
    }

    @Test
    @DisplayName("ADC Absolute Y mode should add the value to the accumulator from combining address with Y")
    public void testADCAbsoluteYMode() throws MemoryException, ClockException, InvalidBusWidthException {
        //TODO
    }

    @Test
    @DisplayName("ADC Indirect X mode should add the value to the accumulator from combining address with X")
    public void testADCIndirectXMode() throws MemoryException, ClockException, InvalidBusWidthException {
        //TODO needs LDX instruction and IndirectX addressing mode working prior
    }

    @Test
    @DisplayName("ADC Indirect X mode should add the value to the accumulator from combining address with X")
    public void testADCIndirectYMode() throws MemoryException, ClockException, InvalidBusWidthException {
        //TODO needs LDY instruction and IndirectY addressing mode working prior
    }

}
