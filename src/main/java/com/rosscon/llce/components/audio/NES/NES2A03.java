package com.rosscon.llce.components.audio.NES;

import com.rosscon.llce.components.audio.AudioProcessor;
import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;

import javax.sound.sampled.LineUnavailableException;

public class NES2A03 extends AudioProcessor {

    /**
     * Pulse 1
     */
    Pulse pulse1;

    /**
     * Pulse 2
     */
    Pulse pulse2;

    /**
     * Triangle
     */
    Triangle triangle;

    /**
     * Noise
     */
    Noise noise;

    /**
     * DMC
     */
    DMC dmc;

    public NES2A03(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag) throws LineUnavailableException {
        super(addressBus, dataBus, rwFlag);
        pulse1 = new Pulse(SAMPLE_RATE);
        pulse2 = new Pulse(SAMPLE_RATE);
        triangle = new Triangle(SAMPLE_RATE);
        noise = new Noise(SAMPLE_RATE);
    }

    @Override
    public short getSample() {

        int sample = (pulse1.getSample() + pulse2.getSample()) / 2;

        return (short)sample;

        //return 0;
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {
        if (flag instanceof RWFlag){

            int address = addressBus.readDataFromBus();
            int data = dataBus.readDataFromBus();

            if (address >= 0x4000 && address <= 0x4017) {

                if (address == 0x4000){
                    //Duty, envelope loop / counter halt, constant volume, volume/envelope
                    pulse1.setDuty((data & 0b11000000) >> 6);
                    pulse1.setEnvLoopLengthCounterHalt((data & 0b00100000) != 0);
                    pulse1.setConstantVolume((data & 0b00010000) != 0);
                    pulse1.setVolumeEnvelope(data & 0b00001111);
                } else if (address == 0x4001){
                    // Sweep enable, period, negate, shift
                    pulse1.setSweepEnable((data & 0x80) != 0);
                    pulse1.setPeriod((data & 0b01110000) >> 4);
                    pulse1.setNegate((data & 0b00001000) != 0);
                    pulse1.setShift(data & 0b00000111);
                }
                else if (address == 0x4002) {
                    // Timer Low Byte
                    pulse1.setTimer((pulse1.getTimer() & 0xFFFFFF00) | (data & 0x00FF));
                }
                else if (address == 0x4003) {
                    // Length counter, Timer high
                    pulse1.setTimer((pulse1.getTimer() & 0x000000FF) | ((data & 0x0007) << 8));
                    pulse1.setLengthCounter(data & 0b11111000);
                }

                else if (address == 0x4004){
                    //Duty, envelope loop / counter halt, constant volume, volume/envelope
                    pulse1.setDuty((data & 0b11000000) >> 6);
                    pulse1.setEnvLoopLengthCounterHalt((data & 0b00100000) != 0);
                    pulse1.setConstantVolume((data & 0b00010000) != 0);
                    pulse1.setVolumeEnvelope(data & 0b00001111);
                } else if (address == 0x4005){
                    // Sweep enable, period, negate, shift
                    pulse1.setSweepEnable((data & 0x80) != 0);
                    pulse1.setPeriod((data & 0b01110000) >> 4);
                    pulse1.setNegate((data & 0b00001000) != 0);
                    pulse1.setShift(data & 0b00000111);
                }
                else if (address == 0x4006) {
                    // Timer Low Byte
                    pulse1.setTimer((pulse1.getTimer() & 0xFFFFFF00) | (data & 0x00FF));
                }
                else if (address == 0x4007) {
                    // Length counter, Timer high
                    pulse1.setTimer((pulse1.getTimer() & 0x000000FF) | ((data & 0x0007) << 8));
                    pulse1.setLengthCounter(data & 0b11111000);
                }


                else if (address == 0x4017){
                    pulse1.onFrameCounter();
                }
            }
        }
    }
}
