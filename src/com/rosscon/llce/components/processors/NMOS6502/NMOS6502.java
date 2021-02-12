package com.rosscon.llce.components.processors.NMOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayUtils;

public class NMOS6502 extends Processor {

    /**
     * Debugging
     */
    private boolean PRINT_TRACE = false;           // Used to enable printing information to stdout

    private NMOS6502InstructionMapping instructionMapping;

    /**
     * Error Messages
     */
    private final String EX_INVALID_INSTRUCTION =
            "Invalid or unknown instruction";

    private final String EX_TICK_FETCH_ERROR =
            "Error fetching instruction";

    private final String EX_RESET_ERROR =
            "Unable to perform reset";


    /**
     * Registers
     */
    private byte[]  regPC;      // Program Counter
    private byte    regSP;      // Stack Pointer
    private byte    regACC;     // Accumulator;
    private byte    regX;       // Index Register X
    private byte    regY;       // Index Register Y
    private byte    regStatus;  // Processor Status [C][Z][I][D][B][V][N]
    private byte[]  regIntAddr; // Custom register used for building addresses over multiple cycles
    private boolean regIntCarry;// Custom carry register for handling ABS X,Y

    /**
     * Getters for unit testing
     */
    public byte[] getRegPC() {
        return regPC;
    }

    public byte getRegSP() {
        return regSP;
    }

    public byte getRegACC() {
        return regACC;
    }

    public byte getRegX() {
        return regX;
    }

    public byte getRegY() {
        return regY;
    }

    public byte getRegStatus() {
        return regStatus;
    }

    /**
     * Cycle tracking
     */
    private int cycles;

    /**
     * Current Instruction
     */
    private byte instruction;

    /**
     * Addressing Mode
     * http://www.obelisk.me.uk/6502/addressing.html#IMP
     */
    private NMOS6502AddressingMode addressingMode;


    /**
     * Default constructor builds the processor, calls super to connect to busses, clock and external flags
     * Also calls the reset function to initialise the processor
     * @param clock Clock
     * @param addressBus Address Bus
     * @param dataBus Data Bus
     * @param rwFlag External R/W Flag set by processor
     */
    public NMOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
    }

    public NMOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag, boolean printTrace) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;
    }

    /**
     * Reset/Initialise registers
     */
    private void reset() throws ProcessorException {

        try {
            regPC       = new byte[]{(byte) 0xFF, (byte) 0xFC};
            regSP       = (byte) 0xFF;
            regACC      = (byte) 0x00;
            regX        = (byte) 0x00;
            regY        = (byte) 0x00;
            regStatus   = (byte) 0x00;
            cycles      = 0;
            regIntAddr  = new byte[2];

            /**
             * Read the reset vector
             * TODO make this timing specific following https://www.pagetable.com/?p=410
             */
            this.addressBus.writeDataToBus(regPC);
            rwFlag.setFlagValue(true);
            regIntAddr[1] = this.dataBus.readDataFromBus()[0];

            this.regPC = ByteArrayUtils.increment(this.regPC);
            this.addressBus.writeDataToBus(regPC);
            rwFlag.setFlagValue(true);
            regIntAddr[0] = this.dataBus.readDataFromBus()[0];
            this.regPC = regIntAddr;
        } catch (MemoryException | InvalidBusDataException ex){
            throw new ProcessorException(EX_RESET_ERROR + " : " + ex.getMessage());
        }

        instructionMapping = new NMOS6502InstructionMapping();
    }

    /**
     * Fetches next instruction and increments the program counter
     */
    private byte fetch() throws ProcessorException {

        byte fetchedData;

        try{
            addressBus.writeDataToBus(regPC);
            rwFlag.setFlagValue(true);
            fetchedData = dataBus.readDataFromBus()[0];
            if (PRINT_TRACE)
                System.out.print("Fetch : [" + String.format("%02X", this.regPC[0]) +
                    String.format("%02X", this.regPC[1]) + "] ");

            regPC = ByteArrayUtils.increment(regPC);
        } catch ( Exception ex){
            throw new ProcessorException(ex.getMessage());
        }

        if (PRINT_TRACE)
            System.out.println(String.format("%02X", fetchedData));
        return fetchedData;
    }

    /**
     * Follows the addressing mode that has been set in order to set the address bus
     */
    private void addressing() throws ProcessorException {

        try {
            switch (this.addressingMode) {
                case IMPLICIT:      // These modes do nothing with memory
                case ACCUMULATOR:
                    break;

                case IMMEDIATE:     // Makes cpu request next address in memory
                    if (this.cycles == 1){
                        this.fetch();
                    }
                    break;

                case ZERO_PAGE:     // Move to next address in memory, read contents, build zero page address from it
                    switch(this.cycles){
                        case 2:
                            this.regIntAddr[1] = this.fetch();
                            this.regIntAddr[0] = 0x00;
                            break;
                        case 1:
                            addressBus.writeDataToBus(regIntAddr);
                            break;
                    }

                case ZERO_PAGE_X:
                    switch(this.cycles){
                        case 3:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 2:
                            this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regX);
                            this.regIntAddr[0] = 0x00;
                            break;
                        case 1:
                            addressBus.writeDataToBus(regIntAddr);
                            break;
                    }
                    break;

                case ZERO_PAGE_Y:
                    switch(this.cycles){
                        case 3:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 2:
                            this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regY);
                            this.regIntAddr[0] = 0x00;
                            break;
                        case 1:
                            addressBus.writeDataToBus(regIntAddr);
                            break;
                    }
                    break;

                case ABSOLUTE:
                    switch(this.cycles){
                        case 2:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 1:
                            this.regIntAddr[0] = this.fetch();
                            addressBus.writeDataToBus(regIntAddr);
                            break;
                    }
                    break;

                case ABSOLUTE_X:
                    switch (this.cycles){
                        case 3:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 2:
                            this.regIntAddr[0] = this.fetch();
                            break;
                        case 1:
                            if (!this.regIntCarry){
                                // Extra cycle required on carry
                                if (ByteArrayUtils.willCarryOnAddition(this.regIntAddr[1], this.regX)) {
                                    this.regIntCarry = true;
                                    this.cycles ++;
                                }
                                this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regX);
                            } else {
                                this.regIntCarry = false;
                                this.regIntAddr[0] = (byte)(this.regIntAddr[0] + 0x01);
                            }
                            addressBus.writeDataToBus(regIntAddr);
                    }
                    break;

                case ABSOLUTE_Y:
                    switch (this.cycles){
                        case 3:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 2:
                            this.regIntAddr[0] = this.fetch();
                            break;
                        case 1:
                            if (!this.regIntCarry){
                                // Extra cycle required on carry
                                if (ByteArrayUtils.willCarryOnAddition(this.regIntAddr[1], this.regY)) {
                                    this.regIntCarry = true;
                                    this.cycles ++;
                                }
                                this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regY);
                            } else {
                                this.regIntCarry = false;
                                this.regIntAddr[0] = (byte)(this.regIntAddr[0] + 0x01);
                            }
                            addressBus.writeDataToBus(regIntAddr);
                    }
                    break;

                case INDIRECT:
                    switch (this.cycles){
                        case 4:
                            this.regIntAddr[1] = this.fetch();
                            break;
                        case 3:
                            this.regIntAddr[0] = this.fetch();
                            addressBus.writeDataToBus(regIntAddr);
                            break;
                        case 2:
                            rwFlag.setFlagValue(true);
                            this.regIntAddr[1] = dataBus.readDataFromBus()[0];
                            break;
                        case 1:
                            addressBus.writeDataToBus(ByteArrayUtils.increment(addressBus.readDataFromBus()));
                            rwFlag.setFlagValue(true);
                            this.regIntAddr[0] = dataBus.readDataFromBus()[0];
                            addressBus.writeDataToBus(this.regIntAddr);
                            break;
                    }
                    break;

                case INDEXED_INDIRECT_X:
                    switch (this.cycles){
                        case 5:
                            this.regIntAddr[1] = this.fetch();
                            this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regX);
                            break;
                        case 4:
                            this.regIntAddr[0] = 0x00;
                            this.addressBus.writeDataToBus(this.regIntAddr);
                            break;
                        case 3:
                            this.rwFlag.setFlagValue(true);
                            this.regIntAddr[1] = dataBus.readDataFromBus()[0];
                            break;
                        case 2:
                            byte[] next = addressBus.readDataFromBus();
                            next = ByteArrayUtils.increment(next);
                            this.addressBus.writeDataToBus(next);
                            break;
                        case 1:
                            this.rwFlag.setFlagValue(true);
                            this.regIntAddr[0] = dataBus.readDataFromBus()[0];
                            this.addressBus.writeDataToBus(this.regIntAddr);
                            break;
                    }
                    break;

                case INDIRECT_INDEXED_Y:
                    switch (this.cycles){
                        case 4:
                            this.regIntAddr[0] = 0x00;
                            this.regIntAddr[1] = this.fetch();
                            this.addressBus.writeDataToBus(this.regIntAddr);
                            break;
                        case 3:
                            this.rwFlag.setFlagValue(true);
                            this.regIntAddr[1] = dataBus.readDataFromBus()[0];
                            break;
                        case 2:
                            byte[] next = addressBus.readDataFromBus();
                            next = ByteArrayUtils.increment(next);
                            this.addressBus.writeDataToBus(next);
                            break;
                        case 1:
                            this.rwFlag.setFlagValue(true);
                            this.regIntAddr[0] = dataBus.readDataFromBus()[0];

                            if (!this.regIntCarry){
                                if (ByteArrayUtils.willCarryOnAddition(this.regIntAddr[1], this.regY)) {
                                    this.regIntCarry = true;
                                    this.cycles ++;
                                }
                                this.regIntAddr[1] = (byte)(this.regIntAddr[1] + this.regY);
                            }
                            else {
                                this.regIntCarry = false;
                                this.regIntAddr[0] = (byte)(this.regIntAddr[0] + 0x01);
                            }
                            addressBus.writeDataToBus(regIntAddr);

                            break;
                    }
                    break;

                case RELATIVE:
                    //TODO implement relative addressing, however will need to implement a branching instruction
                    break;
            }
        } catch (Exception ex){
            throw new ProcessorException(ex.getMessage());
        }

        this.cycles--;
    }

    /**
     * Decodes the instruction and sets the number of cycles
     * @param instruction instruction to decode
     */
    private void decode(byte instruction) throws ProcessorException {
        this.instruction = instruction;

        if (this.instructionMapping.containsKey(instruction)){
            NMOS6502InstructionDetails details = (NMOS6502InstructionDetails) this.instructionMapping.get(instruction);
            this.addressingMode = details.addressingMode;
            this.cycles = details.cycles;
            this.cycles --;
            return;
        } else {
            throw new ProcessorException(EX_INVALID_INSTRUCTION + " : " + instruction);
        }
    }

    /**
     * Executes the current instruction and decrements the remaining cycles
     */
    private void execute() throws ProcessorException {

        switch (this.instruction){
            case NMOS6502Instructions.INS_ADC_IMM:
            case NMOS6502Instructions.INS_ADC_ZP:
            case NMOS6502Instructions.INS_ADC_ZPX:
            case NMOS6502Instructions.INS_ADC_ABS:
            case NMOS6502Instructions.INS_ADC_ABX:
            case NMOS6502Instructions.INS_ADC_ABY:
            case NMOS6502Instructions.INS_ADC_INX:
            case NMOS6502Instructions.INS_ADC_INY:
                ADC();
                break;



            case NMOS6502Instructions.INS_JMP_ABS:
            case NMOS6502Instructions.INS_JMP_IND:
                JMP();
                break;



            case NMOS6502Instructions.INS_LDA_IMM:
            case NMOS6502Instructions.INS_LDA_ZP:
            case NMOS6502Instructions.INS_LDA_ZPX:
            case NMOS6502Instructions.INS_LDA_ABS:
            case NMOS6502Instructions.INS_LDA_ABX:
            case NMOS6502Instructions.INS_LDA_ABY:
            case NMOS6502Instructions.INS_LDA_INX:
            case NMOS6502Instructions.INS_LDA_INY:
                LDA();
                break;

            case NMOS6502Instructions.INS_LDY_IMM:
            case NMOS6502Instructions.INS_LDY_ZP:
            case NMOS6502Instructions.INS_LDY_ZPX:
            case NMOS6502Instructions.INS_LDY_ABS:
            case NMOS6502Instructions.INS_LDY_ABX:
                LDY();
                break;

            case NMOS6502Instructions.INS_LDX_IMM:
            case NMOS6502Instructions.INS_LDX_ZP:
            case NMOS6502Instructions.INS_LDX_ZPY:
            case NMOS6502Instructions.INS_LDX_ABS:
            case NMOS6502Instructions.INS_LDX_ABY:
                LDX();
                break;

            default:
                throw new ProcessorException(EX_INVALID_INSTRUCTION + " : " + this.instruction);
        }
    }

    @Override
    public void onTick() throws ProcessorException {
        if ( this.cycles == 0 ){
            try {
                byte instruction = fetch();
                decode(instruction);
            } catch (Exception ex){
                throw new ProcessorException(EX_TICK_FETCH_ERROR);
            }
        }
        else if ( this.cycles > 0 ) {
            addressing();
            if (this.cycles == 0) execute();
        }
    }

    /**
     * Enables a given flag
     * @param flag flag to enable
     */
    private void enableFlag(byte flag) {
        this.regStatus = (byte)(this.regStatus | flag);
    }

    /**
     * Disables a given flag
     * @param flag flag to disable
     */
    private void disableFlag(byte flag) {
        if ((this.regStatus & flag) == flag){
            this.regStatus = (byte)(this.regStatus - flag);
        }
    }

    /**
     * Add with Carry
     * http://www.obelisk.me.uk/6502/reference.html#ADC
     * Adds the contents of a memory location to the accumulator and sets the carry
     * bit if an overflow occurred. All the work is completed on the final cycle once
     * data has been fetched from memory.
     * Sets CARRY_FLAG if an overflow/carry occurred from bit 7
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets OVERFLOW_FLAG if sign bit is incorrect (bit 6 carried to bit 7)
     * Sets NEGATIVE_FLAG if bit 7 is a 1
     */
    private void ADC() throws ProcessorException {

        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }
        byte value = dataBus.readDataFromBus()[0];
        byte result = 0x00;

        if ((this.regStatus & NMOS6502Flags.DECIMAL_MODE) == NMOS6502Flags.DECIMAL_MODE){
            // BCD addition
            //TODO BCD addition
        } else {
            // Binary addition
            result = (byte)(value + this.regACC);
        }

        /**
         * Set Flags
         */
        // Carry Flag
        if (ByteArrayUtils.willCarryOnAddition(value, this.regACC))
            enableFlag(NMOS6502Flags.CARRY_FLAG);

        // Zero Flag
        if (result == 0x00)
            enableFlag(NMOS6502Flags.ZERO_FLAG);

        /**
         * Overflow flag
         * based on the following logic to detect n overflow situation
         * Pos + Pos = Pos -> OK
         * Pos + Pos = Neg -> FAIL Set flag
         * Pos + Neg = OK -> Cannot overflow
         * Neg + Neg = Neg -> OK
         * Neg + Neg = Pos - FAIL Set flag
         * Only need to look at the MSB of the ACC, Value, Result
         * HEX 80 = BIN 10000000
         */
        if (((this.regACC & 0x80) != 0x80) && ((value & 0x80) != 0x80) && ((result & 0x80) == 0x80)){
            enableFlag(NMOS6502Flags.OVERFLOW_FLAG);
        }
        else if (((this.regACC & 0x80) == 0x80) && ((value & 0x80) == 0x80) && ((result & 0x80) != 0x80)){
            enableFlag(NMOS6502Flags.OVERFLOW_FLAG);
        }

        // Negative Flag
        if ((result & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.NEGATIVE_FLAG);

        // Finally set accumulator with new value
        this.regACC = result;
        if (PRINT_TRACE)
            System.out.println("ADC : " + String.format("%02X", this.regACC));
    }

    /**
     * Jumps the program counter to the value currently held on the address bus
     * @throws ProcessorException
     */
    private void JMP() throws ProcessorException {
        this.regPC = this.regIntAddr;
        if (PRINT_TRACE)
            System.out.println("JMP : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
    }

    /**
     * Loads the value from memory into the accumulator
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of accumulator is a 1
     * @throws ProcessorException
     */
    private void LDA() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];

        // Zero Flag
        if (value == 0x00)
            enableFlag(NMOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.NEGATIVE_FLAG);

        this.regACC = value;

        if (PRINT_TRACE)
            System.out.println("LDA : " + String.format("%02X", this.regACC));
    }

    /**
     * Loads the value from memory into the x register
     * Sets ZERO_FLAG if X register becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of X register is a 1
     * @throws ProcessorException
     */
    private void LDX() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];

        // Zero Flag
        if (value == 0x00)
            enableFlag(NMOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.NEGATIVE_FLAG);

        this.regX = value;

        if (PRINT_TRACE)
            System.out.println("LDX : " + String.format("%02X", this.regX));
    }

    /**
     * Loads the value from memory into the y register
     * Sets ZERO_FLAG if Y register becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of Y register is a 1
     * @throws ProcessorException
     */
    private void LDY() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];

        // Zero Flag
        if (value == 0x00)
            enableFlag(NMOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.NEGATIVE_FLAG);

        this.regY = value;

        if (PRINT_TRACE)
            System.out.println("LDY : " + String.format("%02X", this.regY));
    }

}
