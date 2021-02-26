package com.rosscon.llce.components.graphics.NES2C02;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.cartridges.NES.NESNametableMirroring;
import com.rosscon.llce.components.cartridges.NES.NametableMirror;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.*;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;


public class NES2C02 extends Processor implements FlagListener {

    private int[] screenBufferOdd;
    private int[] screenBufferEven;

    /**
     * used for calculating the current framerate
     */
    public long startOfFrame;
    public long endOfFrame;

    /**
     * Internal registers
     */
    private int     regPPUCTRL;
    private int     regPPUMASK;
    private int     regPPUSTATUS;
    private int     regOAMADDR;
    private int     regOAMDATA;
    private int     regPPUSCROLL;
    private int     regPPUADDR;
    private int     regPPUDATA;
    private boolean regOddFrame;


    /**
     * Delayed read and write
     * Reading from most registers takes an extra clock cycle
     */
    private int addressLatch;
    private int dataBuffer;

    /**
     * PPU specific Busses & Flags
     */
    private IntegerBus ppuAddressBus;
    private IntegerBus ppuDataBus;
    private RWFlag ppuRwRWFlag;


    /**
     * Tracking where we are on the screen.
     */
    private int scanline;
    private int cycle;
    private int fineX;

    private LoopyRegister vramAddress;
    private LoopyRegister tramAddress;


    /**
     * Flags
     */
    private RWFlag flgCpuRW;
    private NMIFlag flgCpuNmi;
    private HaltFlag flgCpuHalt;

    /**
     * Background tile details
     */
    private int bgNextTileId;
    private int bgNextTileAttrib;
    private int bgNextTileLsb;
    private int bgNextTileMsb;

    private int bgShiftPatternLow;
    private int bgShiftPatternHigh;
    private int bgShiftAttribLow;
    private int bgShiftAttribHigh;

    /**
     * PPU Memories
     */
    int [][] nametable;
    int [][] patternMemory;
    int []   paletteMemory;
    int []   oamMemory;

    NametableMirror nametableMirror;


    /**
     * PPU requires an overloaded constructor to account for the extra bus connections
     * to char rom in cartridge
     * @param clock Clock to respond to ticks
     * @param addressBus CPU address bus
     * @param dataBus CPU data bus
     * @param flgCpuRW CPU RW flag
     * @param ppuAddressBus PPU address bus
     * @param ppuDataBus PPU data bus
     * @param ppuRwRWFlag PPU RW flag
     * @param flgCpuNmi CPU NMI flag
     * @param flgCpuHalt CPU halt flag
     * @param nametableMirror Nametable mirror object. Allows for cart to adjust mirroring on the fly.
     */
    public NES2C02(Clock clock, IntegerBus addressBus, IntegerBus dataBus, RWFlag flgCpuRW,
                   IntegerBus ppuAddressBus, IntegerBus ppuDataBus, RWFlag ppuRwRWFlag,
                   NMIFlag flgCpuNmi, HaltFlag flgCpuHalt, NametableMirror nametableMirror){
        super(clock, addressBus, dataBus, flgCpuRW);

        this.ppuAddressBus = ppuAddressBus;
        this.ppuDataBus = ppuDataBus;
        this.ppuRwRWFlag = ppuRwRWFlag;
        this.flgRW.addListener(this);
        this.nametableMirror = nametableMirror;

        this.flgCpuNmi = flgCpuNmi;
        this.flgCpuHalt = flgCpuHalt;
        this.flgCpuRW = flgCpuRW;

        reset();
    }

    private void reset(){
        this.regPPUCTRL     = 0;
        this.regPPUMASK     = 0x0000;
        this.regPPUSTATUS   = 0x0000;
        this.regOAMADDR     = 0x0000;
        this.regOAMDATA     = 0x0000;
        this.regPPUSCROLL   = 0x0000;
        this.regPPUADDR     = 0x0000;
        this.regPPUDATA     = 0x0000;
        this.regOddFrame    = false;

        this.screenBufferOdd = new int[NES2C02Constants.WIDTH_VISIBLE_PIXELS * NES2C02Constants.HEIGHT_VISIBLE_SCANLINES];
        this.screenBufferEven = new int[NES2C02Constants.WIDTH_VISIBLE_PIXELS * NES2C02Constants.HEIGHT_VISIBLE_SCANLINES];

        this.scanline = 0;
        this.cycle = 0;

        this.bgNextTileId        = 0x00;
        this.bgNextTileAttrib    = 0x00;
        this.bgNextTileLsb       = 0x00;
        this.bgNextTileMsb       = 0x00;

        this.bgShiftPatternLow   = 0x00;
        this.bgShiftPatternHigh  = 0x00;
        this.bgShiftAttribLow    = 0x00;
        this.bgShiftAttribHigh   = 0x00;

        this.vramAddress = new LoopyRegister();
        this.tramAddress = new LoopyRegister();

        this.nametable      = new int[2][1024];
        this.patternMemory  = new int[2][4096];
        this.paletteMemory  = new int[32];
        this.oamMemory      = new int[256];
        this.fineX = 0x00;
    }

    /**
     * Used to keep graphics thread separate. Can also do rudimentary double
     * buffering by holding two arrays
     * @return int[] of pixel values
     */
    public int[] getScreenBuffer(){

        int[] returnBuffer = new int[screenBufferEven.length];

        if (regOddFrame){
            synchronized (screenBufferEven){
                System.arraycopy(this.screenBufferEven, 0, returnBuffer, 0, screenBufferEven.length);
            }
        } else {
            synchronized (screenBufferOdd){
                System.arraycopy(this.screenBufferOdd, 0, returnBuffer, 0, screenBufferOdd.length);
            }
        }

        return returnBuffer;
    }


    /**
     * Sets a status flag to the status register
     * @param flag Flag to enable
     */
    private void setStatusFlag (int flag) {
        this.regPPUSTATUS = this.regPPUSTATUS | flag;
    }

    /**
     * Clears a status flag from the flag register
     * @param flag Flag to clear
     */
    private void clearStatusFlag (int flag) {
        this.regPPUSTATUS = this.regPPUSTATUS & (~flag);
    }

    private boolean isFlagSet(int register, int flag){
        return (flag & register) != 0;
    }

    /**
     *
     */
    private void incrementScrollX() {
        if(isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND) ||
                isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_SPRITES)){

            if (vramAddress.getCoarseX() == 31){
                vramAddress.setCoarseX(0);
                vramAddress.setNametableX(~vramAddress.getNametableX());
            } else {
                vramAddress.setCoarseX(vramAddress.getCoarseX() + 1);
            }
        }
    }

    /**
     *
     */
    private void incrementScrollY() {
        if(isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND) ||
                isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_SPRITES)){

            if (vramAddress.getFineY() < 7){
                vramAddress.setFineY(vramAddress.getFineY() + 1);
            } else {
                vramAddress.setFineY(0);

                if (vramAddress.getCoarseY() == 29){
                    vramAddress.setCoarseY(0);
                    vramAddress.setNametableY(~vramAddress.getNametableY());
                } else if (vramAddress.getCoarseY() == 31) {
                    vramAddress.setCoarseY(0);
                } else {
                    vramAddress.setCoarseY(vramAddress.getCoarseY() + 1);
                }
            }
        }
    }

    /**
     *
     */
    private void transferAddressX() {
        if(isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND) ||
                isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_SPRITES)) {
            vramAddress.setNametableX(tramAddress.getNametableX());
            vramAddress.setCoarseX(tramAddress.getCoarseX());
        }
    }

    /**
     *
     */
    private void transferAddressY() {
        if(isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND) ||
                isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_SPRITES)) {
            vramAddress.setFineY(tramAddress.getFineY());
            vramAddress.setNametableY(tramAddress.getNametableY());
            vramAddress.setCoarseY(tramAddress.getCoarseY());
        }
    }

    /**
     *
     */
    private void loadBackgroundShifters () {
        this.bgShiftPatternLow = (bgShiftPatternLow & 0xFF00) | bgNextTileLsb;
        this.bgShiftPatternHigh = (bgShiftPatternHigh & 0xFF00) | bgNextTileMsb;
        this.bgShiftAttribLow = (bgShiftAttribLow & 0xFF00) | (((bgNextTileAttrib & 0b01) != 0) ? 0xFF : 0x00);
        this.bgShiftAttribHigh = (bgShiftAttribHigh & 0xFF00) | (((bgNextTileAttrib & 0b10) != 0) ? 0xFF : 0x00);
    }

    /**
     *
     */
    private void updateShifters () {
        if (isFlagSet(this.regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND)){
            bgShiftPatternLow <<= 1;
            bgShiftPatternHigh <<= 1;
            bgShiftAttribLow <<= 1;
            bgShiftAttribHigh <<= 1;
        }
    }


    /**
     * Reads the memory attached to the PPU address and memory bus
     * @param address
     * @return
     */
    private int ppuRead(int address) throws ProcessorException{

        int data = 0x00;
        address &= 0x3FFF;

        if (address < 0x2000) {
            /*
             * Load from cartridge
             */
            try{
                this.ppuAddressBus.writeDataToBus(address);
                this.ppuRwRWFlag.setFlagValue(RWFlag.READ);
                data = this.ppuDataBus.readDataFromBus();
            } catch (InvalidBusDataException | FlagException e) {
                ProcessorException pe = new ProcessorException(NES2C02Constants.EX_PPU_READ_FAIL);
                pe.addSuppressed(e);
                throw pe;
            }

        }
        else if (address >= 0x2000 && address <= 0x3EFF){
            /*
             * Load from nametalbes
             */
            address &= 0x0FFF;

            NESNametableMirroring tmpMirr = this.nametableMirror.getMirrorMode();

            if (tmpMirr == NESNametableMirroring.VERTICAL){

                if (address >= 0x0000 && address <= 0x03FF)
                    data = nametable[0][address & 0x03FF];
                if (address >= 0x0400 && address <= 0x07FF)
                    data = nametable[1][address & 0x03FF];
                if (address >= 0x0800 && address <= 0x0BFF)
                    data = nametable[0][address & 0x03FF];
                if (address >= 0x0C00 && address <= 0x0FFF)
                    data = nametable[1][address & 0x03FF];
            } else {
                if (address >= 0x0000 && address <= 0x03FF)
                    data = nametable[0][address & 0x03FF];
                if (address >= 0x0400 && address <= 0x07FF)
                    data = nametable[0][address & 0x03FF];
                if (address >= 0x0800 && address <= 0x0BFF)
                    data = nametable[1][address & 0x03FF];
                if (address >= 0x0C00 && address <= 0x0FFF)
                    data = nametable[1][address & 0x03FF];
            }

        } else if (address >= 0x3F00 && address <= 0x3FFF){
            address &= 0x001F;
            if (address == 0x0010) address = 0x0000;
            if (address == 0x0014) address = 0x0004;
            if (address == 0x0018) address = 0x0008;
            if (address == 0x001C) address = 0x000C;
            data = paletteMemory[address] & (isFlagSet(regPPUMASK, NES2C02MaskFlags.GREYSCALE) ? 0x30 : 0x3F);
        }

        return data;
    }

    private void ppuWrite(int address, int data) throws ProcessorException {

        address &= 0x3FFF;

        if (address >= 0x2000 && address <= 0x3EFF){
            /*
             * Load from nametalbes
             */
            address &= 0x0FFF;

            NESNametableMirroring tmpMirr = this.nametableMirror.getMirrorMode();

            if (tmpMirr == NESNametableMirroring.VERTICAL){

                if (address >= 0x0000 && address <= 0x03FF)
                    nametable[0][address & 0x03FF] = data;
                if (address >= 0x0400 && address <= 0x07FF)
                    nametable[1][address & 0x03FF] = data;
                if (address >= 0x0800 && address <= 0x0BFF)
                    nametable[0][address & 0x03FF] = data;
                if (address >= 0x0C00 && address <= 0x0FFF)
                    nametable[1][address & 0x03FF] = data;
            } else {
                if (address >= 0x0000 && address <= 0x03FF)
                    nametable[0][address & 0x03FF] = data;
                if (address >= 0x0400 && address <= 0x07FF)
                    nametable[0][address & 0x03FF] = data;
                if (address >= 0x0800 && address <= 0x0BFF)
                    nametable[1][address & 0x03FF] = data;
                if (address >= 0x0C00 && address <= 0x0FFF)
                    nametable[1][address & 0x03FF] = data;
            }

        } else if (address >= 0x3F00 && address <= 0x3FFF){
            address &= 0x001F;
            if (address == 0x0010) address = 0x0000;
            if (address == 0x0014) address = 0x0004;
            if (address == 0x0018) address = 0x0008;
            if (address == 0x001C) address = 0x000C;
            paletteMemory[address] = data;
        }
    }

    private int getColourFromPalette(int palette, int pixel) throws ProcessorException {
        int colorIndex = ppuRead(0x3F00 + (palette << 2) + pixel) & 0x3F;
        return NES2C02Constants.PALETTE[colorIndex];
    }


    @Override
    public void onTick() throws ProcessorException {


        if (scanline >= -1 && scanline < 240) {

            /*
             * Odd frame cycle skip
             */
            if (scanline == 0 && cycle == 0)
                cycle = 1;

            if (scanline == -1 && cycle == 1)
                clearStatusFlag(NES2C02StatusFlags.VBLANK_STARTED);

            if ((cycle >= 2 && cycle < 258) || (cycle >= 321 && cycle < 338)) {
                updateShifters();

                switch ((cycle - 1) % 8){
                    case 0:
                        loadBackgroundShifters();
                        bgNextTileId = ppuRead(0x2000 | (vramAddress.getValue() & 0x0FFF));
                        break;
                    case 2:
                        bgNextTileAttrib = ppuRead( 0x23C0
                                | (vramAddress.getNametableY() << 11)
                                | (vramAddress.getNametableX() << 10)
                                | ((vramAddress.getCoarseY() >> 2) << 3)
                                | (vramAddress.getCoarseX() >> 2));
                        if ((vramAddress.getCoarseY() & 0x02) != 0) bgNextTileAttrib >>= 4;
                        if ((vramAddress.getCoarseX() & 0x02) != 0) bgNextTileAttrib >>= 2;
                        bgNextTileAttrib &= 0x03;
                        break;
                    case 4:
                        bgNextTileLsb = ppuRead(((this.regPPUCTRL & NES2C02ControllerFlags.BACKGROUND_PATTERN_ADDRESS) << 8)
                                                + ((bgNextTileId & 0x00FFF) << 4)
                                                + (vramAddress.getFineY()));
                        break;
                    case 6:
                        bgNextTileMsb = ppuRead(((this.regPPUCTRL & NES2C02ControllerFlags.BACKGROUND_PATTERN_ADDRESS) << 8)
                                + ((bgNextTileId & 0x0000FFF) << 4)
                                + (vramAddress.getFineY()) + 8);
                        break;
                    case 7:
                        incrementScrollX();
                        break;
                }
            }

            if (cycle == 256)
                incrementScrollY();

            if (cycle == 257){
                loadBackgroundShifters();
                transferAddressX();
            }

            if (cycle == 338 || cycle == 340)
                bgNextTileId = ppuRead(0x2000 | (vramAddress.getValue() & 0x0FFF));

            if (scanline == -1 && cycle >= 280 && cycle < 305)
                transferAddressY();
        }

        if (scanline == 240){
            // Does nothing
        }

        if (scanline >= 241 && scanline < 261){
            if (scanline == 241 && cycle == 1){
                setStatusFlag(NES2C02StatusFlags.VBLANK_STARTED);
                if (isFlagSet(regPPUCTRL, NES2C02ControllerFlags.GENERATE_NMI_START_VBLANK)) {
                    try {
                        this.flgCpuNmi.setFlagValue(NMIFlag.NMI);
                    } catch (FlagException e) {
                        ProcessorException pe = new ProcessorException(NES2C02Constants.EX_CPU_NMI);
                        pe.addSuppressed(e);
                        throw pe;
                    }
                }
            }
        }

        int bgPixel = 0x00;
        int bgPalette = 0x00;

        if (isFlagSet(regPPUMASK, NES2C02MaskFlags.SHOW_BACKGROUND)){
            int bitMux = 0x8000 >> fineX;

            int p0Pixel = ((bgShiftPatternLow & bitMux) > 0) ? 0b01 : 0b00;
            int p1Pixel = ((bgShiftPatternHigh & bitMux) > 0) ? 0b10 : 0b00;
            bgPixel = p0Pixel | p1Pixel;

            int p0Bg = ((bgShiftAttribLow & bitMux) > 0) ? 0b01 : 0b00;
            int p1Bg = ((bgShiftAttribHigh & bitMux) > 0) ? 0b10 : 0b00;
            bgPalette = p0Bg | p1Bg;
        }

        if (cycle -1 > 0 && cycle - 1 < 256 && scanline > -1 && scanline < 240){

            int c = getColourFromPalette(bgPalette, bgPixel);

            if (regOddFrame)
                this.screenBufferOdd[(scanline * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + (cycle - 1)] = c;
            else
                this.screenBufferEven[(scanline * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + (cycle - 1)] = c;
        }

        cycle++;
        if (cycle >= 341){
            cycle = 0;
            scanline ++;
            if (scanline > 261){
                scanline = -1;
                regOddFrame = !regOddFrame;
            }
        }
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {
        // Respond to CPU flags for registers
        if (flag instanceof RWFlag){

            /*
             * Check in range of PPU Registers
             */
            int tmpAddress = this.addressBus.readDataFromBus();
            int address = this.addressBus.readDataFromBus();

            if (address >= NES2C02Constants.REG_MINIMUM_ADDRESS && address <= NES2C02Constants.REG_MAXIMUM_ADDRESS){
                /*
                 * PPU registers are mirrored every 8 bytes so need to apply a mask
                 */
                address = address & NES2C02Constants.REG_MASK;

                try {
                    if (flag.getFlagValue() == RWFlag.READ) {
                        /*
                         * Reading from registers
                         */
                        switch (address) {
                            case NES2C02Constants.REG_PPUCTRL:
                            case NES2C02Constants.REG_PPUMASK:
                            case NES2C02Constants.REG_OAMADDR:
                            case NES2C02Constants.REG_OAMDATA:
                                this.dataBus.writeDataToBus(this.oamMemory[this.regPPUADDR]);
                                break;
                            case NES2C02Constants.REG_PPUSCROLL:
                                this.dataBus.writeDataToBus(0x00);
                                break;
                            case NES2C02Constants.REG_PPUSTATUS:
                                //setStatusFlag(NES2C02StatusFlags.VBLANK_STARTED);
                                //this.dataBus.writeDataToBus((this.regPPUSTATUS & 0xE0) | (this.dataBuffer & 0x1F));
                                this.dataBus.writeDataToBus(this.regPPUSTATUS);
                                clearStatusFlag(NES2C02StatusFlags.VBLANK_STARTED);
                                this.addressLatch = 0;
                                break;
                            case NES2C02Constants.REG_PPUDATA:
                                int data = this.dataBuffer;

                                this.dataBuffer = ppuRead(this.vramAddress.getValue());

                                // In the palette memory range reads are instantaneous
                                if (this.vramAddress.getValue() >= 0x3F00) data = this.dataBuffer;

                                this.dataBus.writeDataToBus(data);
                                this.vramAddress = new LoopyRegister(this.vramAddress.getValue() +
                                        (isFlagSet(this.regPPUCTRL, NES2C02ControllerFlags.VRAM_ADDR_INC_PER_RW_PPUDATA) ? 32 : 1));
                                break;
                        }

                    } else if (flag.getFlagValue() == RWFlag.WRITE) {
                        /*
                         * Writing to registers
                         */
                        int data = this.dataBus.readDataFromBus();

                        switch (address) {
                            case NES2C02Constants.REG_PPUSTATUS:
                            case NES2C02Constants.REG_OAMADDR:
                                this.regOAMADDR = data & 0x00FF;
                                break;
                            case NES2C02Constants.REG_OAMDATA:
                                oamMemory[regOAMDATA] = data;
                                break;
                            case NES2C02Constants.REG_PPUCTRL:
                                this.regPPUCTRL = data;
                                this.tramAddress.setNametableX(isFlagSet(this.regPPUCTRL, NES2C02ControllerFlags.BASE_NAMETABLE_ADDRESS_LOW) ? 1 : 0);
                                this.tramAddress.setNametableY(isFlagSet(this.regPPUCTRL, NES2C02ControllerFlags.BASE_NAMETABLE_ADDRESS_HIGH) ? 1 : 0);
                                break;
                            case NES2C02Constants.REG_PPUMASK:
                                this.regPPUMASK = data;
                                break;
                            case NES2C02Constants.REG_PPUSCROLL:
                                if (this.addressLatch == 0){
                                    this.fineX = data & 0x07;
                                    this.tramAddress.setCoarseX(data >> 3);
                                    this.addressLatch = 1;
                                } else {
                                    tramAddress.setFineY(data & 0x07);
                                    tramAddress.setCoarseY(data >> 3);
                                    this.addressLatch = 0;
                                }
                                break;
                            case NES2C02Constants.REG_PPUADDR:
                                if (this.addressLatch == 0) {
                                    this.tramAddress = new LoopyRegister(((data & 0x3F) << 8) | (this.tramAddress.getValue() & 0x00FF) );
                                    addressLatch = 1;
                                } else {
                                    this.tramAddress = new LoopyRegister((this.tramAddress.getValue() & 0xFF00) | data);
                                    this.vramAddress = new LoopyRegister(tramAddress.getValue());
                                    addressLatch = 0;
                                }
                                break;
                            case NES2C02Constants.REG_PPUDATA:
                                int addr = this.vramAddress.getValue();
                                ppuWrite(this.vramAddress.getValue(), data);
                                //System.out.println("PPU WRITE TO : " + String.format("%02X", this.vramAddress.getValue()) + " " +  String.format("%02X", data));
                                this.vramAddress = new LoopyRegister(this.vramAddress.getValue() +
                                            (isFlagSet(this.regPPUCTRL, NES2C02ControllerFlags.VRAM_ADDR_INC_PER_RW_PPUDATA) ? 32 : 1));
                                //System.out.println(this.vramAddress.getValue());
                                break;
                        }
                    }
                } catch (InvalidBusDataException | ProcessorException e) {
                    e.printStackTrace();
                    MemoryException me = new MemoryException(e.getMessage());
                    me.addSuppressed(e);
                    FlagException fe = new FlagException(e.getMessage());
                    fe.addSuppressed(me);
                    throw fe;
                }

            }

            /*
             * When writing to this register, this triggers DMA for the PPU to
             * Start loading from the page written to this register
             */
            if (address == NES2C02Constants.REG_OAMDMA && flag.getFlagValue() == RWFlag.WRITE){
                // TODO start the interrupt process
            }
        }
    }
}
