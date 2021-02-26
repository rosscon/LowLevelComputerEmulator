package com.rosscon.llce.components.processors.MOS6502Integer;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.HaltFlag;
import com.rosscon.llce.components.flags.NMIFlag;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.memory.ReadOnlyMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Flags;
import com.rosscon.llce.components.processors.MOS6502.MOS6502Instructions;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the ADC (Add with Carry instructions)
 * http://www.obelisk.me.uk/6502/reference.html#ADC
 *
 */
public class MOS6502TestADC {

    IntegerBus addressBus;
    IntegerBus dataBus;
    RWFlag rwFlag;
    NMIFlag nmiRWFlag;
    HaltFlag haltFlag;
    Clock clock;
    MOS6502 cpu;
    ReadOnlyMemory bootRom;

    @Before
    public void reset() throws InvalidBusWidthException, MemoryException, ProcessorException {

        addressBus = new IntegerBus(16);
        dataBus = new IntegerBus(8);
        rwFlag = new RWFlag();
        nmiRWFlag = new NMIFlag();
        clock = new Clock();

        bootRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0xFFFC, 0xFFFD, new int[]{0, 0});
        cpu = new MOS6502(clock, addressBus, dataBus, rwFlag, nmiRWFlag, haltFlag, true);
    }

    @Test
    @DisplayName("ADC Immediate Mode should add the value of next memory location to accumulator")
    public void testADCImmediateMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
            0x0000, 0x00001, data);

        clock.tick(2);
        assertEquals(0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Adding values that cause the 7th bit to change should set the Overflow Flag")
    public void testADCOverflowFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0x40,
                MOS6502Instructions.INS_ADC_IMM, 0x40
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals(0b10000000, cpu.getRegACC());
        Assertions.assertEquals(MOS6502Flags.OVERFLOW_FLAG, (cpu.getRegStatus() & MOS6502Flags.OVERFLOW_FLAG));
    }

    @Test
    @DisplayName("ADC when 0 should set the zero flag")
    public void testADCZeroFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0x00
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        assertEquals((byte)0b00000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.ZERO_FLAG, (cpu.getRegStatus() & MOS6502Flags.ZERO_FLAG));
    }

    @Test
    @DisplayName("ADC when bit 7 is set negative flag should be set")
    public void testADCNegativeFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0x80
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        clock.tick(2);
        assertEquals(0x80, cpu.getRegACC());
        assertEquals(MOS6502Flags.NEGATIVE_FLAG, (cpu.getRegStatus() & MOS6502Flags.NEGATIVE_FLAG));
    }

    @Test
    @DisplayName("ADC when an overflow in bit 7 occurs the carry flag should be set")
    public void testADCCarryFlag() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_IMM, 0xFF,
                MOS6502Instructions.INS_ADC_IMM, 0x01
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(4);
        assertEquals((byte)0b00000000, cpu.getRegACC());
        assertEquals(MOS6502Flags.CARRY_FLAG, (cpu.getRegStatus() & MOS6502Flags.CARRY_FLAG));
    }

    @Test
    @DisplayName("ADC Zero Page mode should add the value to the accumulator")
    public void testADCZeroPageMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_ZP, 0x03,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0003, data);

        clock.tick(3);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Zero Page X mode should add the value to the accumulator from combining address with X")
    public void testADCZeroPageXMode() throws MemoryException, ClockException{

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01,
                MOS6502Instructions.INS_ADC_ZPX, 0x04,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(6);
        assertEquals(0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_ADC_ABS, 0x04, 0x00,
                0x00, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0004, data);

        clock.tick(4);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute X mode should add the value to the accumulator from combining address with X")
    public void testADCAbsoluteXMode() throws MemoryException, ClockException {

        int[] data = new int[]{
                MOS6502Instructions.INS_LDX_IMM, 0x01,
                MOS6502Instructions.INS_ADC_ABX, 0x04, 0x00,
                0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute X mode with carry should add the value to the accumulator from combining address with X and carry")
    public void testADCAbsoluteXCarryMode() throws MemoryException, ClockException {

        int[] data = new int[0x1000];
        data[0x0000] = MOS6502Instructions.INS_LDX_IMM;
        data[0x0001] = 0x01;
        data[0x0002] = MOS6502Instructions.INS_ADC_ABX;
        data[0x0003] = 0xFF;
        data[0x0004] = 0x00;
        data[0x0100] = 0x43;

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x1000,data);

        clock.tick(6);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute Y mode should add the value to the accumulator from combining address with Y")
    public void testADCAbsoluteYMode() throws MemoryException, ClockException {
        int[] data = new int[]{
                MOS6502Instructions.INS_LDY_IMM, 0x01,
                MOS6502Instructions.INS_ADC_ABY, 0x04, 0x00,
                0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0005, data);

        clock.tick(6);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Absolute Y mode with carry should add the value to the accumulator from combining address with Y and carry")
    public void testADCAbsoluteYCarryMode() throws MemoryException, ClockException {

        int[] data = new int[0x1000];
        data[0x0000] = MOS6502Instructions.INS_LDY_IMM;
        data[0x0001] = 0x01;
        data[0x0002] = MOS6502Instructions.INS_ADC_ABY;
        data[0x0003] = 0xFF;
        data[0x0004] = 0x00;
        data[0x0100] = 0x43;

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x1000, data);

        clock.tick(6);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect X mode should add the value to the accumulator from combining address with X")
    public void testADCIndirectXMode() throws MemoryException, ClockException {

        int[] data = new int[0x3000];
        data[0x0000] = MOS6502Instructions.INS_LDX_IMM;
        data[0x0001] = 0x04;
        data[0x0002] = MOS6502Instructions.INS_ADC_INX;
        data[0x0003] = 0x20;
        data[0x0024] = 0x74;
        data[0x0025] = 0x20;
        data[0x2074] = 0x42;

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x3000, data);

        clock.tick(8);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect Y mode should add the value to the accumulator from combining address with Y")
    public void testADCIndirectYMode() throws MemoryException, ClockException {

        int[] data = new int[0x4040];
        data[0x0000] = MOS6502Instructions.INS_LDY_IMM;
        data[0x0001] = 0x10;
        data[0x0002] = MOS6502Instructions.INS_ADC_INY;
        data[0x0003] = 0x86;
        data[0x0086] = 0x28;
        data[0x0087] = 0x40;
        data[0x4038] = 0x43;

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x4040, data);

        clock.tick(7);
        assertEquals((byte)0x43, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC Indirect Y Carry mode should add the value to the accumulator from combining address with Y")
    public void testADCIndirectYCarryMode() throws MemoryException, ClockException {

        int[] data = new int[0x1000];
        data[0x0000] = MOS6502Instructions.INS_LDY_IMM;
        data[0x0001] = 0xFF;

        data[0x0002] = MOS6502Instructions.INS_ADC_INY;
        data[0x0003] = 0x04;
        data[0x0004] = 0x06;
        data[0x0005] = 0x00;
        data[0x0105] = 0x42;

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x1000, data);

        clock.tick(7);
        assertEquals((byte)0x42, cpu.getRegACC());
    }

    @Test
    @DisplayName("ADC When carry bit is set this should also be added to the value")
    public void testADCFromCarry() throws MemoryException, ClockException{

        int[] data = new int[]{
                MOS6502Instructions.INS_SEC_IMP,
                MOS6502Instructions.INS_ADC_IMM, 0x42
        };

        ReadOnlyMemory testADCRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0002, data);

        clock.tick(4);
        assertEquals(0x43, cpu.getRegACC());
    }

}
