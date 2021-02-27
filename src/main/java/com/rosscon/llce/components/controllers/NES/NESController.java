package com.rosscon.llce.components.controllers.NES;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.controllers.ControllerException;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.flags.FlagException;
import com.rosscon.llce.components.flags.RWFlag;
import com.rosscon.llce.components.flags.FlagListener;

public abstract class NESController implements FlagListener {

    /**
     * Address Constants
     */
    protected static final int PLAYER_1_ADDRESS = 0x4016;
    protected static final int PLAYER_2_ADDRESS = 0x4017;

    /**
     * Button Indexes
     */
    protected static final int BUTTON_INDEX_A       = 0;
    protected static final int BUTTON_INDEX_B       = 1;
    protected static final int BUTTON_INDEX_SELECT  = 2;
    protected static final int BUTTON_INDEX_START   = 3;
    protected static final int BUTTON_INDEX_UP      = 4;
    protected static final int BUTTON_INDEX_DOWN    = 5;
    protected static final int BUTTON_INDEX_LEFT    = 6;
    protected static final int BUTTON_INDEX_RIGHT   = 7;

    /**
     * Error Messages
     */
    private static final String EX_BUS_WRITE = "Controller encountered an error writing state to bus";

    /**
     * Player ID
     */
    protected int player;

    /**
     * Snapshot state of the switches
     */
    protected int switchState;

    /**
     * Count number of reads occurred for shifting
     */
    protected int count;

    /**
     * Address Bus
     */
    protected IntegerBus addressBus;

    /**
     * Data Bus
     */
    protected IntegerBus dataBus;

    /**
     * Address Bus
     */
    protected final int address;


    public NESController(IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, int player){
        rwFlag.addListener(this);
        this.addressBus = addressBus;
        this.dataBus = dataBus;
        this.player = player;

        if (player == 1)
            this.address = PLAYER_1_ADDRESS;
        else if (player == 2)
            this.address = PLAYER_2_ADDRESS;
        else
            this.address = 0;
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {

        try {
            if (addressBus.readDataFromBus() == this.address && flag.getFlagValue() == RWFlag.WRITE) {
                snapshotInput();
                count = 0;
            } else if (addressBus.readDataFromBus() == this.address && flag.getFlagValue() == RWFlag.READ) {
                dataBus.writeDataToBus((switchState >>> count) & 0x01);
                count++;
                if (count > 7) count = 0;
            }
        } catch (InvalidBusDataException be) {
            be.printStackTrace();
            ControllerException ce = new ControllerException(EX_BUS_WRITE);
            ce.addSuppressed(be);
            FlagException fe = new FlagException(EX_BUS_WRITE);
            fe.addSuppressed(be);
            throw fe;
        }
    }

    /**
     * Instructs the controller to store the current state of the controller input
     */
    protected abstract void snapshotInput();
}
