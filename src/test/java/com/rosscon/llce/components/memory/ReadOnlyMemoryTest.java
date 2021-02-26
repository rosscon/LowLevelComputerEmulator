package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.busses.InvalidBusWidthException;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagException;

import com.rosscon.llce.components.flags.RWFlagValue;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlyMemoryTest {

    @Test
    @DisplayName("Create ROM with valid predefined memory should not return an error")
    public void testValidPredefinedMemoryMap () throws MemoryException, InvalidBusDataException, ClockException, FlagException {

        IntegerBus addressBus = new IntegerBus();
        IntegerBus dataBus = new IntegerBus();
        RWFlag rwFlag = new RWFlag();

        int[] data = new int[]{
                0xFF, 0x01
        };

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                0x0000, 0x0001, data);

        addressBus.writeDataToBus(0x00);
        rwFlag.setFlagValue(RWFlag.READ);
        assertEquals(0xFF, dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("Create ROM from an invalid byte array should throw an exception, data too long")
    public void testInvalidPredefinedByteArray () throws InvalidBusWidthException {

        IntegerBus addressBus = new IntegerBus();
        IntegerBus dataBus = new IntegerBus(16);
        RWFlag rwFlag = new RWFlag();

        int[] data = new int[]{
                0xFF, 0x01, 0x00, 0x00
        };

        assertThrows(MemoryException.class, () -> {
            ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                    0x0000, 0x0001, data);;
        });
    }

    @Test
    @DisplayName("Create ROM from an invalid byte array should throw an exception, data too short")
    public void testInvalidPredefinedByteArrayShort () throws InvalidBusWidthException {

        IntegerBus addressBus = new IntegerBus();
        IntegerBus dataBus = new IntegerBus(16);
        RWFlag rwFlag = new RWFlag();

        int[] data = new int[]{
                0xFF, 0x01, 0x00, 0x00
        };

        assertThrows(MemoryException.class, () -> {
            ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag,
                    0x0000, 0x0101, data);;
        });
    }


}
