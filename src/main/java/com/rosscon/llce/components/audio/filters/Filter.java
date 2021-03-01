package com.rosscon.llce.components.audio.filters;

public class Filter {

    protected float c, a1, a2, a3, b1, b2;

    protected float[] inputHistory;

    protected float[] outputHistory;

    public Filter(float frequency, int sampleRate, float resonance){
        this.inputHistory = new float[2];
        this.outputHistory = new float[3];
    }

    public float getNext(float nextValue){
        float newOutput = a1 * nextValue + a2 * this.inputHistory[0] +
                a3 * this.inputHistory[1] - b1 * this.outputHistory[0] -
                b2 * this.outputHistory[1];

        this.inputHistory[1] = this.inputHistory[0];
        this.inputHistory[0] = nextValue;

        this.outputHistory[2] = this.outputHistory[1];
        this.outputHistory[1] = this.outputHistory[0];
        this.outputHistory[0] = newOutput;

        return this.outputHistory[0];
    }
}
