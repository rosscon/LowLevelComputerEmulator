package com.rosscon.llce.components.busses;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of com.rosscon.llce.components.busses.Bus
 */
public class BusTest {

    @Test
    @DisplayName("Default bus instantiation should work")
    public void testDefaultConstructor () {
        Bus tmpBus = new Bus();
        assertArrayEquals( new byte[2] ,tmpBus.readDataFromBus(),
                "Default constructor should instantiate a bus with all zeros");
    }

    @Test
    @DisplayName("Instantiating with a valid bus width should work")
    public void testValidBusWidth () throws InvalidBusWidthException {
        Bus tmpBus = new Bus(32);
        assertArrayEquals(new byte[4], tmpBus.readDataFromBus(),
                "Bus should instantiate with the defined width");
    }

    @Test
    @DisplayName("Instantiating with an invalid bus width should throw an exception")
    public void testInvalidBusWidthException () {
        assertThrows(InvalidBusWidthException.class, () -> {
            Bus tmpBus = new Bus(9);
        });
    }

    @Test
    @DisplayName("Instantiating with a valid bus width that matches the initial data should work")
    public void testValidBusWidthAnInitialData () throws InvalidBusWidthException {
        byte [] tmpInit = "TEST".getBytes(StandardCharsets.UTF_8);
        Bus tmpBus = new Bus(tmpInit.length * 8, tmpInit);
        assertArrayEquals("TEST".getBytes(), tmpBus.readDataFromBus(),
                "Data read from bus should match what the bus was initialised with");
    }

    @Test
    @DisplayName("Instantiating with a mismatched bus width and initial data should throw an exception")
    public void testMismatchedBusWidthAndInitialData (){
        assertThrows(InvalidBusWidthException.class, () -> {
            byte [] tmpInit = "TEST".getBytes(StandardCharsets.UTF_8);
            Bus tmpBus = new Bus(tmpInit.length * 6, tmpInit);
        });
    }

    @Test
    @DisplayName("Writing valid data to bus should work")
    public void testWritingValidData () throws InvalidBusDataException {
        Bus tmpBus = new Bus();
        tmpBus.writeDataToBus("AB".getBytes(StandardCharsets.UTF_8));
        assertArrayEquals("AB".getBytes(StandardCharsets.UTF_8), tmpBus.readDataFromBus(),
                "Data read from bus should match what ws written");
    }

    @Test
    @DisplayName("Writing invalid data size to bus should throw an error")
    public void testWritingInvalidDataSize () {
        Bus tmpBus = new Bus();
        assertThrows(InvalidBusDataException.class, () -> {
            tmpBus.writeDataToBus("ABC".getBytes(StandardCharsets.UTF_8));
        })
    }

}
