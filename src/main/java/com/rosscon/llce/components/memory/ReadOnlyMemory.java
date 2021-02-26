package com.rosscon.llce.components.memory;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;

public class ReadOnlyMemory extends Memory {


    /**
     * Construct Read Only Memory that is empty and fills the full address range
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag R/W flag to attach to
     * @throws MemoryException Thrown when any bus of flag is null
     */
    public ReadOnlyMemory(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag) throws MemoryException {
        super(addressBus, dataBus, rwFlag);
    }

    /**
     * Construct Read Only Memory that is empty for a given address range
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag R/W flag to attach to
     * @param startAddress address of the first value in memory
     * @param lastAddress address of the last value in memory
     * @throws MemoryException Thrown when any bus of flag is null or an invalid address range provided
     */
    public ReadOnlyMemory(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag,
                          int startAddress, int lastAddress) throws MemoryException {
        super(addressBus, dataBus, rwFlag, startAddress, lastAddress);
    }

    /**
     * Construct Read Only Memory with predefined contents
     * @param addressBus address bus to attach to
     * @param dataBus data bus to attach to
     * @param rwFlag R/W flag to attach to
     * @param startAddress address of the first value in memory
     * @param lastAddress address of the last value in memory
     * @param contents predefined contents of the Read Only Memory
     * @throws MemoryException Thrown when any bus of flag is null, or an invalid address range provided, or provided data size mismatches address range
     */
    public ReadOnlyMemory(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag,
                          int startAddress, int lastAddress, int[] contents) throws MemoryException {
        super(addressBus, dataBus, rwFlag, startAddress, lastAddress);

        if (contents.length > (lastAddress - startAddress + 1))
            throw new MemoryException(ReadOnlyMemoryConstants.EX_PROVIDED_DATA_TOO_LARGE);

        if (contents.length < (lastAddress - startAddress))
            throw new MemoryException(ReadOnlyMemoryConstants.EX_PROVIDED_DATA_TOO_SMALL);

        this.contents = contents;
    }

    /**
     * Read only memory will only respond to read flags, write flags are ignored
     * @param flag flag that fired event
     * @throws FlagException thrown when error reading memory
     */
    @Override
    public void onFlagChange(Flag flag) throws FlagException {

        if (flag instanceof RWFlag && flag.getFlagValue() == RWFlag.READ){
            int address = this.addressBus.readDataFromBus();
            try {
                if (addressIsInRange(address))
                    this.dataBus.writeDataToBus(readValueFromAddress(address));
            } catch (InvalidBusDataException be) {
                MemoryException me = new MemoryException(MemoryConstants.EX_ERROR_WRITING_TO_BUS);
                me.addSuppressed(be);
                FlagException fe = new FlagException(MemoryConstants.EX_ERROR_WRITING_TO_BUS);
                fe.addSuppressed(me);
                throw fe;
            }
        }
    }
}
