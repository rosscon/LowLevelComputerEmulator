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

        this.start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;

        for (ByteArrayWrapper key : init.keySet()) {

            if (key.getData().length != addressBus.readDataFromBus().length)
                throw new MemoryException(this.BUS_SIZE_MISMATCH);

            long address = ByteArrayUtils.byteArrayToLong(key.getData());
            this.start = Math.min(address, start);
            end = Math.max(address, end);
        }

        int size = (int) ((end - start) + 1);
        this.contentsArr = new byte[size][dataBus.readDataFromBus().length];

        for (Map.Entry<ByteArrayWrapper, byte[]> entry : init.entrySet()) {
            long address = ByteArrayUtils.byteArrayToLong(entry.getKey().getData());
            address -= start;
            this.contentsArr[(int)address] = entry.getValue();
        }
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

        // Change to storing into a normal array
        this.start = ByteArrayUtils.byteArrayToLong(startAddress);
        long end = ByteArrayUtils.byteArrayToLong(endAddress);
        int size = (int) ((end - start) + 1);

        int dataByteWidth = dataBus.readDataFromBus().length;
        this.contentsArr = new byte[size][dataBus.readDataFromBus().length];

        if (size * dataByteWidth != data.length) {
            throw new MemoryException(this.BUS_SIZE_MISMATCH);
        }

        for (int i = 0; i < size; i++){
            byte[] tmp = new byte[dataByteWidth];
            for (int j = 0; j < dataByteWidth; j++){
                int dataIndex = (i * dataByteWidth) + j;
                tmp[j] = data[dataIndex];
                this.contentsArr[i] = tmp;
            }
        }
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

            long address = ByteArrayUtils.byteArrayToLong(this.addressBus.readDataFromBus());

            if (address >= this.start && address < this.start + contentsArr.length){
                address -= start;
                try {
                    this.dataBus.writeDataToBus(this.contentsArr[(int) address]);
                } catch (InvalidBusDataException ex) {
                    throw new MemoryException(ex.getMessage());
                }
            }
        }

    }
}
