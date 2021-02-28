package com.rosscon.llce.components.audio.NES;

public class Triangle extends Channel{

    public Triangle(int sampleRate) {
        super(sampleRate);
    }

    @Override
    public double getSample() {
        return 0;
    }
}
