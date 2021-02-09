package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.utils.ByteArrayUtils;
import com.rosscon.llce.utils.ByteArrayWrapper;

import java.util.*;

/**
 * Emulate behaviour of read only memory
 */
public class ReadOnlyMemory extends Memory {

    /**
     * Contents of the memory <address, data>
     */
    private Map<ByteArrayWrapper, byte[]> contents = new HashMap<>();

    /**
     * Error message when provided data does not match address size
     */
    private final String DATA_SIZE_ADDRESS_MISMATCH =
        "Size of supplied data does not match the address range provided";


    /**
     * Error message when bus size does not match the provided data
     */
    private final String BUS_SIZE_MISMATCH =
            "Size of supplied data does not match the address range provided";


    /**
     * Instantiate read only memory with empty contents
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param clock clock to listen for ticks
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Clock clock) {
        super(addressBus, dataBus, clock);
    }


    /**
     * Instantiate read only memory with some initial data that is pre-mapped to memory locations
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param clock clock to listen for ticks
     * @param init
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Clock clock, Map<ByteArrayWrapper, byte[]> init) throws MemoryException {
        super(addressBus, dataBus, clock);

        int addressWidth = addressBus.readDataFromBus().length;
        int dataWidth = addressBus.readDataFromBus().length;

        Iterator initIt = init.entrySet().iterator();

        /**
         * Check that the input data matches the busses widths
         */
        while (initIt.hasNext()){
            Map.Entry element = (Map.Entry)initIt.next();
            if (((ByteArrayWrapper)element.getKey()).getLength() != addressWidth ||
                    ((byte[])element.getValue()).length != dataWidth)
                throw new MemoryException(BUS_SIZE_MISMATCH);
        }

        this.contents = Map.copyOf(init);
    }

    /**
     * Instantiate read only memory with initial data based on start and end addresses inclusive.
     * For areas outside of the address range instantiate the contents with zeros.
     * If data is larger than the address range then throws an exception.
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param clock clock to listen for ticks
     * @param startAddress address of beginning of memory
     * @param endAddress address of last memory location
     * @param data data to write to memory.
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Clock clock,
                          byte[] startAddress, byte[] endAddress, byte[] data) throws MemoryException {
        super(addressBus, dataBus, clock);

        Queue<Byte> dataQueue = new ArrayDeque<>(){{
            for(int i = 0; i < data.length; i++) add(data[i]);
        }};

        for (byte[] index = startAddress;
             Arrays.compare(index, endAddress) < 1;
             index = ByteArrayUtils.increment(index)){

            List<Byte> tmpData = new LinkedList<>();

            int width = dataBus.readDataFromBus().length;
            for (int j = width; j > 0; j--){
                if (dataQueue.size() == 0) throw new MemoryException(DATA_SIZE_ADDRESS_MISMATCH);
                tmpData.add(dataQueue.remove());
            }

            this.contents.put(new ByteArrayWrapper(index), ByteArrayUtils.listToArray(tmpData));
        }

        if (dataQueue.size() > 0) throw new MemoryException(DATA_SIZE_ADDRESS_MISMATCH);
    }


    /**
     * If the value of the address bus is in range of the ROM address space then write to bus
     */
    @Override
    public void onTick() throws MemoryException {

        byte[] key = this.addressBus.readDataFromBus();
        ByteArrayWrapper wrappedKey = new ByteArrayWrapper(key);

        if(this.contents.containsKey(wrappedKey)) {
            try {
                this.dataBus.writeDataToBus(contents.get(wrappedKey));
            } catch (InvalidBusDataException ex) {
                throw new MemoryException(ex.getMessage());
            }
        }
    }
}
