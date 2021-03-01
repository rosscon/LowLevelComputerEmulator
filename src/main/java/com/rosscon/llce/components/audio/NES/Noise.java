package com.rosscon.llce.components.audio.NES;

public class Noise extends Channel {

    public Noise(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public int getSample() {
        return 0;
    }
}
