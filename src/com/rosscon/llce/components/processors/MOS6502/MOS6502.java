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
     * Registers
     */
    private long    regPC;      // Program Counter
    private byte    regSP;      // Stack Pointer
    private byte    regACC;     // Accumulator;
    private byte    regX;       // Index Register X
    private byte    regY;       // Index Register Y
    private byte    regStatus;  // Processor Status [C][Z][I][D][B][V][N]
    private long    regIntAddr; // Custom register used for building addresses over multiple cycles



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
    //private byte instruction;
    private MOS6502Instruction instruction;

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

    public MOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag, boolean printTrace, long pcOverride) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;
        this.regPC = pcOverride;
    }

    /**
     * Reset/Initialise registers
     */
    private void reset() throws ProcessorException {

        try {
            regPC       = MOS6502Constants.VECTOR_RESET;
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
        } catch (MemoryException | InvalidBusDataException ex){
            throw new ProcessorException(MOS6502Constants.EX_RESET_ERROR + " : " + ex.getMessage());
        }

        instructionMapping = new MOS6502InstructionMapping();
    }

    /**
     * Determines whether a flag is currently set on the CPU status register
     * @param flag Flag to check
     * @return true = flag set, false = flag not set
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
            byte[] freeAddress = new byte[] { MOS6502Constants.STACK_PAGE, this.regSP };
            this.addressBus.writeDataToBus(freeAddress);
            this.dataBus.writeDataToBus(new byte[] {value});
            this.rwFlag.setFlagValue(false);
            this.regSP = (byte)(this.regSP + 0xFF); // Adding FF allowing a wrap around is easier than subtraction
        } catch (MemoryException | InvalidBusDataException e) {
            throw new ProcessorException(MOS6502Constants.EX_STACK_PUSH_ERROR + " - " + e.getMessage());
        }
    }

    /**
     * Pulls a byte of data from the stack
     * Increases the stack pointer by 1 then reads the value at that address
     * @return returns the byte read from the stack
     */
    private byte pullFromStack() throws ProcessorException {
        byte read;
        try {
            this.regSP = (byte)(this.regSP + 0x01);
            byte[] readAddress = new byte[] { MOS6502Constants.STACK_PAGE, this.regSP };
            this.addressBus.writeDataToBus(readAddress);
            this.rwFlag.setFlagValue(true);
            read = this.dataBus.readDataFromBus()[0];
        } catch (MemoryException | InvalidBusDataException e) {
            throw new ProcessorException(MOS6502Constants.EX_STACK_PUSH_ERROR + " - " + e.getMessage());
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
                case ACCUMULATOR:
                    break;
                case RELATIVE:
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
         * Stops the process of reading address if clock cycles increased, Leave ACCUMULATOR as used later
         */
        this.addressingMode = (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR) ? MOS6502AddressingMode.IMPLICIT : this.addressingMode;

        this.cycles--;
    }

    /**
     * Decodes the instruction and sets the number of cycles
     * @param instruction instruction to decode
     */
    private void decode(byte instruction) throws ProcessorException {

        MOS6502InstructionDetails details = this.instructionMapping.getInstructionDetails(instruction);

        if ( details != null ){
            this.instruction = details.instruction;
            this.addressingMode = details.addressingMode;
            this.cycles = details.cycles;
            this.cycles --;

            if (PRINT_TRACE) {
                System.out.println("Instruction: " + this.instruction.name());
                System.out.println("Addressing Mode: " + this.addressingMode.name());
            }

        } else {
            throw new ProcessorException(MOS6502Constants.EX_INVALID_INSTRUCTION + " : " + instruction);
        }
    }

    /**
     * Executes the current instruction and decrements the remaining cycles
     */
    private void execute() throws ProcessorException {

        switch (this.instruction){
            case ADC:
                ADC();
                break;

            case AND:
                AND();
                break;

            case ASL:
                ASL();
                break;

            case BIT:
                BIT();
                break;

            case BRK:
                BRK();
                break;

            /*
             * Branching conditions
             */
            case BCC:
                branch(!isFlagSet(MOS6502Flags.CARRY_FLAG));
                break;
            case BCS:
                branch(isFlagSet(MOS6502Flags.CARRY_FLAG));
                break;
            case BEQ:
                branch(isFlagSet(MOS6502Flags.ZERO_FLAG));
                break;
            case BMI:
                branch(isFlagSet(MOS6502Flags.NEGATIVE_FLAG));
                break;
            case BNE:
                branch(!isFlagSet(MOS6502Flags.ZERO_FLAG));
                break;
            case BPL:
                branch(!isFlagSet(MOS6502Flags.NEGATIVE_FLAG));
                break;
            case BVC:
                branch(!isFlagSet(MOS6502Flags.OVERFLOW_FLAG));
                break;
            case BVS:
                branch(isFlagSet(MOS6502Flags.OVERFLOW_FLAG));
                break;


            case CLC:
                clearFlag(MOS6502Flags.CARRY_FLAG);
                break;
            case CLD:
                clearFlag(MOS6502Flags.DECIMAL_MODE);
                break;
            case CLI:
                clearFlag(MOS6502Flags.INTERRUPT_DIS);
                break;
            case CLV:
                clearFlag(MOS6502Flags.OVERFLOW_FLAG);
                break;

            case CMP:
                CMP();
                break;

            case CPX:
                CPX();
                break;

            case CPY:
                CPY();
                break;

            case DEC:
                DEC();
                break;

            case DEX:
                DEX();
                break;

            case DEY:
                DEY();
                break;

            case EOR:
                EOR();
                break;



            case INX:
                INX();
                break;

            case INY:
                INY();
                break;

            case JMP:
                JMP();
                break;

            case JSR:
                JSR();
                break;

            case LDA:
                LDA();
                break;

            case LDX:
                LDX();
                break;

            case LDY:
                LDY();
                break;

            case LSR:
                LSR();
                break;

            case NOP:
                // No Operation
                break;

            case ORA:
                ORA();
                break;

            case PHA:
                pushToStack(this.regACC);
                break;

            case PHP:
                PHP();
                break;

            case PLA:
                PLA();
                break;

            case PLP:
                PLP();
                break;

            case ROL:
                ROL();
                break;

            case ROR:
                ROR();
                break;

            case RTI:
                RTI();
                break;

            case RTS:
                RTS();
                break;

            case SBC:
                SBC();
                break;

            case SEC:
                enableFlag(MOS6502Flags.CARRY_FLAG);
                break;
            case SED:
                enableFlag(MOS6502Flags.DECIMAL_MODE);
                break;
            case SEI:
                enableFlag(MOS6502Flags.INTERRUPT_DIS);
                break;

            case STA:
                ST(this.regACC);
                break;

            case STX:
                ST(this.regX);
                break;

            case STY:
                ST(this.regY);
                break;

            case TAX:
                this.regX = this.regACC;
                break;

            case TAY:
                this.regY = this.regACC;
                break;

            case TSX:
                this.regX = this.regSP;
                break;

            case TXA:
                this.regACC = this.regX;
                break;

            case TXS:
                this.regSP = this.regX;
                break;

            case TYA:
                this.regACC = this.regY;
                break;

            default:
                throw new ProcessorException(MOS6502Constants.EX_INVALID_INSTRUCTION + " : " + this.instruction);
        }
    }

    @Override
    public void onTick() throws ProcessorException {
        if ( this.cycles == 0 ){
            try {
                byte instruction = fetch();
                decode(instruction);
            } catch (Exception ex){
                throw new ProcessorException(MOS6502Constants.EX_TICK_FETCH_ERROR);
            }
        }
        else if ( this.cycles == 1 ) {
            addressing();
            execute();
            if (PRINT_TRACE)
                System.out.println();
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
            this.instruction = MOS6502Instruction.NOP;
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
     * Performs an arithmetic shift left i.e multiplies by 2
     * Where the result is stored depends on the addressing mode used
     * Sets the CARRY_FLAG to whatever was in bit 7
     * Arts the ZERO_FLAG if the result is 0x00
     * Sets the NEGATIVE_FLAG to the value of bit 7
     * @throws ProcessorException Can throw processor exception if there is a memory error
     */
    private void ASL() throws ProcessorException {
        byte value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                rwFlag.setFlagValue(true);
            } catch (MemoryException ex){
                throw new ProcessorException(ex.getMessage());
            }
            value = this.dataBus.readDataFromBus()[0];
        }

        // Carry Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.CARRY_FLAG);

        value = (byte)(((value & 0xFF) << 1) & 0xFF);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
        else
            clearFlag(MOS6502Flags.NEGATIVE_FLAG);

        // Zero Flag
        if (value == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                this.dataBus.writeDataToBus(new byte[]{value});
                rwFlag.setFlagValue(false);
            } catch (MemoryException | InvalidBusDataException ex){
                throw new ProcessorException(ex.getMessage());
            }
        } else {
            this.regACC = value;
        }

        if (PRINT_TRACE)
            System.out.println("ASL Result : " + String.format("%02X", value));
    }

    /**
     * Tests if one or more bits are set in the memory location.
     * The accumulator is used as the mask pattern and AND'd with the memory
     * value.
     * Sets the ZERO_FLAG if the result of AND == 0x00
     * Sets the OVERFLOW flag if bit 6 is set in the value from memory
     * Sets the NEGATIVE_FLAG if bit 7 is set in the value from memory
     * @throws ProcessorException Can throw a processor exception if there is an issue reading from memory
     */
    private void BIT() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = dataBus.readDataFromBus()[0];

        byte result = (byte)((this.regACC & value) & 0xFF);

        // Zero Flag
        if (result == 0x00) {
            enableFlag(MOS6502Flags.ZERO_FLAG);
        } else {
            clearFlag(MOS6502Flags.ZERO_FLAG);
        }

        // Overflow Flag based on memory value
        if ((value & 0b01000000) == 0b01000000){
            enableFlag(MOS6502Flags.OVERFLOW_FLAG);
        } else {
            clearFlag(MOS6502Flags.OVERFLOW_FLAG);
        }

        // Negative Flag based on memory value
        if ((value & 0b10000000) == 0b10000000){
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
        } else {
            clearFlag(MOS6502Flags.NEGATIVE_FLAG);
        }

        if (PRINT_TRACE)
            System.out.println("BIT (Flags) : " + String.format("%02X", this.getRegStatus()));
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
        this.regPC = MOS6502Constants.VECTOR_IRQ_BRK;

        /*
         * Set PC
         */
        low = fetch();
        high = fetch();
        this.regPC = (((high & 0xFF) << 8) | (low & 0xFF)) & 0xFFFF;

        /*
         * Clear flags
         */
        clearFlag(MOS6502Flags.BREAK_COMMAND);
        clearFlag(MOS6502Flags.IGNORED_FLAG);
    }

    /**
     * Compares the contents of the accumulator with a value held in memory
     * Sets CARRY_FLAG if Accumulator >= Memory Value
     * Sets ZERO_FLAG if Accumulator == Memory Value
     * Sets NEGATIVE_FLAG if Accumulator < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CMP() throws ProcessorException {
        try{
            this.rwFlag.setFlagValue(true);
            int value = (this.dataBus.readDataFromBus()[0] & 0xFF);
            int acc = (this.regACC & 0xFF);

            int result = acc - value;

            // Zero Flag
            if (result == 0) {
                enableFlag(MOS6502Flags.ZERO_FLAG);
            } else {
                clearFlag(MOS6502Flags.ZERO_FLAG);
            }

            // Negative Flag
            if ((result & 0b10000000) == 0b10000000){
                enableFlag(MOS6502Flags.NEGATIVE_FLAG);
            } else {
                clearFlag(MOS6502Flags.NEGATIVE_FLAG);
            }

            // Carry Flag
            if (result > 0){
                enableFlag(MOS6502Flags.CARRY_FLAG);
            } else {
                clearFlag(MOS6502Flags.CARRY_FLAG);
            }

        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }
    }

    /**
     * Compares the contents of the X register with a value held in memory
     * Sets CARRY_FLAG if X >= Memory Value
     * Sets ZERO_FLAG if X == Memory Value
     * Sets NEGATIVE_FLAG if X < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CPX() throws ProcessorException {
        try{
            this.rwFlag.setFlagValue(true);
            int value = (this.dataBus.readDataFromBus()[0] & 0xFF);
            int x = (this.regX & 0xFF);

            int result = x - value;

            // Zero Flag
            if (result == 0) {
                enableFlag(MOS6502Flags.ZERO_FLAG);
            } else {
                clearFlag(MOS6502Flags.ZERO_FLAG);
            }

            // Negative Flag
            if ((result & 0b10000000) == 0b10000000){
                enableFlag(MOS6502Flags.NEGATIVE_FLAG);
            } else {
                clearFlag(MOS6502Flags.NEGATIVE_FLAG);
            }

            // Carry Flag
            if (result > 0){
                enableFlag(MOS6502Flags.CARRY_FLAG);
            } else {
                clearFlag(MOS6502Flags.CARRY_FLAG);
            }

        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }
    }

    /**
     * Compares the contents of the Y register with a value held in memory
     * Sets CARRY_FLAG if Y >= Memory Value
     * Sets ZERO_FLAG if Y == Memory Value
     * Sets NEGATIVE_FLAG if Y < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CPY() throws ProcessorException {

        try{
            this.rwFlag.setFlagValue(true);
            int value = (this.dataBus.readDataFromBus()[0] & 0xFF);
            int y = (this.regY & 0xFF);

            int result = y - value;

            // Zero Flag
            if (result == 0) {
                enableFlag(MOS6502Flags.ZERO_FLAG);
            } else {
                clearFlag(MOS6502Flags.ZERO_FLAG);
            }

            // Negative Flag
            if ((result & 0b10000000) == 0b10000000){
                enableFlag(MOS6502Flags.NEGATIVE_FLAG);
            } else {
                clearFlag(MOS6502Flags.NEGATIVE_FLAG);
            }

            // Carry Flag
            if (result > 0){
                enableFlag(MOS6502Flags.CARRY_FLAG);
            } else {
                clearFlag(MOS6502Flags.CARRY_FLAG);
            }

        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }
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
     * Exclusive OR is performed on the accumulator and a value from memory
     * the result is stored in the accumulator
     * Sets the ZERO_FLAG if the accumulator becomes 0
     * Sets the NEGATIVE_FLAG if bit 7 of the accumulator becomes a 1
     * @throws ProcessorException If there is an issue reading from memory
     */
    private void EOR() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];

        this.regACC = (byte)(this.regACC ^ value);

        // Zero Flag
        if (this.regACC == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((this.regACC & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
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
     * Performs a logical shift right on the value
     * Where the result is stored depends on the addressing mode used
     * Sets the CARRY_FLAG to whatever was in bit 0
     * Bit 7 will always be set to 0
     * @throws ProcessorException
     */
    private void LSR() throws ProcessorException {
        byte value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                rwFlag.setFlagValue(true);
            } catch (MemoryException ex){
                throw new ProcessorException(ex.getMessage());
            }
            value = this.dataBus.readDataFromBus()[0];
        }

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);

        // Carry Flag
        if ((value & 0b00000001) == 0b00000001)
            enableFlag(MOS6502Flags.CARRY_FLAG);

        value = (byte)(((value & 0xFF) >>> 1) & 0xFF);

        // Zero Flag
        if (value == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                this.dataBus.writeDataToBus(new byte[]{value});
                rwFlag.setFlagValue(false);
            } catch (MemoryException | InvalidBusDataException ex){
                throw new ProcessorException(ex.getMessage());
            }
        } else {
            this.regACC = value;
        }

        if (PRINT_TRACE)
            System.out.println("LSR Result : " + String.format("%02X", value));
    }

    /**
     * Performs an inclusive OR operation on a value in memory and
     * the accumulator string the result in the accumulator
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of accumulator becomes a 1
     * @throws ProcessorException Can throw a ProcessorException when issues reading memory
     */
    private void ORA() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];

        this.regACC = (byte)(this.regACC | value);

        // Zero Flag
        if (this.regACC == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        // Negative Flag
        if ((this.regACC & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
    }

    /**
     * Pushes a copy of the status flag onto the stack
     * Note the is a caveat with this instruction that bits 4 and 5
     * are always set in the stack but not on the actual flags
     * http://wiki.nesdev.com/w/index.php/Status_flags
     */
    private void PHP() throws ProcessorException {

        byte value = (byte)(this.regStatus | 0b00110000);

        pushToStack(value);

        if (PRINT_TRACE)
            System.out.println("PHP Pushed : " + String.format("%02X", value));
    }

    /**
     * Pulls Accumulator from the stack
     * Sets ZERO_FLAG if accumulator becomes zero
     * Sets NEGATIVE_FLAG if bit 7 of accumulator is a 1
     * @throws ProcessorException Can throw memory exception on error reading memory
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
     * Pulls processor status from the stack
     * Note bits 4 and 5 are ignored when pulled
     * http://wiki.nesdev.com/w/index.php/Status_flags
     * @throws ProcessorException Can throw memory exception on error reading memory
     */
    private void PLP() throws ProcessorException {
        byte flags = pullFromStack();
        if (PRINT_TRACE)
            System.out.println("PLPFlags Pulled: " + String.format("%02X", flags));

        flags = (byte)(flags & 0b11001111);

        if (PRINT_TRACE)
            System.out.println("PLP Flags Masked: " + String.format("%02X", flags));

        this.regStatus = flags;
    }

    /**
     * Moves each bit in a value one place to the left
     * Bit 0 is set by the CARRY_FLAG
     * Sets CARRY_FLAG to original bit 7
     * @throws ProcessorException Can throw processor exception on memory errors
     */
    private void ROL() throws ProcessorException {
        byte value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                rwFlag.setFlagValue(true);
            } catch (MemoryException ex){
                throw new ProcessorException(ex.getMessage());
            }
            value = this.dataBus.readDataFromBus()[0];
        }

        byte tmp = 0x00;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            tmp = (byte)0x01;

        // Carry Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.CARRY_FLAG);
        else
            clearFlag(MOS6502Flags.CARRY_FLAG);

        value = (byte)((((value & 0xFF)  << 1) + tmp) & 0xFF);


        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
        else
            clearFlag(MOS6502Flags.NEGATIVE_FLAG);

        // Zero Flag
        if (value == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                this.dataBus.writeDataToBus(new byte[]{value});
                rwFlag.setFlagValue(false);
            } catch (MemoryException | InvalidBusDataException ex){
                throw new ProcessorException(ex.getMessage());
            }
        } else {
            this.regACC = value;
        }

        if (PRINT_TRACE)
            System.out.println("ROL Result : " + String.format("%02X", value));
    }

    private void ROR() throws ProcessorException {
        byte value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                rwFlag.setFlagValue(true);
            } catch (MemoryException ex){
                throw new ProcessorException(ex.getMessage());
            }
            value = this.dataBus.readDataFromBus()[0];
        }

        byte tmp = 0x00;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            tmp = (byte)0x80;

        // Carry Flag
        if ((value & 0b00000001) == 0b00000001)
            enableFlag(MOS6502Flags.CARRY_FLAG);
        else
            clearFlag(MOS6502Flags.CARRY_FLAG);

        value = (byte)((((value & 0xFF) >> 1) + tmp) & 0xFF);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(MOS6502Flags.NEGATIVE_FLAG);
        else
            clearFlag(MOS6502Flags.NEGATIVE_FLAG);

        // Zero Flag
        if (value == 0x00)
            enableFlag(MOS6502Flags.ZERO_FLAG);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            try {
                this.dataBus.writeDataToBus(new byte[]{value});
                rwFlag.setFlagValue(false);
            } catch (MemoryException | InvalidBusDataException ex){
                throw new ProcessorException(ex.getMessage());
            }
        } else {
            this.regACC = value;
        }

        if (PRINT_TRACE)
            System.out.println("ROL Result : " + String.format("%02X", value));
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
        //fetch();

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
     * Perform a subtraction with carry
     * Acc = Acc - Value - (1 - Carry)
     * Simplifies to
     * Acc = A + -Value + 1 + Carry
     * Can take the 2's compliment of value from memory then perform same steps as ADC
     * Sets the ZERO_FLAG if Accumulator becomes 0
     * Sets the NEGATIVE_FLAG if bit 7 is set
     *
     * @throws ProcessorException Can throw a ProcessorException if there is an issue writing to memory
     */
    private void SBC() throws ProcessorException {
        try {
            rwFlag.setFlagValue(true);
        } catch (MemoryException ex){
            throw new ProcessorException(ex.getMessage());
        }

        byte value = this.dataBus.readDataFromBus()[0];
        // perform 2's compliment
        value = (byte)(~value & 0xFF);
        value = (byte)((value + 0x01)& 0xFF);

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
            System.out.println("SBC : " + String.format("%02X", this.regACC));
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
