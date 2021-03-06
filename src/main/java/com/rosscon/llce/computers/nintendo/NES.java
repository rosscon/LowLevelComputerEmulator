package com.rosscon.llce.computers.nintendo;


import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.cartridges.NES.NESCartridge;
import com.rosscon.llce.components.cartridges.NES.NESCartridgeFactory;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockThreaded;
import com.rosscon.llce.components.clocks.dividers.Divider;
import com.rosscon.llce.components.controllers.NES.NESControllerKeyboard;
import com.rosscon.llce.components.flags.HaltFlag;
import com.rosscon.llce.components.flags.NMIFlag;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.graphics.NES2C02.NES2C02;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.computers.Computer;
import javafx.event.EventHandler;

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
    private IntegerBus cpuAddressBus;
    private IntegerBus cpuDataBus;
    private RWFlag rwRWFlagCpu;

    /**
     * Internal RAM and mapper
     */
    private RWFlag rwRWFlagInternalRamMapper;
    private RandomAccessMemory internalRAM;
    private IntegerBus internalRAMAddressBus;
    private IntegerBus internalRAMDataBus;
    private MirroredMapper internalRAMMapper;

    /**
     * PPU busses and flags
     */
    private RWFlag rwRWFlagPPU;
    private IntegerBus ppuAddressBus;
    private IntegerBus ppuDataBus;


    /**
     * Cartridge
     */
    private NESCartridge cartridge;


    /**
     * Interrupts
     */
    private NMIFlag flgCpuNmi;
    private HaltFlag flgCpuHalt;


    private ClockThreaded masterClock;
    private Divider cpuDivider;
    private Divider ppuDivider;

    private MOS6502 cpu;

    private NES2C02 ppu;

    private NESControllerKeyboard controller;

    public NES () throws InvalidBusWidthException, IOException, CartridgeException, ProcessorException, MemoryException, InvalidBusDataException, FlagException {

        /*
         * Main bus
         */
        this.cpuAddressBus = new IntegerBus(16);
        this.cpuDataBus = new IntegerBus(8);
        this.rwRWFlagCpu = new RWFlag();

        /*
         * Internal RAM and mirroring mapper
         */
        this.internalRAMAddressBus = new IntegerBus(16);
        this.internalRAMDataBus = new IntegerBus(8);
        this.rwRWFlagInternalRamMapper = new RWFlag();
        this.internalRAM = new RandomAccessMemory(internalRAMAddressBus, internalRAMDataBus,
                rwRWFlagInternalRamMapper,0x0000, 0x07FF);

        this.internalRAMMapper = new MirroredMapper(cpuAddressBus, cpuDataBus, rwRWFlagCpu,
                this.internalRAM, 0x0000, 0x1FFF, 0x07FF);


        /*
         * Setup the PPU Busses
         */
        this.ppuAddressBus = new IntegerBus(16);
        this.ppuDataBus = new IntegerBus(8);
        this.rwRWFlagPPU = new RWFlag();


        /*
         * Setup/Add the cartridge
         */

        this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/donkey.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/mspacman.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/mario.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/excitebike.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/balloon.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/ice.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/castle3.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/nestest.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );*/
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/full_palette/full_palette.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwRWFlagPPU
        );*/


        /*
         * Lastly add the CPU and PPU as they call reset() on start
         */
        //this.masterClock = new ClockThreaded(10);
        this.masterClock = new ClockThreaded(10);
        Clock clock = new Clock();

        this.cpuDivider = new Divider(12, masterClock);
        this.ppuDivider = new Divider(4, masterClock);

        //this.cpuDivider = new Divider(12, clock);
        //this.ppuDivider = new Divider(4, clock);

        this.flgCpuNmi = new NMIFlag();
        this.flgCpuHalt = new HaltFlag();

        //this.cpu = new MOS6502(cpuDivider, this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu, this.flgCpuNmi, this.flgCpuHalt, false, 0xC000);
        this.cpu = new MOS6502(cpuDivider, this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu, this.flgCpuNmi, this.flgCpuHalt, false);

        this.ppu = new NES2C02(ppuDivider, cpuAddressBus, cpuDataBus, rwRWFlagCpu,
                ppuAddressBus, ppuDataBus, rwRWFlagPPU, flgCpuNmi, flgCpuHalt, cartridge.getNametableMirror());


        controller = new NESControllerKeyboard(this.cpuAddressBus, this.cpuDataBus, this.rwRWFlagCpu, 1);

        Thread thread = new Thread(this.masterClock);
        thread.start();

        System.out.println("Clock Started");
    }

    public EventHandler getKeyPressHandler(){
        return controller.getKeyPressHandler();
    }

    public EventHandler getKeyReleaseHandler(){
        return controller.getKeyReleaseHandler();
    }

    public NES2C02 getGpu(){
        return this.ppu;
    }

}
