package com.rosscon.llce.components.graphics.NES2C02;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
import javafx.scene.image.PixelWriter;

import java.util.Random;

public class NES2C02 extends Processor implements FlagListener {

    private PixelWriter pixelWriter;

    int x = 0;
    int y = 0;
    int count = 0;

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
     * @param pixelWriter PixelWriter to display graphics
     */
    public NES2C02(Clock clock, Bus addressBus, Bus dataBus, Flag cpuRwFlag,
                   Bus ppuAddressBus, Bus ppuDataBus, Flag ppuRwFlag, PixelWriter pixelWriter){
        super(clock, addressBus, dataBus, cpuRwFlag);
        this.pixelWriter = pixelWriter;
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
    }

    @Override
    public void onTick() throws ProcessorException {


        // Null PPU behaviour just draws colours to the pixel writer
        if (this.pixelWriter != null) {
            if (this.x == 0 && this.y == 0) {
                this.startOfFrame = System.nanoTime();
            }

            int c = NES2C02Constants.PALETTE[(this.y + this.count) % 0x40];

            for (int dx = 0; dx < NES2C02Constants.GRAPHICS_SCALING; dx ++){
                for (int dy = 0; dy < NES2C02Constants.GRAPHICS_SCALING; dy++){
                    this.pixelWriter.setArgb(this.x + dx, this.y + dy, c);
                }
            }

            this.x += NES2C02Constants.GRAPHICS_SCALING;
            if (this.x >= NES2C02Constants.GRAPHICS_SCALING * NES2C02Constants.WIDTH_VISIBLE_PIXELS) {
                this.x = 0;
                this.y += NES2C02Constants.GRAPHICS_SCALING;
            }
            if (this.y >= NES2C02Constants.GRAPHICS_SCALING * NES2C02Constants.HEIGHT_VISIBLE_SCANLINES) {
                this.y = 0;
                this.endOfFrame = System.nanoTime();
                count++;
                count = count % (0x40 * NES2C02Constants.GRAPHICS_SCALING);

                //System.out.println(((float) 1 / ((this.endOfFrame - this.startOfFrame) / 1000000000f)) + "FPS");
            }
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
