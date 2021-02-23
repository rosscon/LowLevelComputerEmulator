package com.rosscon.llce.computers.nintendo;


import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.cartridges.CartridgeException;
import com.rosscon.llce.components.cartridges.NES.NESCartridge;
import com.rosscon.llce.components.cartridges.NES.NESCartridgeFactory;
import com.rosscon.llce.components.cartridges.NES.NESNametableMirroring;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockThreaded;
import com.rosscon.llce.components.clocks.dividers.Divider;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.graphics.NES2C02.NES2C02;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.MOS6502.MOS6502;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.computers.Computer;
import javafx.scene.image.PixelWriter;

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
    private Flag rwFlagCpu;

    /**
     * Internal RAM and mapper
     */
    private Flag rwFlagInternalRamMapper;
    private RandomAccessMemory internalRAM;
    private IntegerBus internalRAMAddressBus;
    private IntegerBus internalRAMDataBus;
    private MirroredMapper internalRAMMapper;

    /**
     * PPU busses and flags
     */
    private Flag rwFlagPPU;
    private IntegerBus ppuAddressBus;
    private IntegerBus ppuDataBus;

    /**
     * VRAM, Nametables and palette memory
     */
    private RandomAccessMemory nameTableMemory;
    private RandomAccessMemory paletteMemory;
    private MirroredMapper nametableMapper;
    private IntegerBus nametableAddressBus;
    private IntegerBus nametableDataBus;
    private Flag rwFlagNametableMapper;


    /**
     * Cartridge
     */
    private NESCartridge cartridge;


    private ClockThreaded masterClock;
    private Divider cpuDivider;
    private Divider ppuDivider;

    private MOS6502 cpu;

    private NES2C02 ppu;

    public NES () throws InvalidBusWidthException, IOException, CartridgeException, ProcessorException, MemoryException, InvalidBusDataException, FlagException {

        /*
         * Main bus
         */
        this.cpuAddressBus = new IntegerBus(16);
        this.cpuDataBus = new IntegerBus(8);
        this.rwFlagCpu = new Flag();

        /*
         * Internal RAM and mirroring mapper
         */
        this.internalRAMAddressBus = new IntegerBus(16);
        this.internalRAMDataBus = new IntegerBus(8);
        this.rwFlagInternalRamMapper = new Flag();
        this.internalRAM = new RandomAccessMemory(internalRAMAddressBus, internalRAMDataBus,
                rwFlagInternalRamMapper,0x0000, 0x07FF);

        this.internalRAMMapper = new MirroredMapper(cpuAddressBus, cpuDataBus, rwFlagCpu,
                this.internalRAM, 0x0000, 0x1FFF, 0x07FF);


        /*
         * Setup the PPU TODO
         */
        this.ppuAddressBus = new IntegerBus(16);
        this.ppuDataBus = new IntegerBus(8);
        this.rwFlagPPU = new Flag();


        /*
         * Setup/Add the cartridge
         */

        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/donkey.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/
        this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/mario.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );
        /*this.cartridge = NESCartridgeFactory.cartridgeFromINESFile(
                "/Users/rossconroy/Desktop/nestest.nes",
                this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU
        );*/


        /*
         * Lastly add the CPU and PPU as they call reset() on start
         */
        this.masterClock = new ClockThreaded(10);
        Clock clock = new Clock();

        this.cpuDivider = new Divider(12, masterClock);
        this.ppuDivider = new Divider(4, masterClock);

        //this.cpu = new MOS6502(cpuDivider, this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu, true, 0xC000);
        this.cpu = new MOS6502(cpuDivider, this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu, false, 0xC000);

        this.ppu = new NES2C02(ppuDivider, cpuAddressBus, cpuDataBus, rwFlagCpu,
                ppuAddressBus, ppuDataBus, rwFlagPPU);

        /*
         * Cheating a bit with the nametable memory and assigning all 4KB,
         * however will be behind a mirrored mapper so only 2KB will actually be used
         */
        this.nametableAddressBus = new IntegerBus(16);
        this.nametableDataBus = new IntegerBus(8);
        this.rwFlagNametableMapper = new Flag();
        this.nameTableMemory = new RandomAccessMemory(this.nametableAddressBus, this.nametableDataBus,
                this.rwFlagNametableMapper,0x2000, 0x2C00);
        int mapperMask = this.cartridge.getNametableMapper() == NESNametableMirroring.VERTICAL ? (0x01 << 10) : (0x01 << 11);
        this.nametableMapper = new MirroredMapper(ppuAddressBus, ppuDataBus, rwFlagPPU,
                this.nameTableMemory, 0x2000, 0x2C00, mapperMask);


        long start = System.nanoTime();
        int cycles = 10000000;

        try {
            clock.tick(cycles);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        long end = System.nanoTime();

        /*try {
            this.cpuAddressBus.writeDataToBus(0x0002);
            this.rwFlagCpu.setFlagValue(true);
            System.out.println("0x0002 - " + String.format("%02X", this.cpuDataBus.readDataFromBus()));
            this.cpuAddressBus.writeDataToBus(0x0003);
            this.rwFlagCpu.setFlagValue(true);
            System.out.println("0x0003 - " + String.format("%02X", this.cpuDataBus.readDataFromBus()));



        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        long clockDiff = end - start;
        System.out.println((cycles / (clockDiff / 1000000000.0d))/1000000.0d + "MHz");*/

        /*this.nameTableMemory = new RandomAccessMemory(internalRAMAddressBus, internalRAMDataBus,
                rwFlagPPU, new byte[] {0x20, 0x00}, new byte[] { 0x3E, (byte)0xFF});

        this.ppu = new NES2C02(ppuDivider, this.cpuAddressBus, this.cpuDataBus, this.rwFlagCpu,
                this.ppuAddressBus, this.ppuDataBus, this.rwFlagPPU, pixelwriter);*/

        Thread thread = new Thread(this.masterClock);
        thread.start();

        System.out.println("Clock Started");
    }

    public int[] getScreenBuffer(){
        return this.ppu.getScreenBuffer();
    }

}
