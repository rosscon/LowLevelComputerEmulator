package com.rosscon.llce.components.audio.NES;


public class DMC extends Channel{

    public DMC(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public double getSample() {
        return 0;
    }
}
