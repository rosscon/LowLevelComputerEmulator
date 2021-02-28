package com.rosscon.llce.components.audio.NES;


public class DMC extends Channel{

    public DMC(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public short getSample() {
        return 0;
    }
}
