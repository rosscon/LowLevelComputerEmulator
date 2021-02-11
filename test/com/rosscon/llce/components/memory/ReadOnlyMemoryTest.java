package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.ClockException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.utils.ByteArrayWrapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlyMemoryTest {

    @Test
    @DisplayName("Create ROM with valid predefined memory should not return an error")
    public void testValidPredefinedMemoryMap () throws MemoryException, InvalidBusDataException, ClockException {

        Bus addressBus = new Bus();
        Bus dataBus = new Bus();
        Flag rwFlag = new Flag();

        Map<ByteArrayWrapper, byte[]> predefinedValid = new HashMap<>() {{
            put(new ByteArrayWrapper("AA".getBytes(StandardCharsets.UTF_8)),
                    "BB".getBytes(StandardCharsets.UTF_8));
            put(new ByteArrayWrapper("CC".getBytes(StandardCharsets.UTF_8)),
                    "DD".getBytes(StandardCharsets.UTF_8));
        }};

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, predefinedValid);
        addressBus.writeDataToBus("CC".getBytes(StandardCharsets.UTF_8));
        rwFlag.setFlagValue(true);
        assertArrayEquals("DD".getBytes(StandardCharsets.UTF_8), dataBus.readDataFromBus());
    }

    @Test
    @DisplayName("Create ROM with invalid predefined memory should throw an error")
    public void testInvalidPredefinedMemoryMap () throws MemoryException {

        Bus addressBus = new Bus();
        Bus dataBus = new Bus();
        Flag rwFlag = new Flag();

        Map<ByteArrayWrapper, byte[]> predefinedInvalid = new HashMap<>() {{
            put(new ByteArrayWrapper("AAA".getBytes(StandardCharsets.UTF_8)),
                    "BB".getBytes(StandardCharsets.UTF_8));
            put(new ByteArrayWrapper("CC".getBytes(StandardCharsets.UTF_8)),
                    "DD".getBytes(StandardCharsets.UTF_8));
        }};

        assertThrows(MemoryException.class, () -> {
            ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, predefinedInvalid);
        });
    }


    @Test
    @DisplayName("Create ROM from a valid byte array should work")
    public void testValidPredefinedByteArray () throws MemoryException, InvalidBusDataException, ClockException {

        Bus addressBus = new Bus();
        Bus dataBus = new Bus();
        Flag rwFlag = new Flag();

        byte[] startAddress = {0, 0};
        byte[] endAddress = {0, 3};
        byte[] data = {0, 1, 2, 3, 4, 5, 6, 7};

        ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, startAddress, endAddress, data);

        addressBus.writeDataToBus(new byte[] {0, 2});
        rwFlag.setFlagValue(true);
        byte[] read = dataBus.readDataFromBus();
        byte[] expected = {4, 5};
        assertArrayEquals(read, expected);
    }

    @Test
    @DisplayName("Create ROM from an invalid byte array should throw an exception, data too long")
    public void testInvalidPredefinedByteArray () {

        Bus addressBus = new Bus();
        Bus dataBus = new Bus();
        Flag rwFlag = new Flag();

        byte[] startAddress = {0, 0};
        byte[] endAddress = {0, 3};
        byte[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8};

        assertThrows(MemoryException.class, () -> {
            ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, startAddress, endAddress, data);
        });
    }

    @Test
    @DisplayName("Create ROM from an invalid byte array should throw an exception, data too short")
    public void testInvalidPredefinedByteArrayShort () {

        Bus addressBus = new Bus();
        Bus dataBus = new Bus();
        Flag rwFlag = new Flag();

        byte[] startAddress = {0, 0};
        byte[] endAddress = {0, 3};
        byte[] data = {0, 1, 2};

        assertThrows(MemoryException.class, () -> {
            ReadOnlyMemory testRom = new ReadOnlyMemory(addressBus, dataBus, rwFlag, startAddress, endAddress, data);
        });
    }


}
