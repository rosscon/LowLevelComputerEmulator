package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.utils.ByteArrayUtils;
import com.rosscon.llce.utils.ByteArrayWrapper;

import java.util.*;

/**
 * Emulate behaviour of read only memory
 */
public class ReadOnlyMemory extends Memory {

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
     * @param rwFlag flag to listen to R/W status and indicate when to write data to data bus
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(addressBus, dataBus, rwFlag);
    }


    /**
     * Instantiate read only memory with some initial data that is pre-mapped to memory locations
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag flag to listen to R/W status and indicate when to write data to data bus
     * @param init initial memory mapping
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Flag rwFlag, Map<ByteArrayWrapper, byte[]> init) throws MemoryException {
        super(addressBus, dataBus, rwFlag);

        int addressWidth = addressBus.readDataFromBus().length;
        int dataWidth = dataBus.readDataFromBus().length;

        Iterator initIt = init.entrySet().iterator();

        // Check that the input data matches the busses widths
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
     * @param rwFlag flag to listen to R/W status and indicate when to write data to data bus
     * @param startAddress address of beginning of memory
     * @param endAddress address of last memory location
     * @param data data to write to memory.
     */
    public ReadOnlyMemory(Bus addressBus, Bus dataBus, Flag rwFlag,
                          byte[] startAddress, byte[] endAddress, byte[] data) throws MemoryException {
        super(addressBus, dataBus, rwFlag);

        Queue<Byte> dataQueue = new ArrayDeque<>(){{
            for (byte datum : data) add(datum);
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
     * On notify of flag change write to data bus if flag is set to high and has valid address on address bus
     * @param newValue flag value
     * @param flag which flag fired the event
     * @throws MemoryException might throw memory exception if error with busses
     */
    @Override
    public void onFlagChange(boolean newValue, Flag flag) throws MemoryException {

        // On R/W flag being set to true write contents at address on address bus to data bus if within range
        if (flag == rwFlag && newValue){

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
}
