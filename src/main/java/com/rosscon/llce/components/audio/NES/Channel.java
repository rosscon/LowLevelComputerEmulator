package com.rosscon.llce.components.audio.NES;


public abstract class Channel {

    protected int timer;
    protected int lengthCounter;

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
        //System.out.println("timer " +  timer);
    }

    public int getLengthCounter() {
        return lengthCounter;
    }

    public void setLengthCounter(int lengthCounter) {
        this.lengthCounter = lengthCounter * 4;
        //System.out.println("lengthCounter " +  lengthCounter);
    }

    protected final int sampleRate;

    public Channel(int sampleRate){
        this.timer = 0;
        this.lengthCounter = 0;
        this.sampleRate = sampleRate;
    }

    public abstract double getSample();

    public void onFrameCounter(){
        if (this.lengthCounter > 0) this.lengthCounter--;
    }
}
