package com.rosscon.llce.components.clocks;

/**
 * Clock that uses threads to keep running
 */
public class ClockThreaded extends Clock implements Runnable {

    /**
     * Time in milliseconds the clock will wait
     */
    private int sleepTime;

    public ClockThreaded(int sleepTime){
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while(true){
            try {
                this.tick();
            } catch (ClockException e) {
                e.printStackTrace();
            }
        }
    }
}
