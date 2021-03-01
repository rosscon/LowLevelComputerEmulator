package com.rosscon.llce.components.audio.NES;

import com.rosscon.llce.components.audio.AudioProcessor;
import com.rosscon.llce.components.audio.filters.HighPassFilter;
import com.rosscon.llce.components.audio.filters.LowPassFilter;
import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;

import javax.sound.sampled.LineUnavailableException;

public class NES2A03 extends AudioProcessor {

    private static final int CLOCKS_PER_SAMPLE = 487;

    Clock clock;

    /**
     * Pulse 1
     */
    Pulse pulse1;
    int pulse1Enabled;

    /**
     * Pulse 2
     */
    Pulse pulse2;
    int pulse2Enabled;

    /**
     * Triangle
     */
    Triangle triangle;
    int triangleEnabled;

    /**
     * Noise
     */
    Noise noise;
    int noiseEnabled;

    /**
     * DMC
     */
    DMC dmc;
    int dmcEnabled;

    /**
     * APU memory buffers
     */
    int memoryAddressBuffer;
    int memoryDataBuffer;

    HighPassFilter highPassFilter;
    LowPassFilter lowPassFilter;

    public NES2A03(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, Clock clock) throws LineUnavailableException {
        super(addressBus, dataBus, rwFlag);
        pulse1 = new Pulse(SAMPLE_RATE);
        pulse2 = new Pulse(SAMPLE_RATE);
        triangle = new Triangle(SAMPLE_RATE);
        noise = new Noise(SAMPLE_RATE);
        dmc = new DMC(SAMPLE_RATE);

        pulse1Enabled = 0;
        pulse2Enabled = 0;
        triangleEnabled = 0;
        noiseEnabled = 0;
        dmcEnabled = 0;

        memoryAddressBuffer = -1;
        memoryDataBuffer = -1;

        this.clock = clock;
        this.highPassFilter = new HighPassFilter(90, SAMPLE_RATE, 1);
        this.lowPassFilter = new LowPassFilter(14000, SAMPLE_RATE, 1);
    }


    @Override
    public short getSample() {

        try {
            this.clock.tick(CLOCKS_PER_SAMPLE);
        } catch (ClockException e) {
            e.printStackTrace();
        }

        double samplePulse1 = pulse1.getSample() * pulse1Enabled;
        double samplePulse2 = pulse2.getSample() * pulse2Enabled;
        double sampleTriangle = triangle.getSample() * triangleEnabled;
        double sampleNoise = noise.getSample() * noiseEnabled;
        double sampleDmc = dmc.getSample() * dmcEnabled;

        double tndOut = 159.0d / ((1d/ (sampleTriangle / 8227d) + (sampleNoise / 12241d) + (sampleDmc / 22638d) ) + 100d);
        double pulseOut = 95.88d / ((8128d / (samplePulse1 + samplePulse2)) + 100.0d);

        float sample = (float) (tndOut + pulseOut - 0.5);
        sample = lowPassFilter.getNext(sample);
        sample = highPassFilter.getNext(sample);

        return (short)(sample * Short.MAX_VALUE);
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {
        if (flag instanceof RWFlag) {

            try {
                int address = addressBus.readDataFromBus();

                if (address >= 0x4000 && address <= 0x4017) {
                    int data = dataBus.readDataFromBus();

                    if (address == 0x4000) {
                        //Duty, envelope loop / counter halt, constant volume, volume/envelope
                        pulse1.setDuty((data & 0b11000000) >> 6);
                        pulse1.setEnvLoopLengthCounterHalt((data & 0b00100000) != 0);
                        pulse1.setConstantVolume((data & 0b00010000) != 0);
                        pulse1.setVolumeEnvelope(data & 0b00001111);
                    } else if (address == 0x4001) {
                        // Sweep enable, period, negate, shift
                        pulse1.setSweepEnable((data & 0x80) != 0);
                        pulse1.setPeriod((data & 0b01110000) >> 4);
                        pulse1.setNegate((data & 0b00001000) != 0);
                        pulse1.setShift(data & 0b00000111);
                    } else if (address == 0x4002) {
                        // Timer Low Byte
                        pulse1.setTimer((pulse1.getTimer() & 0xFFFFFF00) | (data & 0x00FF));
                    } else if (address == 0x4003) {
                        // Length counter, Timer high
                        pulse1.setTimer((pulse1.getTimer() & 0x000000FF) | ((data & 0x0007) << 8));
                        pulse1.setLengthCounter((data & 0b11111000) >> 4);
                    } else if (address == 0x4004) {
                        //Duty, envelope loop / counter halt, constant volume, volume/envelope
                        pulse2.setDuty((data & 0b11000000) >> 6);
                        pulse2.setEnvLoopLengthCounterHalt((data & 0b00100000) != 0);
                        pulse2.setConstantVolume((data & 0b00010000) != 0);
                        pulse2.setVolumeEnvelope(data & 0b00001111);
                    } else if (address == 0x4005) {
                        // Sweep enable, period, negate, shift
                        pulse2.setSweepEnable((data & 0x80) != 0);
                        pulse2.setPeriod((data & 0b01110000) >> 4);
                        pulse2.setNegate((data & 0b00001000) != 0);
                        pulse2.setShift(data & 0b00000111);
                    } else if (address == 0x4006) {
                        // Timer Low Byte
                        pulse2.setTimer((pulse2.getTimer() & 0xFFFFFF00) | (data & 0x00FF));
                    } else if (address == 0x4007) {
                        // Length counter, Timer high
                        pulse2.setTimer((pulse2.getTimer() & 0x000000FF) | ((data & 0x0007) << 8));
                        pulse2.setLengthCounter(data & 0b11111000);
                    } else if (address == 0x4015) {
                        pulse1Enabled = data & 0b00000001;
                        pulse2Enabled = (data & 0b00000010) >>> 1;
                        triangleEnabled = (data & 0b00000100) >>> 2;
                        noiseEnabled = (data & 0b00001000) >>> 3;
                        dmcEnabled = (data & 0b00010000) >>> 4;
                    } else if (address == 0x4017) {
                        pulse2.onFrameCounter();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
