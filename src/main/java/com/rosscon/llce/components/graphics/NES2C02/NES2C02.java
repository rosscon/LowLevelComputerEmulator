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

    public NES2C02(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag,
                   Bus ppuAddressBus, Bus ppuDataBus, Flag ppuRwFlag, PixelWriter pixelWriter){
        super(clock, addressBus, dataBus, rwFlag);
        this.pixelWriter = pixelWriter;
    }

    public NES2C02(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(clock, addressBus, dataBus, rwFlag);
    }

    @Override
    public void onTick() throws ProcessorException {



        // Null PPU behaviour just draws colours to the pixel writer
        if (this.pixelWriter != null) {
            if (this.x == 0 && this.y == 0) {
                this.startOfFrame = System.nanoTime();
            }

            int c = NES2C02Constants.PALETTE[(this.y + this.count) % 0x40];
            this.pixelWriter.setArgb(this.x, this.y, c);

            this.x++;
            if (this.x >= 256) {
                this.x = 0;
                this.y++;
            }
            if (this.y >= 240) {
                this.y = 0;
                this.endOfFrame = System.nanoTime();
                count++;
                count = count % 0x40;

                //System.out.println(((float) 1 / ((this.endOfFrame - this.startOfFrame) / 1000000000f)) + "FPS");
            }
        }
    }

    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {
        // Respond to CPU flags for registers
        if (flag == this .rwFlag){
            //TODO respond to registers
        }
    }
}
