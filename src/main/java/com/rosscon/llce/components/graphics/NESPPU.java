package com.rosscon.llce.components.graphics;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
import javafx.scene.image.PixelWriter;

import java.util.Random;

public class NESPPU extends Processor {

    private PixelWriter pixelWriter;

    int x = 0;
    int y = 0;

    Random rand = new Random();

    long startOfFrame;
    long endOfFrame;

    public NESPPU(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag, PixelWriter pixelWriter){
        super(clock, addressBus, dataBus, rwFlag);
        this.pixelWriter = pixelWriter;
    }

    public NESPPU(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(clock, addressBus, dataBus, rwFlag);
    }

    @Override
    public void onTick() throws ProcessorException {

        if (this.x == 0 && this.y == 0){
            this.startOfFrame = System.nanoTime();
        }

        int c = (rand.nextInt(Integer.MAX_VALUE) & 0x00FFFFFF) | 0xFF000000;
        this.pixelWriter.setArgb(this.x, this.y, c);

        this.x++;
        if (this.x >= 256){
            this.x = 0;
            this.y ++;
        }
        if (this.y >= 240){
            this.y = 0;
            this.endOfFrame = System.nanoTime();

            //System.out.println(((float) 1 / ((this.endOfFrame - this.startOfFrame) / 1000000000f)) + "FPS");
        }
    }
}
