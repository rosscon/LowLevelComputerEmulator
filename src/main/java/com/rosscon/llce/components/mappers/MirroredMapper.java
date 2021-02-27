package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.memory.Memory;

public class MirroredMapper extends Mapper {

    /**
     * Memory to be mirrored
     */
    protected Memory memory;

    /**
     * Mask to AND with requested address in order to address the memory.
     */
    private int mask;


    /**
     * A mirrored mapper, Works by using a bitmask to determine the true memory address
     * @param addressBus System address bus
     * @param dataBus System data bus
     * @param rwFlag System RW flag
     * @param memory Memory to map addresses to
     * @param mapperLow Lowest address for mapper
     * @param mapperHigh Highest address for the mapper
     * @param mask Mask used to determine the mirrored address.
     */
    public MirroredMapper(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag,
                          Memory memory, int mapperLow, int mapperHigh, int mask) {
        super(addressBus, dataBus, rwFlag);
        this.memory = memory;

        this.mask = mask;

        this.firstAddress = mapperLow;
        this.lastAddress  = mapperHigh;

    }


    @Override
    public void onFlagChange(Flag flag) throws FlagException {

        if (flag instanceof RWFlag){
            int address = this.addressBus.readDataFromBus();

            if (address >= this.firstAddress && address <= this.lastAddress){

                int maskedAddress = address & this.mask;

                try {
                    this.memory.getAddressBus().writeDataToBus(maskedAddress);
                } catch (InvalidBusDataException ex){
                    MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_ADDRESS);
                    me.addSuppressed(ex);
                    FlagException fe = new FlagException(MirroredMapperConstants.EX_INVALID_ADDRESS);
                    throw fe;
                }

                // If the new flag == false (write) then transfer the incoming data bus value
                if (flag.getFlagValue() == RWFlag.WRITE) {
                    try {
                        this.memory.getDataBus().writeDataToBus(this.dataBus.readDataFromBus());
                    } catch (InvalidBusDataException ex) {
                        MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_DATA);
                        me.addSuppressed(ex);
                        FlagException fe = new FlagException(MirroredMapperConstants.EX_INVALID_DATA);
                        throw fe;
                    }
                }

                try {
                    this.memory.getRwFlag().setFlagValue(flag.getFlagValue());
                } catch (FlagException ex){
                    MapperException me = new MapperException(ex.getMessage());
                    me.addSuppressed(ex);
                    FlagException fe = new FlagException(ex.getMessage());
                    throw fe;
                }

                // If the new flag == true (read) then transfer the read data back
                if (flag.getFlagValue() == RWFlag.READ){
                    try {
                        this.dataBus.writeDataToBus(this.memory.getDataBus().readDataFromBus());
                    }
                    catch (InvalidBusDataException ex) {
                        MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_DATA);
                        me.addSuppressed(ex);
                        FlagException fe = new FlagException(MirroredMapperConstants.EX_INVALID_DATA);
                        throw fe;
                    }
                }

            }
        }
    }
}
