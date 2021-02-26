package com.rosscon.llce.components.controllers;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagListener;
import com.rosscon.llce.components.flags.FlagValueRW;
import com.rosscon.llce.components.mappers.MapperException;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.ProcessorException;

/**
 * A dummy NES controller that sends preconfigures controller inputs
 */
public class DummyNes implements FlagListener {

    IntegerBus addressBus;
    IntegerBus dataBus;
    Flag rwFlag;

    int value = 0b00001000;

    int count = 0;


    public DummyNes(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag){
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.rwFlag = rwFlag;
        rwFlag.addListener(this);
    }

    @Override
    public void onFlagChange(FlagValueRW newValue, Flag flag) throws MemoryException, InvalidBusDataException, MapperException, ProcessorException {
        if (flag == rwFlag){
            if (addressBus.readDataFromBus() == 0x4016 && newValue == FlagValueRW.READ){

                int outputData = 0x01 & (value >>> count);
                dataBus.writeDataToBus(outputData);

                if (count == 8)
                    count = 0;

                //value >>>= 1;
            }
        }
    }
}
