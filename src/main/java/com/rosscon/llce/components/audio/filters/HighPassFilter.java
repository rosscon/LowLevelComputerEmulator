package com.rosscon.llce.components.audio.filters;

/**
 * High and low pass filter inspired by
 * https://stackoverflow.com/questions/28291582/implementing-a-high-pass-filter-to-an-audio-signal
 */

public class HighPassFilter extends Filter {

    public HighPassFilter(float frequency, int sampleRate, float resonance){
        super(frequency, sampleRate, resonance);

        c = (float)Math.tan(Math.PI * frequency / sampleRate);
        a1 = 1.0f / (1.0f + resonance * c + c * c);
        a2 = -2f * a1;
        a3 = a1;
        b1 = 2.0f * (c * c - 1.0f) * a1;
        b2 = (1.0f - resonance * c + c * c) * a1;
    }
}
