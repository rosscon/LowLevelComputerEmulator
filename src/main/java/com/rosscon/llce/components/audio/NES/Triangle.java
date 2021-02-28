package com.rosscon.llce.components.audio.NES;

public class Triangle extends Channel{

    public Triangle(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public short getSample() {
        return 0;
    }
}
