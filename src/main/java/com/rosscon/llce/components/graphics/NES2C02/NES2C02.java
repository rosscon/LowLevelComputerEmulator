package com.rosscon.llce.components.graphics.NES2C02;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
import javafx.scene.image.PixelWriter;

public class NES2C02 extends Processor implements FlagListener {

    public static PixelWriter pixelWriter;

    int x = 0;
    int y = 0;
    int count = 0;

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
    private int     regPPUSCROLL;
    private int     regPPUADDR;
    private boolean regOddFrame;



    /**
     * PPU requires an overloaded constructor to account for the extra bus connections
     * to char rom in cartridge
     * @param clock Clock to respond to ticks
     * @param addressBus CPU address bus
     * @param dataBus CPU data bus
     * @param cpuRwFlag CPU RW flag
     * @param ppuAddressBus PPU address bus
     * @param ppuDataBus PPU data bus
     * @param ppuRwFlag PPU RW flag
     */
    public NES2C02(Clock clock, IntegerBus addressBus, IntegerBus dataBus, Flag cpuRwFlag,
                   IntegerBus ppuAddressBus, IntegerBus ppuDataBus, Flag ppuRwFlag){
        super(clock, addressBus, dataBus, cpuRwFlag);
        //this.pixelWriter = pixelWriter;
        this.rwFlag.addListener(this::onFlagChange);
        reset();
    }

    private void reset(){
        this.regPPUCTRL     = 0;
        this.regPPUMASK     = 0;
        this.regPPUSTATUS   = 0;
        this.regOAMADDR     = 0;
        this.regPPUSCROLL   = 0;
        this.regPPUADDR     = 0;
        this.regOddFrame    = false;

        this.screenBufferOdd = new int[NES2C02Constants.WIDTH_VISIBLE_PIXELS * NES2C02Constants.HEIGHT_VISIBLE_SCANLINES];
        this.screenBufferEven = new int[NES2C02Constants.WIDTH_VISIBLE_PIXELS * NES2C02Constants.HEIGHT_VISIBLE_SCANLINES];
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

    @Override
    public void onTick() throws ProcessorException {

        if (this.x == 0 && this.y == 0) {
            this.startOfFrame = System.nanoTime();
        }

        int c = NES2C02Constants.PALETTE[(this.y + this.count) % 0x40];

        if (x < NES2C02Constants.WIDTH_VISIBLE_PIXELS & y < NES2C02Constants.HEIGHT_VISIBLE_SCANLINES) {
            if (regOddFrame)
                this.screenBufferOdd[(y * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + x] = c;
            else
                this.screenBufferEven[(y * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + x] = c;
        }

        this.x++;

        if (this.x > NES2C02Constants.WIDTH_TOTAL_PIXELS){
            this.x = 0;
            this.y++;
        }

        if (y > NES2C02Constants.HEIGHT_TOTAL_SCANLINES){
            y = 0;
            this.count = (this.count + 1) % 0x40;
            this.endOfFrame = System.nanoTime();
            this.regOddFrame = !this.regOddFrame;
        }
    }

    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {
        // Respond to CPU flags for registers
        if (flag == this.rwFlag){
            //TODO respond to registers
        }
    }
}
