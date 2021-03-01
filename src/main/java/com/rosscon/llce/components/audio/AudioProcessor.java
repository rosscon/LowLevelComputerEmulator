package com.rosscon.llce.components.audio;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.FlagListener;
import com.rosscon.llce.components.flags.RWFlag;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;

public abstract class AudioProcessor implements Runnable, FlagListener {

    /**
     * 44.1KHz sample rate
     */
    protected static final int SAMPLE_RATE = 44100;

    /**
     * Number of bytes per sample
     */
    protected static final int SAMPLE_SIZE = 2;

    /**
     * 10 ms buffer
     */
    protected static final double BUFFER_DURATION = 0.100;

    /**
     * buffer size
     */
    final static public int PACKET_SIZE = (int)(BUFFER_DURATION * SAMPLE_RATE * SAMPLE_SIZE);

    protected SourceDataLine line;

    //protected long time;

    protected IntegerBus addressBus;

    protected IntegerBus dataBus;


    public AudioProcessor(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag) throws LineUnavailableException {
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        rwFlag.addListener(this);

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {

        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE * 8, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, PACKET_SIZE * 2);

            if (!AudioSystem.isLineSupported(info))
                throw new LineUnavailableException();

            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
        }
        catch (LineUnavailableException e) {
            System.out.println("Line of that type is not available");
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Requested line buffer size = " + PACKET_SIZE *2);
        System.out.println("Actual line buffer size = " + line.getBufferSize());

        ByteBuffer buff = ByteBuffer.allocate(PACKET_SIZE);

        while (true) {
            buff.clear();

            for (int i = 0; i < PACKET_SIZE /SAMPLE_SIZE; i++) {
                buff.putShort(getSample());
            }

            line.write(buff.array(), 0, buff.position());

            try {
                while ((line.getBufferSize() - line.available()) > (PACKET_SIZE * 2))
                    Thread.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    public abstract short getSample();
}
