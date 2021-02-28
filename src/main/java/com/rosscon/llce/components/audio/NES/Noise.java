package com.rosscon.llce.components.audio.NES;

public class Noise extends Channel {

    public Noise(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public short getSample() {
        return 0;
    }
}
