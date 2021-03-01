package com.rosscon.llce.components.audio.NES;


public abstract class Channel {

    protected int timer;
    protected int lengthCounter;

    protected static final int[] LENGTH_TABLE = new int[]{
            10, 254, 20, 2, 40, 4, 80, 6,
            160, 8, 60, 10, 14, 12, 26, 14,
            12, 16, 24, 18, 48, 20, 96, 22,
            192, 24, 72, 26, 16, 28, 32, 30
    };

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getLengthCounter() {
        return lengthCounter;
    }

    public void setLengthCounter(int lengthCounter) {
        this.lengthCounter = LENGTH_TABLE[lengthCounter];
    }

    protected final int sampleRate;

    public Channel(int sampleRate){
        this.timer = 0;
        this.lengthCounter = 0;
        this.sampleRate = sampleRate;
    }

    public abstract int getSample();

    public void onFrameCounter(){
    }
}
