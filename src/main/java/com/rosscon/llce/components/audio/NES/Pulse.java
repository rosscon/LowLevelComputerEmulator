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
    private double angle;

    int currentSample;

    int sweepCount;



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


    public void setConstantVolume(boolean constantVolume) {
        this.constantVolume = constantVolume;
    }


    public void setVolumeEnvelope(int volumeEnvelope) {
        this.volumeEnvelope = volumeEnvelope;
    }


    public void setSweepEnable(boolean sweepEnable) {
        this.sweepEnable = sweepEnable;
    }


    public void setPeriod(int period) {
        this.period = (period + 1) * 2;
    }


    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setShift(int shift) {
        this.shift = shift;
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

        currentSample = 0;

        sweepCount = 0;
    }

    @Override
    public int getSample() {
        double dutyCycle = 0;
        if (duty == 0) dutyCycle = 0.125;
        if (duty == 1) dutyCycle = 0.25;
        if (duty == 2) dutyCycle = 0.5;
        if (duty == 3) dutyCycle = 0.75;


        if (this.lengthCounter == 0) return 0;
        if (this.timer < 0) return 0;

        int frequency = 1789773 / (16 * (this.timer + 1));
        if (frequency == 0) return 0;

        double angleIncrement = (double)frequency / this.sampleRate;
        angle += angleIncrement;

        if (angle > 1.0d) angle = -1.0d;

        if (this.period >= (0x07FF * 2) || this.period < 8 )
            return 0;

        int sample;

        if ((angle + 1) < (dutyCycle * 2)){
            sample = 1;
        } else {
            sample = 0;
        }

        if (constantVolume){
            sample = sample * 16;
        } else {
            sample = sample * volumeEnvelope;
        }

        return sample;
    }

    @Override
    public void onFrameCounter(){
        if (!isEnvLoopLengthCounterHalt()){
            lengthCounter--;
        }
        if (sweepEnable){
            sweepCount++;
            if (sweepCount == this.period){

                if (negate) {
                    this.timer = this.timer >>> this.shift;
                } else {
                    this.timer = (this.timer << this.shift) & 0b011111111111;
                }
                sweepCount = 0;
            }
        }
    }
}
