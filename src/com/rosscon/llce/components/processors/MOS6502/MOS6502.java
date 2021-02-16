package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
import com.rosscon.llce.utils.ByteArrayUtils;
import com.rosscon.llce.utils.ByteUtils;


/**
 *      __  __  ____   _____     __ _____  ___ ___
 *     |  \/  |/ __ \ / ____|   / /| ____|/ _ \__ \
 *     | \  / | |  | | (___    / /_| |__ | | | | ) |
 *     | |\/| | |  | |\___ \  | '_ \___ \| | | |/ /
 *     | |  | | |__| |____) | | (_) |__) | |_| / /_
 *     |_|  |_|\____/|_____/   \___/____/ \___/____|
 *
 * Emulates the functions of an MOS 6502 processor
 */
public class MOS6502 extends Processor {

    /**
     * Debugging
     */
    private boolean PRINT_TRACE = false;           // Used to enable printing information to stdout

    private MOS6502InstructionMapping instructionMapping;

    /**
     * Error Messages
     */
    private final String EX_INVALID_INSTRUCTION =
            "Invalid or unknown instruction";

    private final String EX_TICK_FETCH_ERROR =
            "Error fetching instruction";

    private final String EX_RESET_ERROR =
            "Unable to perform reset";

    private final String EX_STACK_PUSH_ERROR =
            "Unable to push to stack";


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
     * Vectors / Pages
     */
    private final byte STACK_PAGE       = (byte)0x01;
    private final byte[] VECTOR_NMI     = new byte[]{ (byte)0xFF, (byte)0xFFB };
    private final byte[] VECTOR_RESET   = new byte[]{ (byte)0xFF, (byte)0xFFC };
    private final byte[] VECTOR_IRQ_BRK = new byte[]{ (byte)0xFF, (byte)0xFFE };

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
    private MOS6502AddressingMode addressingMode;


    /**
     * Default constructor builds the processor, calls super to connect to busses, clock and external flags
     * Also calls the reset function to initialise the processor
     * @param clock Clock
     * @param addressBus Address Bus
     * @param dataBus Data Bus
     * @param rwFlag External R/W Flag set by processor
     */
    public MOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
    }

    public MOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag, boolean printTrace) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;
    }

    /**
     * Reset/Initialise registers
     */
    private void reset() throws ProcessorException {

        try {
            regPC       = VECTOR_RESET;
            regSP       = (byte) 0xFF;
            regACC      = (byte) 0x00;
            regX        = (byte) 0x00;
            regY        = (byte) 0x00;
            regStatus   = (byte) 0x00;
            cycles      = 0;
            regIntAddr  = new byte[2];

            /*
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

        instructionMapping = new MOS6502InstructionMapping();
    }

    /**
     * Determines wheter a flag is currently set on the CPU status register
     * @param flag
     * @return
     */
    private boolean isFlagSet(byte flag){
        return (byte)(this.regStatus & flag) == flag;
    }

    /**
     * Pushes a value to the stack then decrements the stack pointer by 1
     * @param value value to push to the stack
     */
    private void pushToStack(byte value) throws ProcessorException {
        try {
            byte[] freeAddress = new byte[] { this.STACK_PAGE, this.regSP };
            this.addressBus.writeDataToBus(freeAddress);
            this.dataBus.writeDataToBus(new byte[] {value});
            this.rwFlag.setFlagValue(false);
            this.regSP = (byte)(this.regSP + 0xFF); // Adding FF allowing a wrap around is easier than subtraction
        } catch (MemoryException | InvalidBusDataException e) {
            throw new ProcessorException(this.EX_STACK_PUSH_ERROR + " - " + e.getMessage());
        }
    }

    /**
     * Pulls a byte of data from the stack
     * Increases the stack pointer by 1 then reads the value at that address
     * @return
     */
    private byte pullFromStack() throws ProcessorException {
        byte read;
        try {
            this.regSP = (byte)(this.regSP + 0x01);
            byte[] readAddress = new byte[] { this.STACK_PAGE, this.regSP };
            //readAddress = ByteArrayUtils.increment(readAddress);
            this.addressBus.writeDataToBus(readAddress);
            this.rwFlag.setFlagValue(true);
            read = this.dataBus.readDataFromBus()[0];
        } catch (MemoryException | InvalidBusDataException e) {
            throw new ProcessorException(this.EX_STACK_PUSH_ERROR + " - " + e.getMessage());
        }
        return read;
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
            System.out.printf("%02X%n", fetchedData);
        return fetchedData;
    }

    /**
     * Follows the addressing mode that has been set in order to set the address bus
     */
    private void addressing() throws ProcessorException {

        try {
            switch (this.addressingMode) {
                case IMPLICIT:      // These modes do nothing with memory
                    break;
                case RELATIVE:
                case ACCUMULATOR:
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
                            if (!this.regIntCarry) {
                                long previous = ByteArrayUtils.byteArrayToLong(this.regIntAddr);
                                long calculated = previous + ByteUtils.byteToIntUnsigned(this.regX);
                                byte[] tmp = ByteArrayUtils.longToByteArray(calculated, 2);
                                if (tmp[0] != this.regIntAddr[0]){
                                    this.regIntCarry = true;
                                    this.cycles++;
                                }
                                this.regIntAddr = tmp;
                                this.fetch();
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
                            if (!this.regIntCarry) {
                                long previous = ByteArrayUtils.byteArrayToLong(this.regIntAddr);
                                long calculated = previous + ByteUtils.byteToIntUnsigned(this.regY);
                                byte[] tmp = ByteArrayUtils.longToByteArray(calculated, 2);
                                if (tmp[0] != this.regIntAddr[0]){
                                    this.regIntCarry = true;
                                    this.cycles++;
                                }
                                this.regIntAddr = tmp;
                                this.fetch();
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
                                if (ByteUtils.willCarryOnAddition(this.regIntAddr[1], this.regY)) {
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

                /*case RELATIVE:
                    if (this.cycles == 1){
                        System.out.println();
                    }*/
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
            MOS6502InstructionDetails details = (MOS6502InstructionDetails) this.instructionMapping.get(instruction);
            this.addressingMode = details.addressingMode;
            this.cycles = details.cycles;
            this.cycles --;
        } else {
            throw new ProcessorException(EX_INVALID_INSTRUCTION + " : " + instruction);
        }
    }

    /**
     * Executes the current instruction and decrements the remaining cycles
     */
    private void execute() throws ProcessorException {

        switch (this.instruction){
            case MOS6502Instructions.INS_ADC_IMM:
            case MOS6502Instructions.INS_ADC_ZP:
            case MOS6502Instructions.INS_ADC_ZPX:
            case MOS6502Instructions.INS_ADC_ABS:
            case MOS6502Instructions.INS_ADC_ABX:
            case MOS6502Instructions.INS_ADC_ABY:
            case MOS6502Instructions.INS_ADC_INX:
            case MOS6502Instructions.INS_ADC_INY:
                if (this.cycles == 0) ADC();
                break;

            case MOS6502Instructions.INS_AND_IMM:
            case MOS6502Instructions.INS_AND_ZP:
            case MOS6502Instructions.INS_AND_ZPX:
            case MOS6502Instructions.INS_AND_ABS:
            case MOS6502Instructions.INS_AND_ABX:
            case MOS6502Instructions.INS_AND_ABY:
            case MOS6502Instructions.INS_AND_INX:
            case MOS6502Instructions.INS_AND_INY:
                if (this.cycles == 0) AND();
                break;

            case MOS6502Instructions.INS_BRK_IMP:
                BRK();
                break;

            /*
             * Branching conditions
             */
            case MOS6502Instructions.INS_BCC_REL:
                branch(!isFlagSet(MOS6502Flags.CARRY_FLAG));
                break;
            case MOS6502Instructions.INS_BCS_REL:
                branch(isFlagSet(MOS6502Flags.CARRY_FLAG));
                break;
            case MOS6502Instructions.INS_BEQ_REL:
                branch(isFlagSet(MOS6502Flags.ZERO_FLAG));
                break;
            case MOS6502Instructions.INS_BMI_REL:
                branch(isFlagSet(MOS6502Flags.NEGATIVE_FLAG));
                break;
            case MOS6502Instructions.INS_BNE_REL:
                branch(!isFlagSet(MOS6502Flags.ZERO_FLAG));
                break;
            case MOS6502Instructions.INS_BPL_REL:
                branch(!isFlagSet(MOS6502Flags.NEGATIVE_FLAG));
                break;
            case MOS6502Instructions.INS_BVC_REL:
                branch(!isFlagSet(MOS6502Flags.OVERFLOW_FLAG));
                break;
            case MOS6502Instructions.INS_BVS_REL:
                branch(isFlagSet(MOS6502Flags.OVERFLOW_FLAG));
                break;


            case MOS6502Instructions.INS_CLC_IMP:
                clearFlag(MOS6502Flags.CARRY_FLAG);
                break;
            case MOS6502Instructions.INS_CLD_IMP:
                clearFlag(MOS6502Flags.DECIMAL_MODE);
                break;
            case MOS6502Instructions.INS_CLI_IMP:
                clearFlag(MOS6502Flags.INTERRUPT_DIS);
                break;
            case MOS6502Instructions.INS_CLV_IMP:
                clearFlag(MOS6502Flags.OVERFLOW_FLAG);
                break;


            case MOS6502Instructions.INS_DEC_ZP:
            case MOS6502Instructions.INS_DEC_ZPX:
            case MOS6502Instructions.INS_DEC_ABS:
            case MOS6502Instructions.INS_DEC_ABX:
                DEC();
                break;

            case MOS6502Instructions.INS_DEX_IMP:
                DEX();
                break;

            case MOS6502Instructions.INS_DEY_IMP:
                DEY();
                break;


            case MOS6502Instructions.INS_INX_IMP:
                INX();
                break;

            case MOS6502Instructions.INS_INY_IMP:
                INY();
                break;


            case MOS6502Instructions.INS_JMP_ABS:
            case MOS6502Instructions.INS_JMP_IND:
                if (this.cycles == 0) JMP();
                break;


            case MOS6502Instructions.INS_JSR_ABS:
                JSR();
                break;

            case MOS6502Instructions.INS_LDA_IMM:
            case MOS6502Instructions.INS_LDA_ZP:
            case MOS6502Instructions.INS_LDA_ZPX:
            case MOS6502Instructions.INS_LDA_ABS:
            case MOS6502Instructions.INS_LDA_ABX:
            case MOS6502Instructions.INS_LDA_ABY:
            case MOS6502Instructions.INS_LDA_INX:
            case MOS6502Instructions.INS_LDA_INY:
                if (this.cycles == 0) LDA();
                break;

            case MOS6502Instructions.INS_LDY_IMM:
            case MOS6502Instructions.INS_LDY_ZP:
            case MOS6502Instructions.INS_LDY_ZPX:
            case MOS6502Instructions.INS_LDY_ABS:
            case MOS6502Instructions.INS_LDY_ABX:
                if (this.cycles == 0) LDY();
                break;

            case MOS6502Instructions.INS_LDX_IMM:
            case MOS6502Instructions.INS_LDX_ZP:
            case MOS6502Instructions.INS_LDX_ZPY:
            case MOS6502Instructions.INS_LDX_ABS:
            case MOS6502Instructions.INS_LDX_ABY:
                if (this.cycles == 0) LDX();
                break;


            case MOS6502Instructions.INS_NOP_IMP:
                // No Operation
                break;


            case MOS6502Instructions.INS_PHA_IMP:
                if (this.cycles == 0)
                    pushToStack(this.regACC);
                break;


            case MOS6502Instructions.INS_PLA_IMP:
                if (this.cycles == 0)
                    PLA();
                break;


            case MOS6502Instructions.INS_RTI_IMP:
                if (this.cycles == 0) RTI();
                break;

            case MOS6502Instructions.INS_RTS_IMP:
                RTS();
                break;


            case MOS6502Instructions.INS_SEC_IMP:
                enableFlag(MOS6502Flags.CARRY_FLAG);
                break;
            case MOS6502Instructions.INS_SED_IMP:
                enableFlag(MOS6502Flags.DECIMAL_MODE);
                break;
            case MOS6502Instructions.INS_SEI_IMP:
                enableFlag(MOS6502Flags.INTERRUPT_DIS);
                break;

            case MOS6502Instructions.INS_STA_ZP:
            case MOS6502Instructions.INS_STA_ZPX:
            case MOS6502Instructions.INS_STA_ABS:
            case MOS6502Instructions.INS_STA_ABX:
            case MOS6502Instructions.INS_STA_ABY:
            case MOS6502Instructions.INS_STA_INX:
            case MOS6502Instructions.INS_STA_INY:
                if (this.cycles == 0) ST(this.regACC);
                break;

            case MOS6502Instructions.INS_STX_ZP:
            case MOS6502Instructions.INS_STX_ZPY:
            case MOS6502Instructions.INS_STX_ABS:
                if (this.cycles == 0) ST(this.regX);
                break;

            case MOS6502Instructions.INS_STY_ZP:
            case MOS6502Instructions.INS_STY_ZPX:
            case MOS6502Instructions.INS_STY_ABS:
                if (this.cycles == 0) ST(this.regY);
                break;

            case MOS6502Instructions.INS_TAX:
                this.regX = this.regACC;
                break;

            case MOS6502Instructions.INS_TAY:
                this.regY = this.regACC;
                break;

            case MOS6502Instructions.INS_TSX:
                this.regX = this.regSP;
                break;

            case MOS6502Instructions.INS_TXA:
                this.regACC = this.regX;
                break;

            case MOS6502Instructions.INS_TXS:
                this.regSP = this.regX;
                break;

            case MOS6502Instructions.INS_TYA:
                this.regACC = this.regY;
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
            execute();
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
    private void clearFlag(byte flag) {
        if ((this.regStatus & flag) == flag){
            this.regStatus = (byte)(this.regStatus - flag);
        }
    }

    /**
     * Performs the necessary steps to complete a branching instruction
     * Not being 100% cycle accurate on this one. Will perform all the functions of
     * incrementing the program counter from value fetched in memory.
     * Then increments the number of cycles +1 default, +2 if the page has changed
     * Finally the current instruction is set to NOP and addressing mode to IMPLIED
     * to prevent the program counter from also being affected.
     */
    private void branch(boolean branchSucceeded){
        if (this.cycles == 0) {
            if (branchSucceeded){
                // Add a cycle just for branch occurring
                this.cycles++;

                long initialAddress = ByteArrayUtils.byteArrayToLong(this.regPC);
                byte value = dataBus.readDataFromBus()[0];

                //In this scenario we want to treat the value as a signed number;
                long newAddress = initialAddress + value;
                byte[] newPC = ByteArrayUtils.longToByteArray(newAddress, 2);

                // Detect if the page has changed
                if (newPC[0] != this.regPC[0])
                    this.cycles++;

                this.regPC = newPC;

                // Set addressing mode to prevent any more fetches and instruction to NOP
                this.addressingMode = MOS6502AddressingMode.IMPLICIT;
                this.instruction = MOS6502Instructions.INS_NOP_IMP;
            }
        }
    }

    /**
     * Add with Carry
     * http://www.obelisk.me.uk/6502/reference.html#ADC
     * Adds the contents of a memory location to the accumulator and the carry flag if set
     * Sets the carry flag bit if an overflow occurred.
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

        if ((this.regStatus & MOS6502Flags.DECIMAL_MODE) == MOS6502Flags.DECIMAL_MODE){
            // BCD addition
            // TODO BCD addition
        } else {
            // Binary addition
            result = (byte)(value + this.regACC);

            if ((this.regStatus & MOS6502Flags.CARRY_FLAG) == MOS6502Flags.CARRY_FLAG)
                result = (byte)(result + 0x01);
        }

        /*
         * Set Flags
         */
        // Carry Flag
        if (ByteUtils.willCarryOnAddition(value, this.regACC)) {
            enableFlag(MOS6502Flags.CARRY_FLAG);
        } else {
            clearFlag(MOS6502Flags.CARRY_FLAG);
        }

        // Zero Flag
        if (result == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        /*
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
            enableFlag(MOS6502Flags.OVERFLOW_FLAG);
        }
        else if (((this.regACC & 0x80) == 0x80) && ((value & 0x80) == 0x80) && ((result & 0x80) != 0x80)){
            enableFlag(MOS6502Flags.OVERFLOW_FLAG);
        }

        // Negative Flag
        if ((result & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        // Finally set accumulator with new value
        this.regACC = result;
        if (PRINT_TRACE)
            System.out.println("ADC : " + String.format("%02X", this.regACC));
    }

    /**
     * Performs a logical between the accumulator and a memory location
     * Sets zero flag if the result == 0
     * Sets the negative flag if bit 7 is set
     */
    private void AND() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = dataBus.readDataFromBus()[0];
        this.regACC = (byte)(this.regACC & value);

        // Zero Flag
        if (this.regACC == 0x00) {
            enableFlag(MOS6502Flags.ZERO_FLAG);
        } else {
            clearFlag(MOS6502Flags.ZERO_FLAG);
        }

        // Negative Flag
        if ((this.regACC & 0b10000000) == 0b10000000){
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
        } else {
            clearFlag(MOS6502Flags.NEGATIVE_FLAG);
        }
        if (PRINT_TRACE)
            System.out.println("AND : " + String.format("%02X", this.regACC));
    }

    /**
     * Pushes PC and status to the stack
     * set the PC to the interrupt vector at 0xFFFE/F
     * Set the break flag, note this is not left in the status, see https://wiki.nesdev.com/w/index.php/Status_flags
     * "The B Flag" bits 4 and 5 are only set in the stack and not in the register.
     */
    private void BRK() throws ProcessorException {
        switch (this.cycles){
            case 5:
                // Push PC high to stack
                pushToStack(this.regPC[0]);
                if (PRINT_TRACE)
                    System.out.println("BRK Pushed : " + String.format("%02X", this.regPC[0]));
                // Push PC low to stack
                pushToStack(this.regPC[1]);
                if (PRINT_TRACE)
                    System.out.println("BRK Pushed : " + String.format("%02X", this.regPC[1]));
                break;
            case 4:
                // Set flags
                enableFlag(MOS6502Flags.BREAK_COMMAND);
                enableFlag(MOS6502Flags.IGNORED_FLAG);
                // Push status to stack
                pushToStack(this.regStatus);
                if (PRINT_TRACE)
                    System.out.println("BRK Pushed : " + String.format("%02X", this.regStatus));
                break;
            case 3:
                this.regPC = VECTOR_IRQ_BRK;
                break;
            case 2:
                // Set PC low
                this.regIntAddr[1] = fetch();
                break;
            case 1:
                // Set PC high
                this.regIntAddr[0] = fetch();
                break;
            case 0:
                // Set PC
                this.regPC = this.regIntAddr;
                // Clear Flags
                clearFlag(MOS6502Flags.BREAK_COMMAND);
                clearFlag(MOS6502Flags.IGNORED_FLAG);
                break;
        }
    }

    /**
     * Reads the value on the data bus, then decrements it on bus and sets the bus to write
     * Finally any flags are set
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEC() throws ProcessorException {
        if (this.cycles == 0){
            try{
                this.rwFlag.setFlagValue(true);
                byte value = this.dataBus.readDataFromBus()[0];

                value = (byte)(value + 0xFF);
                this.dataBus.writeDataToBus(new byte[]{value});
                this.rwFlag.setFlagValue(false);

                // Zero Flag
                if (this.dataBus.readDataFromBus()[0] == 0x00)
                    enableFlag(MOS6502Flags.ZERO_FLAG);

                // Negative Flag
                if ((this.dataBus.readDataFromBus()[0] & 0b10000000) == 0b10000000)
                    enableFlag(MOS6502Flags.NEGATIVE_FLAG);

                if (PRINT_TRACE)
                    System.out.println("DEC : " + String.format("%02X", this.dataBus.readDataFromBus()[0]));
            } catch (MemoryException | InvalidBusDataException ex){
                throw new ProcessorException(ex.getMessage());
            }
        }
    }

    /**
     * Decrements the value of the X register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEX() {
        this.regX = (byte)(this.regX + 0xFF);

        // Zero Flag
        if (this.regX == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((this.regX & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        if (PRINT_TRACE)
            System.out.println("DEX : " + String.format("%02X", this.regX));
    }

    /**
     * Decrements the value of the Y register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEY() {
        this.regY = (byte)(this.regY + 0xFF);

        // Zero Flag
        if (this.regY == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((this.regY & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        if (PRINT_TRACE)
            System.out.println("DEY : " + String.format("%02X", this.regY));
    }

    /**
     * Increments the X register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void INX() {
        if (this.cycles == 0) {
            this.regX = (byte) (this.regX + 0x01);

            // Zero Flag
            if (this.regX == 0x00)
                enableFlag(MOS6502Flags.ZERO_FLAG);

            // Negative Flag
            if ((this.regX & 0b10000000) == 0b10000000)
                enableFlag(MOS6502Flags.NEGATIVE_FLAG);

            if (PRINT_TRACE)
                System.out.println("INX : " + String.format("%02X", this.regX));
        }
    }


    /**
     * Increments the Y register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void INY() {
        if (this.cycles == 0) {
            this.regY = (byte) (this.regY + 0x01);

            // Zero Flag
            if (this.regY == 0x00)
                enableFlag(MOS6502Flags.ZERO_FLAG);

            // Negative Flag
            if ((this.regY & 0b10000000) == 0b10000000)
                enableFlag(MOS6502Flags.NEGATIVE_FLAG);

            if (PRINT_TRACE)
                System.out.println("INY : " + String.format("%02X", this.regY));
        }
    }


    /**
     * Jumps the program counter to the value currently held on the address bus
     */
    private void JMP() {
        this.regPC = this.regIntAddr;
        if (PRINT_TRACE)
            System.out.println("JMP : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
    }

    /**
     * Jumps to subroutine
     * pushes the PC pointing to the last byte of the instruction to the stack then sets the PC to the
     * address that was read from memory + 1
     */
    private void JSR() throws ProcessorException {

        switch (this.cycles){
            case 4:
            case 3:
            case 2:
                if (PRINT_TRACE)
                    System.out.println("JSR PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
                break;
            case 1:
                if (PRINT_TRACE)
                    System.out.println("JSR PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));

                pushToStack(this.regPC[0]);
                if (PRINT_TRACE)
                    System.out.println("JSR Pushed : " + String.format("%02X", this.regPC[1]));

                pushToStack(this.regPC[1]);
                if (PRINT_TRACE)
                    System.out.println("JSR Pushed : " + String.format("%02X", this.regPC[0]));

                break;
            case 0:
                if (PRINT_TRACE)
                    System.out.println("JSR PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));

                this.regPC = this.regIntAddr;
                this.fetch();
                if (PRINT_TRACE)
                    System.out.println("JSR Set PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
                break;
        }
    }

    /**
     * Loads the value from memory into the accumulator
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of accumulator is a 1
     * @throws ProcessorException Can throw a ProcessorException when issues reading memory
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
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        this.regACC = value;

        if (PRINT_TRACE)
            System.out.println("LDA : " + String.format("%02X", this.regACC));
    }

    /**
     * Loads the value from memory into the x register
     * Sets ZERO_FLAG if X register becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of X register is a 1
     * @throws ProcessorException Can throw a ProcessorException when issues reading memory
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
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        this.regX = value;

        if (PRINT_TRACE)
            System.out.println("LDX : " + String.format("%02X", this.regX));
    }

    /**
     * Loads the value from memory into the y register
     * Sets ZERO_FLAG if Y register becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of Y register is a 1
     * @throws ProcessorException Can throw a ProcessorException when issues reading memory
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
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        this.regY = value;

        if (PRINT_TRACE)
            System.out.println("LDY : " + String.format("%02X", this.regY));
    }

    /**
     * Pulls from the stack
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of accumulator is a 1
     * @throws ProcessorException
     */
    private void PLA() throws ProcessorException {

        this.regACC = pullFromStack();

        // Zero Flag
        if (this.regACC == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((this.regACC & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        if (PRINT_TRACE)
            System.out.println("PLA : " + String.format("%02X", this.regY));
    }

    /**
     * Pulls the processor flags followed by the program counter from the stack
     * When pulling the processor flags bits 5 and 4 are ignored
     * https://wiki.nesdev.com/w/index.php/Status_flags
     */
    private void RTI() throws ProcessorException {
        byte flags = pullFromStack();
        if (PRINT_TRACE)
            System.out.println("RTI Flags Pulled: " + String.format("%02X", flags));

        flags = (byte)(flags & 0b11001111);

        if (PRINT_TRACE)
            System.out.println("RTI Flags Masked: " + String.format("%02X", flags));

        this.regStatus = flags;

        this.regPC[1] = pullFromStack();
        this.regPC[0] = pullFromStack();

        if (PRINT_TRACE)
            System.out.println("RTI Set PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
    }

    /**
     * Pulls the program counter minus 1 from the stack
     * For this emulator will also increment the PC after pulling from stack
     * @throws ProcessorException Can throw a ProcessorException if there is an issue reading from memory
     */
    private void RTS() throws ProcessorException {

        switch (this.cycles){
            case 3:
                // Fetch low byte
                this.regPC[1] = pullFromStack();
                break;
            case 2:
                // Fetch the high byte
                this.regPC[0] = pullFromStack();
                break;
            case 1:
                //Increment PC
                fetch();
                break;
            case 0:
                if (PRINT_TRACE)
                    System.out.println("RTS Set PC : " + String.format("%02X", this.regPC[0]) + String.format("%02X", this.regPC[1]));
        }

    }

    /**
     * Stores the value to the memory address set. Used for STA, STX, STY
     * @param value value to store in memory
     * @throws ProcessorException Can throw a ProcessorException if there is an issue writing to memory
     */
    private void ST(byte value) throws ProcessorException {
        try {
            this.dataBus.writeDataToBus(new byte[]{ value });
            rwFlag.setFlagValue(false);
        } catch (InvalidBusDataException | MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }
    }

}
