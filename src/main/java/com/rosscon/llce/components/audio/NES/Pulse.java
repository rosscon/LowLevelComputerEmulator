package com.rosscon.llce.components.audio.NES;


public class Pulse extends Channel {

    /*
     * Values from 0x4000 & 0x4004
     */
    private int duty;
    private boolean envLoopLengthCounterHalt;
    private boolean constantVolume;
    private int volumeEnvelope;

    /*
     * Values from 0x4001 & 0x4005
     */
    private boolean sweepEnable;
    private int period;
    private boolean negate;
    private int shift;

    /*
     * Values from 0x4002 & 0x4006
     */
    private int envelope;
    private int sweep;

    /**
     * Keep track of sine wave angle
     */
    private float angle;


    public int getDuty() {
        return duty;
    }

    public void setDuty(int duty) {
        this.duty = duty;
    }

    public boolean isEnvLoopLengthCounterHalt() {
        return envLoopLengthCounterHalt;
    }

    public void setEnvLoopLengthCounterHalt(boolean envLoopLengthCounterHalt) {
        this.envLoopLengthCounterHalt = envLoopLengthCounterHalt;
    }

    public boolean isConstantVolume() {
        return constantVolume;
    }

    public void setConstantVolume(boolean constantVolume) {
        this.constantVolume = constantVolume;
    }

    public int getVolumeEnvelope() {
        return volumeEnvelope;
    }

    public void setVolumeEnvelope(int volumeEnvelope) {
        this.volumeEnvelope = volumeEnvelope;
    }

    public boolean isSweepEnable() {
        return sweepEnable;
    }

    public void setSweepEnable(boolean sweepEnable) {
        this.sweepEnable = sweepEnable;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getEnvelope() {
        return envelope;
    }

    public void setEnvelope(int envelope) {
        this.envelope = envelope;
    }

    public int getSweep() {
        return sweep;
    }

    public void setSweep(int sweep) {
        this.sweep = sweep;
    }

    public Pulse(int sampleRate){
        super(sampleRate);

        duty = 0;
        envLoopLengthCounterHalt = false;
        constantVolume = false;
        volumeEnvelope = 0;

        sweepEnable = true;
        period = 0;
        negate = false;
        shift = 0;

        envelope = 0;
        sweep = 0;
    }

    @Override
    public double getSample() {
        if (this.lengthCounter == 0) return 0;

        double frequency = 1789773.0 / (16.0d * (this.timer + 1.0d));

        if (frequency == 0.0f) return 0;

        double angleIncrement = frequency / this.sampleRate;
        //short sample = (short)(Short.MAX_VALUE * approxsin(2*Math.PI * angle));
        double sample = approxsin(2*Math.PI * angle);

        angle += angleIncrement;
        if (angle > 1.0) angle = -1.0f;

        return sample;
    }

    private double approxsin (double t) {
        double j = t * 0.15915f;
        j = j - (int)j;
        return 20.785f * j * (j - 0.5f) * (j - 1.0f);
    }
}
