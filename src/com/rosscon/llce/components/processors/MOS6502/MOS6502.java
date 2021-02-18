package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.Bus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.Flag;
import com.rosscon.llce.components.memory.MemoryException;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;
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
    private long    regPC;      // Program Counter
    private byte    regSP;      // Stack Pointer
    private byte    regACC;     // Accumulator;
    private byte    regX;       // Index Register X
    private byte    regY;       // Index Register Y
    private byte    regStatus;  // Processor Status [C][Z][I][D][B][V][N]
    private long    regIntAddr; // Custom register used for building addresses over multiple cycles

    /**
     * Vectors / Pages
     */
    private final byte STACK_PAGE       = (byte)0x01;
    private final int VECTOR_NMI       = 0xFFFB;
    private final int VECTOR_RESET     = 0xFFFC;
    private final int VECTOR_IRQ_BRK   = 0xFFFE;


    public byte[] getRegPC() {
        return new byte[] {
                (byte) ((this.regPC >>> 8) & 0xFF),
                (byte) (this.regPC & 0xFF)
        };
    }

    public byte[] getRegIntAddr() {
        return new byte[] {
                (byte) ((this.regIntAddr >>> 8) & 0xFF),
                (byte) (this.regIntAddr & 0xFF)
        };
    }

    /**
     * Getters for unit testing
     */
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
            regIntAddr  = 0x000;

            /*
             * Read the reset vector
             * TODO make this timing specific following https://www.pagetable.com/?p=410
             */
            this.addressBus.writeDataToBus(getRegPC());
            rwFlag.setFlagValue(true);
            int low = (this.dataBus.readDataFromBus()[0] & 0xFF);

            this.regPC = (this.regPC + 1) & 0xFFFF;
            this.addressBus.writeDataToBus(getRegPC());
            rwFlag.setFlagValue(true);
            int high = (this.dataBus.readDataFromBus()[0] & 0xFF);

            regIntAddr = low | (high << 8);
            this.regPC = regIntAddr;
            //this.regPC = 0xC000;
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
            addressBus.writeDataToBus(getRegPC());
            rwFlag.setFlagValue(true);
            fetchedData = dataBus.readDataFromBus()[0];
            if (PRINT_TRACE)
                System.out.print("Fetch : [" + String.format("%02X", this.regPC) + "] ");

            this.regPC = ((this.regPC + 1) & 0xFFFF);
        } catch ( Exception ex){
            throw new ProcessorException(ex.getMessage());
        }

        if (PRINT_TRACE) {
            System.out.printf("%02X", fetchedData);
            System.out.println();
        }
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
                    this.fetch();
                    break;

                case ZERO_PAGE:     // Move to next address in memory, read contents, build zero page address from it
                    this.regIntAddr = (this.fetch() & 0xFF);
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case ZERO_PAGE_X:
                    this.regIntAddr = ((this.fetch() & 0xFF) + (this.regX & 0xFF) & 0x00FF);
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case ZERO_PAGE_Y:
                    this.regIntAddr = ((this.fetch() & 0xFF) + (this.regY & 0xFF) & 0x00FF);
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case ABSOLUTE:
                    this.regIntAddr = (this.fetch() & 0xFF);
                    this.regIntAddr = this.regIntAddr | ((this.fetch() & 0xFF) << 8);
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case ABSOLUTE_X:
                    this.regIntAddr = (this.fetch() & 0xFF);
                    this.regIntAddr = this.regIntAddr | ((this.fetch() & 0xFF) << 8);
                    long tmpAbx = this.regIntAddr;
                    this.regIntAddr = this.regIntAddr + (this.regX & 0xFF);

                    if ((this.regIntAddr & 0xFF00) != (tmpAbx & 0xFF00))
                        cycles++;
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case ABSOLUTE_Y:
                    this.regIntAddr = (this.fetch() & 0xFF);
                    this.regIntAddr = this.regIntAddr | ((this.fetch() & 0xFF) << 8);
                    long tmpAby = this.regIntAddr;
                    this.regIntAddr = this.regIntAddr + (this.regY & 0xFF);

                    if ((this.regIntAddr & 0xFF00) != (tmpAby & 0xFF00))
                        cycles++;
                    addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case INDIRECT:
                    // Set internal memory pointer reading from memory
                    this.regIntAddr = (this.fetch() & 0xFF);
                    this.regIntAddr = this.regIntAddr | ((this.fetch() & 0xFF) << 8);
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);

                    // Read low byte from memory
                    long tmp = (dataBus.readDataFromBus()[0] & 0xFF);

                    // Read high byte from memory
                    this.regIntAddr += 1;
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);
                    this.regIntAddr = ((dataBus.readDataFromBus()[0] & 0xFF) << 8) | tmp;
                    break;

                case INDEXED_INDIRECT_X:
                    this.regIntAddr = ((this.fetch() & 0xFF) + (this.regX & 0xFF)) & 0xFF;

                    // Read Low byte
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);
                    long tmpInx = (dataBus.readDataFromBus()[0] & 0xFF);

                    // Read high byte
                    this.regIntAddr++;
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);
                    this.regIntAddr = ((dataBus.readDataFromBus()[0] & 0xFF) << 8) | tmpInx;

                    this.addressBus.writeDataToBus(getRegIntAddr());
                    break;

                case INDIRECT_INDEXED_Y:
                    // Step 1, get zero page address
                    this.regIntAddr = this.fetch() & 0xFF;

                    // Step 2, read low byte
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);
                    long tmpIny = (dataBus.readDataFromBus()[0] & 0xFF);

                    // Step 3, read high byte
                    this.regIntAddr++;
                    addressBus.writeDataToBus(getRegIntAddr());
                    rwFlag.setFlagValue(true);
                    this.regIntAddr = ((dataBus.readDataFromBus()[0] & 0xFF) << 8) | tmpIny;

                    // Step 4, add Y register
                    tmpIny = this.regIntAddr;
                    this.regIntAddr = this.regIntAddr + (this.regY & 0xFF);

                    // Step 5, increment cycle count if page crossed
                    if ((this.regIntAddr & 0xFF00) != (tmpIny & 0xFF00))
                        cycles++;

                    addressBus.writeDataToBus(getRegIntAddr());
                    break;
            }
        } catch (Exception ex){
            throw new ProcessorException(ex.getMessage());
        }

        /*
         * Stops the process of reading address if clock cycles increased
         */
        this.addressingMode = MOS6502AddressingMode.IMPLICIT;

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
                ADC();
                break;

            case MOS6502Instructions.INS_AND_IMM:
            case MOS6502Instructions.INS_AND_ZP:
            case MOS6502Instructions.INS_AND_ZPX:
            case MOS6502Instructions.INS_AND_ABS:
            case MOS6502Instructions.INS_AND_ABX:
            case MOS6502Instructions.INS_AND_ABY:
            case MOS6502Instructions.INS_AND_INX:
            case MOS6502Instructions.INS_AND_INY:
                AND();
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
                JMP();
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
                LDA();
                break;

            case MOS6502Instructions.INS_LDY_IMM:
            case MOS6502Instructions.INS_LDY_ZP:
            case MOS6502Instructions.INS_LDY_ZPX:
            case MOS6502Instructions.INS_LDY_ABS:
            case MOS6502Instructions.INS_LDY_ABX:
                LDY();
                break;

            case MOS6502Instructions.INS_LDX_IMM:
            case MOS6502Instructions.INS_LDX_ZP:
            case MOS6502Instructions.INS_LDX_ZPY:
            case MOS6502Instructions.INS_LDX_ABS:
            case MOS6502Instructions.INS_LDX_ABY:
                LDX();
                break;


            case MOS6502Instructions.INS_NOP_IMP:
                // No Operation
                break;


            case MOS6502Instructions.INS_PHA_IMP:
                pushToStack(this.regACC);
                break;


            case MOS6502Instructions.INS_PLA_IMP:
                PLA();
                break;


            case MOS6502Instructions.INS_RTI_IMP:
                RTI();
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
                ST(this.regACC);
                break;

            case MOS6502Instructions.INS_STX_ZP:
            case MOS6502Instructions.INS_STX_ZPY:
            case MOS6502Instructions.INS_STX_ABS:
                ST(this.regX);
                break;

            case MOS6502Instructions.INS_STY_ZP:
            case MOS6502Instructions.INS_STY_ZPX:
            case MOS6502Instructions.INS_STY_ABS:
                ST(this.regY);
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
        else if ( this.cycles == 1 ) {
            addressing();
            execute();
        } else {
            this.cycles--;
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
        if (branchSucceeded){
            // Add a cycle just for branch occurring
            this.cycles++;

            long initialAddress = this.regPC;
            byte value = dataBus.readDataFromBus()[0];

            //In this scenario we want to treat the value as a signed number;
            long newAddress = initialAddress + value;

            // Detect if the page has changed
            if ((this.regIntAddr & 0xFF00) != (newAddress & 0xFF00))
                this.cycles++;

            this.regPC = newAddress;

            // Set addressing mode to prevent any more fetches and instruction to NOP
            this.addressingMode = MOS6502AddressingMode.IMPLICIT;
            this.instruction = MOS6502Instructions.INS_NOP_IMP;
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

        /*
         * Push PC high byte to stack
         */
        byte high = (byte)((this.regPC >>> 8) & 0xFF);
        pushToStack(high);
        if (PRINT_TRACE)
            System.out.println("JSR Pushed : " + String.format("%02X", high));

        /*
         * Push PC low byte to stack
         */
        byte low = (byte)(this.regPC & 0xFF);
        pushToStack(low);
        if (PRINT_TRACE)
            System.out.println("JSR Pushed : " + String.format("%02X", low));

        /*
         * Set flags
         */
        enableFlag(MOS6502Flags.BREAK_COMMAND);
        enableFlag(MOS6502Flags.IGNORED_FLAG);

        /*
         * Push flags to stack
         */
        pushToStack(this.regStatus);
        if (PRINT_TRACE)
            System.out.println("BRK Pushed : " + String.format("%02X", this.regStatus));


        /*
         * Set PC to IRQ_BRK vector
         */
        this.regPC = VECTOR_IRQ_BRK;

        /*
         * Set PC
         */
        low = fetch();
        high = fetch();
        this.regPC = ((high & 0xFF) << 8) | low;

        /*
         * Clear flags
         */
        clearFlag(MOS6502Flags.BREAK_COMMAND);
        clearFlag(MOS6502Flags.IGNORED_FLAG);
    }

    /**
     * Reads the value on the data bus, then decrements it on bus and sets the bus to write
     * Finally any flags are set
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEC() throws ProcessorException {
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


    /**
     * Increments the Y register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void INY() {
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


    /**
     * Jumps the program counter to the value currently held on the address bus
     */
    private void JMP() {
        this.regPC = this.regIntAddr;
        if (PRINT_TRACE)
            System.out.println("JMP : " + String.format("%02X", this.regPC));
    }

    /**
     * Jumps to subroutine
     * pushes the PC pointing to the last byte of the instruction to the stack then sets the PC to the
     * address that was read from memory + 1
     */
    private void JSR() throws ProcessorException {

        if (PRINT_TRACE)
            System.out.println("JSR PC : " + String.format("%02X", this.regPC));

        /*
         * Push PC high byte to stack
         */
        long tmp = (this.regPC - 1) & 0xFFFF;
        byte high = (byte)((tmp >>> 8) & 0xFF);
        pushToStack(high);
        if (PRINT_TRACE)
            System.out.println("JSR Pushed : " + String.format("%02X", high));

        /*
         * Push PC low byte to stack
         */
        byte low = (byte)(tmp & 0xFF);
        pushToStack(low);
        if (PRINT_TRACE)
            System.out.println("JSR Pushed : " + String.format("%02X", low));

        this.regPC = this.regIntAddr;
        //this.fetch();
        if (PRINT_TRACE)
            System.out.println("JSR Set PC : " + String.format("%02X", this.regPC));
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

        this.regPC = pullFromStack() & 0xFF;
        this.regPC = this.regPC | ((pullFromStack() & 0xFF) << 8);
        fetch();

        if (PRINT_TRACE)
            System.out.println("RTI Set PC : " + String.format("%02X", this.regPC));
    }

    /**
     * Pulls the program counter minus 1 from the stack
     * For this emulator will also increment the PC after pulling from stack
     * @throws ProcessorException Can throw a ProcessorException if there is an issue reading from memory
     */
    private void RTS() throws ProcessorException {

        this.regPC = pullFromStack() & 0xFF;
        this.regPC = this.regPC | ((pullFromStack() & 0xFF) << 8);
        this.regPC++;

        if (PRINT_TRACE)
            System.out.println("RTS Set PC : " + String.format("%02X", this.regPC));

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
