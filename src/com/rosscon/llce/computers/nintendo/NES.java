package com.rosscon.llce.computers.nintendo;


import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.cartridges.NES.NESCartridge;
import com.rosscon.llce.components.cartridges.NES.NESCartridgeFactory;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.clocks.dividers.Divider;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.computers.Computer;

import java.io.IOException;

/**
 * This is a pre configured Nintendo NES computer
 *
 * CPU MEMORY MAP See https://wiki.nesdev.com/w/index.php/CPU_memory_map
 * Address Range        Size HEX    Size                Device
 * 0x0000 - 0x07FF      0x0800      2KB                 Internal RAM
 * 0x0800 - 0x1FFF      0x18000     6KB (3x2KB)         Mirrors of 0x0000 - 0x07FFF
 * 0x2000 - 0x2007      0x0008      8 bytes             PPU Registers
 * 0x2008 - 0x3FFF      0x1FF8      8184 bytes          Mirror of PPU registers, repeats every 8 bytes
 * 0x4000 - 0x4017      0x0018      24 bytes            APU an I/O registers
 * 0x4018 - 0x401F      0x0008      8 bytes             APU and I/O, normally disabled (CPU test mode)
 * 0x4020 - 0xFFFF      0xBFE0      47.9KB              Cartridge (PRG ROM, PRG RAM) and mappers
 *
 * PPU MEMORY MAP see https://wiki.nesdev.com/w/index.php/PPU_memory_map
 * This memory map is on a separate bus to the CPU memory
 * Address Range        Size HEX    Size                Device
 * 0x0000 - 0x0FFF      0x1000      4KB                 Pattern table 0
 * 0x1000 - 0x1FFF      0x1000      4KB                 Pattern table 1
 * 0x2000 - 0x23FF      0x0400      1KB                 Nametable 0
 * 0x2400 - 0x27FF      0x0400      1KB                 Nametable 1
 * 0x2800 - 0x2BFF      0x0400      1KB                 Nametable 2
 * 0x2C00 - 0x2FFF      0x0400      1KB                 Nametable 3
 * 0x3000 - 0x3EFF      0x0F00      3KB                 Mirror of 0x2000 - 0x2EFF
 * 0x3F00 - 0x3F1F      0x0020      32 bytes            Pallette RAM indexes
 * 0x3F20 - 0x3FFF      0x00E0      224 bytes           Mirrors of 0x3F00 - 0x3F1F (7 x 32 bytes)
 *
 * NES Architecture
 *                   +------------------+
 *                   | 2KB Internal RAM |
 *                   +---------+--------+
 *                             |
 *       +----------+ +--------|--------+ +-----+ +-----+
 *       | 6502 CPU | | Mirrored Mapper | | APU | | I/O |
 *       +-----+----+ +--------+--------+ +--+--+ +--+--+
 *             |              |              |       |
 *             |              |              |       |
 *         +---+--------------+--------------+-------+--+
 *         |                 Main Bus                   |
 *         +---+------------------------------+---------+
 *             |     +------------------------|-------------------+
 *             |     | Cartridge          +---+----+  +---------+ |
 *          +--+--+  |                    | Mapper +--+ PRG ROM | |
 *          | PPU |  | +-------------+    +--------+  +---------+ |
 *          +--+--+  | | 8KB Pattern |                            |
 *             |     | +------+------+                            |
 *             |     +--------|-----------------------------------+
 *          +--+--------------+-----------------------+
 *          |                PPU Bus                  |
 *          +----------+-------------------+----------+
 *                     |                   |
 *           +---------+--------+ +--------+-------+
 *           | Nametable Mapper | | Palette Mapper |
 *           +---------+--------+ +--------+-------+
 *                     |                   |
 *              +------+------+     +------+------+
 *              | Nametables  |     | Palette RAM |
 *              +-------------+     +-------------+
 */
public class NES extends Computer {

    /**
     * Main Busses and flags
     */
    private Bus cpuAddressBus;
    private Bus cpuDataBus;
    private Flag rwFlagCpu;

    /**
     * Internal RAM and mapper
     */
    private Flag rwFlagInternalRamMapper;
    private RandomAccessMemory internalRAM;
    private Bus internalRAMAddressBus;
    private Bus internalRAMDataBus;
    private MirroredMapper internalRAMMapper;

    /**
     * PPU busses and flags
     */
    private Flag rwFlagPPU;
    private Bus ppuAddressBus;
    private Bus ppuDataBus;


    /**
     * Cartridge
     */
    private NESCartridge cartridge;


    private Clock masterClock;
    private Divider cpuDivider;
    private Divider ppuDivider;

    private MOS6502 cpu;

    public NES () throws InvalidBusWidthException, IOException, CartridgeException, ProcessorException, ClockException {

        /*
         * Main bus
         */
        this.cpuAddressBus = new Bus(16);
        this.cpuDataBus = new Bus(8);
        this.rwFlagCpu = new Flag();

        /*
         * Internal RAM and mirroring mapper
         */
        this.internalRAMAddressBus = new Bus(16);
        this.internalRAMDataBus = new Bus(8);
        this.rwFlagInternalRamMapper = new Flag();
        this.internalRAM = new RandomAccessMemory(internalRAMAddressBus, internalRAMDataBus,
                rwFlagInternalRamMapper,
                new byte[] {0x00, 0x00}, new byte[] { 0x07, (byte)0xFF});

        this.internalRAMMapper = new MirroredMapper(cpuAddressBus, cpuDataBus, rwFlagCpu,
                this.internalRAM, new byte[] {0x00, 0x00},
                new byte[] { 0x1F, (byte)0xFF}, new byte[] { 0x07, (byte)0xFF});


        /*
         * Setup the PPU TODO
         */
        this.ppuAddressBus = new Bus(16);
        this.ppuDataBus = new Bus(8);
        this.rwFlagPPU = new Flag();


        /*
         * Setup/Add the cartridge
         */
        Clock clock = new Clock();
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/donkey.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/mario.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/nestest.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );


        /*
         * Lastly add the CPU as it will call reset() on start
         */
        this.masterClock = new Clock();
        this.cpuDivider = new Divider(12, masterClock);
        this.ppuDivider = new Divider(4, masterClock);

        this.cpu = new MOS6502(masterClock, this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu, true);

        //long cycles = 100000000;
        long cycles = 100000000;
        long start = System.nanoTime();
        this.masterClock.tick(cycles);
        long finish = System.nanoTime();
        float difference = finish - start;
        System.out.println(((float)cycles / (difference / 1000000000f)) / 1000000f + "MHz");

        System.out.println("TEST");
    }

}
