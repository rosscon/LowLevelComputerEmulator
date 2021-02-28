package com.rosscon.llce.components.audio;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;

import javax.sound.sampled.LineUnavailableException;

public class SineWave extends AudioProcessor {

    private float angle;

    protected double frequency;

    protected double volume;

    public SineWave(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, double frequency) throws LineUnavailableException {
        super(addressBus, dataBus, rwFlag);
        angle = 0.0f;
        this.frequency = frequency;
        volume = 1;
    }

    @Override
    public short getSample() {

        if (frequency == 0.0f) return 0;

        double angleIncrement = frequency / SAMPLE_RATE;
        short sample = (short)(Short.MAX_VALUE * Math.sin(2*Math.PI * angle));

        angle += angleIncrement;
        if (angle > 1.0) angle = -1.0f;

        return sample;
    }

    private float approxsin (float t) {
        float j = t * 0.15915f;
        j = j - (int)j;
        return 20.785f * j * (j - 0.5f) * (j - 1.0f);
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {

    }

}
