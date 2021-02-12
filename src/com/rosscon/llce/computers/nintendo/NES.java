package com.rosscon.llce.computers.nintendo;


import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.mappers.MirroredMapper;
import com.rosscon.llce.components.memory.RandomAccessMemory;
import com.rosscon.llce.components.processors.NMOS6502.NMOS6502;
import com.rosscon.llce.computers.Computer;

/**
 * This is a pre configured Nintendo NES computer
 *
 * CPU MEMORY MAP See https://wiki.nesdev.com/w/index.php/CPU_memory_map
 * Address Range        Size HEX    Size                Device
 * 0x0000 - 0x07FFF     0x0800      2KB                 Internal RAM
 * 0x0800 - 0x1FFFF     0x18000     6KB (3x2KB)         Mirrors of 0x0000 - 0x07FFF
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
 *              +------+------+    +-------+------+
 *              | Nametables  |    | Pallette RAM |
 *              +-------------+    +--------------+
 */
public class NES extends Computer {

    Bus mainAddressBus;
    Bus mainDataBus;
    Bus ppuAddressBus;
    Bus ppuDataBus;

    MirroredMapper internalRAMMapper;

    RandomAccessMemory internalRAM;
    NMOS6502 cpu;

}
