package com.rosscon.llce;

import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.computers.nintendo.NES;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InvalidBusWidthException, MemoryException, ClockException, ProcessorException, IOException, CartridgeException {


        NES console = new NES();

/*
        // Test 6502
        Bus addressBus = new Bus(16);
        Bus dataBus = new Bus(8);
        Flag rwFlag = new Flag();
        Clock clock = new Clock();

        Map<ByteArrayWrapper, byte[]> initROM = new HashMap<>() {{
            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFC }),
                    new byte[]{ NMOS6502Instructions.INS_JMP_ABS });

            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFD }),
                    new byte[]{ (byte)0x00 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0xFF, (byte) 0xFE }),
                    new byte[]{ (byte)0x00 });



            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x00 }),
                    new byte[]{ NMOS6502Instructions.INS_JMP_IND });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x01 }),
                    new byte[]{ (byte) 0x04 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x02 }),
                    new byte[]{ (byte) 0x00 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x04 }),
                    new byte[]{ (byte) 0x10 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x05 }),
                    new byte[]{ (byte) 0x00 });



            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x10 }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x11 }),
                    new byte[]{ (byte)0x03 });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x12 }),
                    new byte[]{ NMOS6502Instructions.INS_ADC_IMM });

            put(new ByteArrayWrapper(new byte[]{ (byte)0x00, (byte) 0x13 }),
                    new byte[]{ (byte)0x03 });
        }};

        ReadOnlyMemory rom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, initROM);
        NMOS6502 cpu = new NMOS6502(clock, addressBus, dataBus, rwFlag);

        clock.tick(12);*/
    }
}

