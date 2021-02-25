package com.rosscon.llce.components.mappers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.FlagValueRW;
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
    public MirroredMapper(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag,
                          Memory memory, int mapperLow, int mapperHigh, int mask) {
        super(addressBus, dataBus, rwFlag);
        this.memory = memory;

        this.mask = mask;

        this.firstAddress = mapperLow;
        this.lastAddress  = mapperHigh;

    }


    @Override
    public void onFlagChange(FlagValueRW newValue, Flag flag) throws MapperException {

        if (flag == rwFlag){
            int address = this.addressBus.readDataFromBus();

            if (address >= this.firstAddress && address <= this.lastAddress){

                int maskedAddress = address & this.mask;

                try {
                    this.memory.getAddressBus().writeDataToBus(maskedAddress);
                } catch (InvalidBusDataException ex){
                    MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_ADDRESS);
                    me.addSuppressed(ex);
                    throw me;
                }

                // If the new flag == false (write) then transfer the incoming data bus value
                if (newValue == FlagValueRW.WRITE) {
                    try {
                        this.memory.getDataBus().writeDataToBus(this.dataBus.readDataFromBus());
                    } catch (InvalidBusDataException ex) {
                        MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_DATA);
                        me.addSuppressed(ex);
                        throw me;
                    }
                }

                try {
                    this.memory.getRwFlag().setFlagValue(newValue);
                } catch (FlagException ex){
                    MapperException me = new MapperException(ex.getMessage());
                    me.addSuppressed(ex);
                    throw me;
                }

                // If the new flag == true (read) then transfer the read data back
                if (newValue == FlagValueRW.READ){
                    try {
                        this.dataBus.writeDataToBus(this.memory.getDataBus().readDataFromBus());
                    }
                    catch (InvalidBusDataException ex) {
                        MapperException me = new MapperException(MirroredMapperConstants.EX_INVALID_DATA);
                        me.addSuppressed(ex);
                        throw me;
                    }
                }

            }
        }
    }
}
