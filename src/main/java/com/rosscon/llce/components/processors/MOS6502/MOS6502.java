package com.rosscon.llce.components.processors.MOS6502;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.busses.InvalidBusDataException;
import com.rosscon.llce.components.clocks.Clock;
import com.rosscon.llce.components.flags.*;
import com.rosscon.llce.components.processors.Processor;
import com.rosscon.llce.components.processors.ProcessorException;



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
public class MOS6502 extends Processor implements FlagListener {

    /**
     * Debugging
     */
    private boolean PRINT_TRACE = false;           // Used to enable printing information to stdout

    private MOS6502InstructionMapping instructionMapping;

    /**
     * Registers
     */
    private int     regPC;      // Program Counter
    private int     regSP;      // Stack Pointer
    private int     regACC;     // Accumulator;
    private int     regX;       // Index Register X
    private int     regY;       // Index Register Y
    private int     regStatus;  // Processor Status [C][Z][I][D][B][V][N]
    private int     regIntAddr; // Custom register used for building addresses over multiple cycles


    /**
     * NMI, Non maskable interrupt
     */
    private boolean nmiTriggered;


    public int getRegPC() {
        return this.regPC;
    }

    public int getRegIntAddr() {
        return this.regIntAddr;
    }

    /**
     * Getters for unit testing
     */
    public int getRegSP() {
        return regSP;
    }

    public int getRegACC() {
        return regACC;
    }

    public int getRegX() {
        return regX;
    }

    public int getRegY() {
        return regY;
    }

    public int getRegStatus() {
        return regStatus;
    }

    /**
     * Cycle tracking
     */
    private int cycles;

    /**
     * Current Instruction
     */
    private MOS6502Instruction instruction;

    /**
     * Addressing Mode
     * http://www.obelisk.me.uk/6502/addressing.html#IMP
     */
    private MOS6502AddressingMode addressingMode;

    /**
     * 6502 Interrupts
     */
    private NMIFlag flgNmi;
    private HaltFlag flgHalt;

    /**
     * Read a value from memory. Clear the data bus before reading to prevent
     * accidentally reading lingering data from previous cycles
     * @param address address to request data
     * @return the value held at the given address
     * @throws ProcessorException Can be thrown on memory error
     */
    private int cpuRead(int address) throws ProcessorException {
        try {
            this.dataBus.writeDataToBus(0x00);
            this.addressBus.writeDataToBus(address);
            this.flgRW.setFlagValue(RWFlag.READ);
        } catch (InvalidBusDataException | FlagException e) {
            ProcessorException pe = new ProcessorException(MOS6502Constants.EX_ERROR_READING_MEMORY);
            pe.addSuppressed(e);
            throw pe;
        }
        return dataBus.readDataFromBus();
    }

    /**
     * Write a value to memory.
     * @param address address to write to
     * @param data value to be written
     * @throws ProcessorException Can be thrown on memory error
     */
    private void cpuWrite (int address, int data) throws ProcessorException {
        try {
            this.dataBus.writeDataToBus(data);
            this.addressBus.writeDataToBus(address);
            this.flgRW.setFlagValue(RWFlag.WRITE);
        } catch (InvalidBusDataException | FlagException e) {
            ProcessorException pe = new ProcessorException(MOS6502Constants.EX_ERROR_WRITING_MEMORY);
            pe.addSuppressed(e);
            throw pe;
        }
    }


    /**
     * Default constructor builds the processor, calls super to connect to busses, clock and external flags
     * Also calls the reset function to initialise the processor
     * @param clock Clock
     * @param addressBus Address Bus
     * @param dataBus Data Bus
     * @param flgRW External R/W Flag set by processor
     */
    public MOS6502(Clock clock, IntegerBus addressBus, IntegerBus dataBus, RWFlag flgRW, NMIFlag flgNmi, HaltFlag flgHalt) throws ProcessorException {
        super(clock, addressBus, dataBus, flgRW);
        this.flgNmi = flgNmi;
        this.flgHalt = flgHalt;
        reset();

        try {
            if (flgNmi != null) this.flgNmi.addListener(this);
            if (flgHalt != null) this.flgHalt.addListener(this);
        } catch (Exception ex){
            ProcessorException pe = new ProcessorException(MOS6502Constants.EX_ERROR_LISTENING_TO_FLAG);
            pe.addSuppressed(ex);
            throw pe;
        }
    }

    public MOS6502(Clock clock, IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, NMIFlag flgNmi, HaltFlag flgHalt, boolean printTrace) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;

        this.flgNmi = flgNmi;
        this.flgHalt = flgHalt;

        try {
            if (flgNmi != null) this.flgNmi.addListener(this);
            if (flgHalt != null) this.flgHalt.addListener(this);
        } catch (Exception ex){
            ProcessorException pe = new ProcessorException(MOS6502Constants.EX_ERROR_LISTENING_TO_FLAG);
            pe.addSuppressed(ex);
            throw pe;
        }
    }

    public MOS6502(Clock clock, IntegerBus addressBus, IntegerBus dataBus, RWFlag rwFlag, NMIFlag flgNmi, HaltFlag flgHalt, boolean printTrace, int pcOverride) throws ProcessorException {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;
        this.regPC = pcOverride;

        this.flgNmi = flgNmi;
        this.flgHalt = flgHalt;

        try {
            if (flgNmi != null) this.flgNmi.addListener(this);
            if (flgHalt != null) this.flgHalt.addListener(this);
        } catch (Exception ex){
            ProcessorException pe = new ProcessorException(MOS6502Constants.EX_ERROR_LISTENING_TO_FLAG);
            pe.addSuppressed(ex);
            throw pe;
        }
    }

    /**
     * Reset/Initialise registers
     */
    private void reset() throws ProcessorException {

        regPC       = MOS6502Constants.VECTOR_RESET;
        regSP       = 0xFF;
        regACC      = 0x00;
        regX        = 0x00;
        regY        = 0x00;
        regStatus   = 0x00;
        cycles      = 0;
        regIntAddr  = 0x000;

        /*
         * Read/Follow the reset vector
         */
        int low = cpuRead(this.regPC);
        int high = cpuRead(this.regPC + 1);
        regIntAddr = low | (high << 8);
        this.regPC = regIntAddr;

        nmiTriggered = false;
        instructionMapping = new MOS6502InstructionMapping();
    }

    /**
     * Determines whether a flag is currently set on the CPU status register
     * @param flag Flag to check
     * @return true = flag set, false = flag not set
     */
    private boolean isFlagSet(int flag){

        int tmp = this.regStatus & flag;
        return tmp != 0;
    }

    /**
     * Pushes a value to the stack then decrements the stack pointer by 1
     * @param value value to push to the stack
     */
    private void pushToStack(int value) throws ProcessorException {
        int freeAddress = (MOS6502Constants.STACK_PAGE << 8) | this.regSP;
        cpuWrite(freeAddress, value);
        this.regSP = (this.regSP - 1) & 0x000000FF; // Subtract 1 then mask
    }

    /**
     * Pulls a byte of data from the stack
     * Increases the stack pointer by 1 then reads the value at that address
     * @return returns the byte read from the stack
     */
    private int pullFromStack() throws ProcessorException {
        this.regSP = (this.regSP + 1) & 0x000000FF; // Add 1 then mask
        int readAddress = (MOS6502Constants.STACK_PAGE << 8) | this.regSP;
        return cpuRead(readAddress);
    }

    /**
     * Fetches next instruction and increments the program counter
     */
    private int fetch() throws ProcessorException {

        int fetchedData = cpuRead(getRegPC());
        this.regPC = ((this.regPC + 1) & 0xFFFF);

        if (PRINT_TRACE){
            System.out.print("Fetch : [" + String.format("%02X", this.regPC) + "] ");
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
                case IMMEDIATE:
                    this.regIntAddr = this.regPC;
                    this.regPC = (this.regPC + 1) & 0xFFFF;
                    break;

                case ZERO_PAGE:     // Move to next address in memory, read contents, build zero page address from it
                    this.regIntAddr = cpuRead(this.regPC);
                    this.regPC = (this.regPC + 1) & 0xFFFF;
                    break;

                case ZERO_PAGE_X:
                    this.regIntAddr = (cpuRead(this.regPC) + this.regX) & 0x00FF;
                    this.regPC = (this.regPC + 1) & 0xFFFF;
                    break;

                case ZERO_PAGE_Y:
                    this.regIntAddr = (cpuRead(this.regPC) + this.regY) & 0x00FF;
                    this.regPC = (this.regPC + 1) & 0xFFFF;
                    break;

                case ABSOLUTE:
                    this.regIntAddr = cpuRead(this.regPC) | (cpuRead(this.regPC + 1) << 8);
                    this.regPC = (this.regPC + 2) & 0xFFFF;
                    break;

                case ABSOLUTE_X:
                    this.regIntAddr = cpuRead(this.regPC) | (cpuRead(this.regPC + 1) << 8);
                    this.regPC = (this.regPC + 2) & 0xFFFF;
                    int tmpAbx = this.regIntAddr & 0xFF00;
                    this.regIntAddr = (this.regIntAddr + this.regX) & 0xFFFF;

                    if ((tmpAbx & 0xFF00) != (this.regIntAddr & 0xFF00))
                        this.cycles ++;
                    break;

                case ABSOLUTE_Y:
                    this.regIntAddr = cpuRead(this.regPC) | (cpuRead(this.regPC + 1) << 8);
                    this.regPC = (this.regPC + 2) & 0xFFFF;
                    int tmpAby = this.regIntAddr & 0xFF00;
                    this.regIntAddr = (this.regIntAddr + this.regY) & 0xFFFF;

                    if ((tmpAby & 0xFF00) != (this.regIntAddr & 0xFF00))
                        this.cycles ++;
                    break;

                case INDIRECT:
                    this.regIntAddr = cpuRead(this.regPC) | (cpuRead(this.regPC + 1) << 8);

                    /*
                     * Hardware bug to account for
                     */
                    if ((this.regIntAddr & 0x00FF) == 0x00FF){
                        this.regIntAddr = (cpuRead(this.regIntAddr)) | (cpuRead(this.regIntAddr & 0xFF00) << 8);
                    } else {
                        this.regIntAddr = (cpuRead(this.regIntAddr)) | (cpuRead((this.regIntAddr) + 1 & 0xFFFF) << 8);
                    }

                    this.regPC = (this.regPC + 2) & 0xFFFF;
                    break;

                case INDEXED_INDIRECT_X:
                    int tmpIndX = cpuRead(this.regPC);
                    int tmpIndXLow = cpuRead((tmpIndX + this.regX) & 0x00FF);
                    int tmpIndXHigh = cpuRead((tmpIndX + this.regX + 1) & 0x00FF);
                    this.regIntAddr = tmpIndXLow | (tmpIndXHigh << 8);
                    this.regPC = (this.regPC + 1) & 0xFFFF;
                    break;

                case INDIRECT_INDEXED_Y:
                    int tmpIndY = cpuRead(this.regPC);
                    int tmpIndYLow = cpuRead(tmpIndY & 0x00FF);
                    int tmpIndYHigh = cpuRead((tmpIndY + 1) & 0x00FF);
                    this.regIntAddr = tmpIndYLow | (tmpIndYHigh << 8);
                    this.regIntAddr = (this.regIntAddr + this.regY) & 0xFFFF;

                    if ((this.regIntAddr & 0xFF00) != (tmpIndYHigh << 8))
                        this.cycles ++;

                    this.regPC = (this.regPC + 1) & 0xFFFF;
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
    private void decode(int instruction) throws ProcessorException {

        MOS6502InstructionDetails details = this.instructionMapping.getInstructionDetails(instruction);

        if (details == null){
            details = this.instructionMapping.getInstructionDetails(MOS6502Instructions.INS_NOP_IMP);
        }

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
                setFlag(MOS6502Flags.CARRY_FLAG, false);
                break;
            case CLD:
                setFlag(MOS6502Flags.DECIMAL_MODE, false);
                break;
            case CLI:
                setFlag(MOS6502Flags.INTERRUPT_DIS, false);
                break;
            case CLV:
                setFlag(MOS6502Flags.OVERFLOW_FLAG, false);
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

            case INC:
                INC();
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
                setFlag(MOS6502Flags.CARRY_FLAG, true);
                break;
            case SED:
                setFlag(MOS6502Flags.DECIMAL_MODE, true);
                break;
            case SEI:
                setFlag(MOS6502Flags.INTERRUPT_DIS, true);
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
                setFlag(MOS6502Flags.ZERO_FLAG, this.regX == 0x00);
                setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regX & 0x0080) != 0);
                break;

            case TAY:
                this.regY = this.regACC;
                setFlag(MOS6502Flags.ZERO_FLAG, this.regY == 0x00);
                setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regY & 0x0080) != 0);
                break;

            case TSX:
                this.regX = this.regSP;
                setFlag(MOS6502Flags.ZERO_FLAG, this.regX == 0x00);
                setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regX & 0x0080) != 0);
                break;

            case TXA:
                this.regACC = this.regX;
                setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);
                setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & 0x0080) != 0);
                break;

            case TXS:
                this.regSP = this.regX;
                break;

            case TYA:
                this.regACC = this.regY;
                setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);
                setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & 0x0080) != 0);
                break;

            default:
                throw new ProcessorException(MOS6502Constants.EX_INVALID_INSTRUCTION + " : " + this.instruction);
        }
    }

    @Override
    public void onTick() throws ProcessorException {
        if ( this.cycles == 0 ){
            try {
                // If NMI triggered perform a BRK
                int instruction = this.nmiTriggered ? MOS6502Instructions.INS_BRK_IMP : fetch();
                decode(instruction);
            } catch (Exception ex) {
                ProcessorException pe = new ProcessorException(MOS6502Constants.EX_TICK_FETCH_ERROR + " " + ex.getMessage());
                pe.addSuppressed(ex);
                throw pe;
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
     * Sets or unsets a flag given a boolean value, true = enable, false = disable
     * @param flag flag to set
     * @param val boolean operator
     */
    private void setFlag(int flag, boolean val){
        if (val)
            this.regStatus |= flag;
        else
            this.regStatus &= ~flag;
    }

    /**
     * Performs the necessary steps to complete a branching instruction
     * Not being 100% cycle accurate on this one. Will perform all the functions of
     * incrementing the program counter from value fetched in memory.
     * Then increments the number of cycles +1 default, +2 if the page has changed
     * Finally the current instruction is set to NOP and addressing mode to IMPLIED
     * to prevent the program counter from also being affected.
     */
    private void branch(boolean branchSucceeded) throws ProcessorException {
        if (branchSucceeded){
            // Add a cycle just for branch occurring
            this.cycles++;

            int initialAddress = this.regIntAddr;
            int value = (byte)cpuRead(this.regIntAddr);

            //In this scenario we want to treat the value as a signed number;
            int newAddress = this.regPC + value;

            // Detect if the page has changed
            if ((this.regIntAddr & 0x0000FF00) != (newAddress & 0x0000FF00))
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

        int value = cpuRead(this.regIntAddr);

        int result = value + regACC;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            result++;

        setFlag(MOS6502Flags.CARRY_FLAG, (result & 0xFF00) != 0);
        setFlag(MOS6502Flags.ZERO_FLAG, (result & 0x00FF) == 0);
        setFlag(MOS6502Flags.OVERFLOW_FLAG, ((result ^ this.regACC) & (result ^ value) & 0x0080) != 0);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (result & 0x0080) != 0);

        this.regACC = (result & 0x00FF);

        if (PRINT_TRACE)
            System.out.println("ADC : " + String.format("%02X", this.regACC));
    }

    /**
     * Performs a logical between the accumulator and a memory location
     * Sets zero flag if the result == 0
     * Sets the negative flag if bit 7 is set
     */
    private void AND() throws ProcessorException {

        int value = cpuRead(this.regIntAddr);
        this.regACC = this.regACC & value;

        setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            value = cpuRead(this.regIntAddr);
        }

        value <<= 1;

        setFlag(MOS6502Flags.CARRY_FLAG, (value & 0xFF00) != 0);
        setFlag(MOS6502Flags.ZERO_FLAG, (value & 0x00FF) == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & 0x0080) != 0);

        value &= 0x00FF;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR) {
            cpuWrite(this.regIntAddr, value);
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

        int value = cpuRead(this.regIntAddr);

        int result = this.regACC & value;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, result == 0x00);

        // Overflow Flag based on memory value
        setFlag(MOS6502Flags.OVERFLOW_FLAG, (value & 0b01000000) == 0b01000000);

        // Negative Flag based on memory value
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int high = (this.regPC >>> 8);
        pushToStack(high);
        if (PRINT_TRACE)
            System.out.println("BRK Pushed High: " + String.format("%02X", high));

        /*
         * Push PC low byte to stack
         */
        int low = this.regPC & MOS6502Constants.MASK_LAST_BYTE;
        pushToStack(low);
        if (PRINT_TRACE)
            System.out.println("BRK Pushed Low: " + String.format("%02X", low));

        /*
         * Set flags
         */
        setFlag(MOS6502Flags.BREAK_COMMAND, !this.nmiTriggered);
        setFlag(MOS6502Flags.IGNORED_FLAG, true);

        /*
         * Push flags to stack
         */
        pushToStack(this.regStatus);
        if (PRINT_TRACE)
            System.out.println("BRK Pushed flags : " + String.format("%02X", this.regStatus));


        /*
         * Set PC to IRQ_BRK or IRQ_NMI vector depending on whether a NMI was triggered
         */
        if (PRINT_TRACE && this.nmiTriggered)
            System.out.println("BRK NMI");
        this.regPC = this.nmiTriggered ? MOS6502Constants.VECTOR_NMI : MOS6502Constants.VECTOR_IRQ_BRK;

        /*
         * Set PC
         */
        low = fetch();
        high = fetch();
        this.regPC = (high << 8) | low;

        /*
         * Clear flags
         */
        setFlag(MOS6502Flags.BREAK_COMMAND, false);
        setFlag(MOS6502Flags.IGNORED_FLAG, false);

        setFlag(MOS6502Flags.INTERRUPT_DIS, this.nmiTriggered);
        this.nmiTriggered = false;
    }

    /**
     * Compares the contents of the accumulator with a value held in memory
     * Sets CARRY_FLAG if Accumulator >= Memory Value
     * Sets ZERO_FLAG if Accumulator == Memory Value
     * Sets NEGATIVE_FLAG if Accumulator < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CMP() throws ProcessorException {
        int value = cpuRead(this.regIntAddr);
        int tmp = this.regACC - value;

        setFlag(MOS6502Flags.CARRY_FLAG, value <= this.regACC);
        setFlag(MOS6502Flags.ZERO_FLAG, (tmp & 0x00FF) == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (tmp & 0x0080) != 0);
    }

    /**
     * Compares the contents of the X register with a value held in memory
     * Sets CARRY_FLAG if X >= Memory Value
     * Sets ZERO_FLAG if X == Memory Value
     * Sets NEGATIVE_FLAG if X < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CPX() throws ProcessorException {
        int value = cpuRead(this.regIntAddr);
        int tmp = this.regX - value;

        setFlag(MOS6502Flags.CARRY_FLAG, value <= this.regX);
        setFlag(MOS6502Flags.ZERO_FLAG, (tmp & 0x00FF) == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (tmp & 0x0080) != 0);
    }

    /**
     * Compares the contents of the Y register with a value held in memory
     * Sets CARRY_FLAG if Y >= Memory Value
     * Sets ZERO_FLAG if Y == Memory Value
     * Sets NEGATIVE_FLAG if Y < Memory Value
     * @throws ProcessorException Can throw ProcessorException on memory read error
     */
    private void CPY() throws ProcessorException {
        int value = cpuRead(this.regIntAddr);
        int tmp = this.regY - value;

        setFlag(MOS6502Flags.CARRY_FLAG, value <= this.regY);
        setFlag(MOS6502Flags.ZERO_FLAG, (tmp & 0x00FF) == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (tmp & 0x0080) != 0);
    }

    /**
     * Reads the value on the data bus, then decrements it on bus and sets the bus to write
     * Finally any flags are set
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEC() throws ProcessorException {
        int value = cpuRead(this.regIntAddr);

        value = (value - 1) & MOS6502Constants.MASK_LAST_BYTE;
        cpuWrite(this.getRegIntAddr(), value);

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & MOS6502Constants.MASK_NEGATIVE) != 0);

        if (PRINT_TRACE)
            System.out.println("DEC : " + String.format("%02X", value));
    }

    /**
     * Decrements the value of the X register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEX() {
        this.regX = (this.regX -1) & MOS6502Constants.MASK_LAST_BYTE;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regX == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regX & MOS6502Constants.MASK_NEGATIVE) != 0);

        if (PRINT_TRACE)
            System.out.println("DEX : " + String.format("%02X", this.regX));
    }

    /**
     * Decrements the value of the Y register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void DEY() {
        this.regY = (this.regY -1) & MOS6502Constants.MASK_LAST_BYTE;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regY == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regY & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int value = cpuRead(this.regIntAddr);

        this.regACC = this.regACC ^ value;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & MOS6502Constants.MASK_NEGATIVE) != 0);

        if (PRINT_TRACE)
            System.out.println("EOR : " + String.format("%02X", this.regACC));
    }

    /**
     * Adds one to a value held in memory
     * Sets ZERO_FLAG if result == 0x00
     * Sets NEGATIVE_FLAG to bit 7
     * @throws ProcessorException Can throw processor exception on memory errors
     */
    private void INC() throws ProcessorException {
        int value = cpuRead(this.regIntAddr);

        value = (value + 1) & MOS6502Constants.MASK_LAST_BYTE;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & MOS6502Constants.MASK_NEGATIVE) != 0);

        cpuWrite(this.regIntAddr, value);

        if (PRINT_TRACE)
            System.out.println("INC : " + String.format("%02X", value));
    }

    /**
     * Increments the X register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void INX() {
        this.regX = (this.regX + 1) & MOS6502Constants.MASK_LAST_BYTE;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regX == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regX & MOS6502Constants.MASK_NEGATIVE) != 0);

        if (PRINT_TRACE)
            System.out.println("INX : " + String.format("%02X", this.regX));
    }


    /**
     * Increments the Y register by 1
     * Sets ZERO_FLAG if == 0x0
     * Sets NEGATIVE_FLAG if but 7 is set to a 1
     */
    private void INY() {
        this.regY = (this.regY + 1) & MOS6502Constants.MASK_LAST_BYTE;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regY == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regY & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int tmp = (this.regPC - 1) & 0xFFFF;
        int high = tmp >>> 8;
        pushToStack(high);
        if (PRINT_TRACE)
            System.out.println("JSR Pushed : " + String.format("%02X", high));

        /*
         * Push PC low byte to stack
         */
        int low = tmp & MOS6502Constants.MASK_LAST_BYTE;
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
        int value = cpuRead(this.regIntAddr);

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int value = cpuRead(this.regIntAddr);
        this.regX = value & 0x00FF;

        setFlag(MOS6502Flags.ZERO_FLAG, this.regX == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regX & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int value = cpuRead(this.regIntAddr);
        this.regY = value & 0x00FF;

        setFlag(MOS6502Flags.ZERO_FLAG, this.regY == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regY & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            value = cpuRead(this.regIntAddr);
        }

        setFlag(MOS6502Flags.CARRY_FLAG, (value & 0x0001) == 0x0001);
        value >>>= 1;
        value = value & 0x00FF;

        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & 0x0080) !=0);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            cpuWrite(this.regIntAddr, value);
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
        int value = cpuRead(this.regIntAddr);

        this.regACC = this.regACC | value;

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & MOS6502Constants.MASK_NEGATIVE) != 0);
    }

    /**
     * Pushes a copy of the status flag onto the stack
     * Note the is a caveat with this instruction that bits 4 and 5
     * are always set in the stack but not on the actual flags
     * http://wiki.nesdev.com/w/index.php/Status_flags
     */
    private void PHP() throws ProcessorException {

        int value = this.regStatus | 0b00110000;

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
        setFlag(MOS6502Flags.ZERO_FLAG, this.regACC == 0x00);

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (this.regACC & MOS6502Constants.MASK_NEGATIVE) != 0);

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
        int flags = pullFromStack();
        if (PRINT_TRACE)
            System.out.println("PLPFlags Pulled: " + String.format("%02X", flags));

        flags = flags & 0b11001111;

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
        int value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            value = cpuRead(this.regIntAddr);
        }

        int tmp = 0x00;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            tmp = 0x01;

        // Carry Flag
        setFlag(MOS6502Flags.CARRY_FLAG, (value & 0b10000000) != 0);

        value = ((value << 1) + tmp) & MOS6502Constants.MASK_LAST_BYTE;


        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & MOS6502Constants.MASK_NEGATIVE) != 0);

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            cpuWrite(regIntAddr, value);
        } else {
            this.regACC = value;
        }

        if (PRINT_TRACE)
            System.out.println("ROL Result : " + String.format("%02X", value));
    }

    /**
     * Moves each bit one place to the right
     * @throws ProcessorException Can throw processor exception on memory errors
     */
    private void ROR() throws ProcessorException {
        int value = this.regACC;

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            value = cpuRead(this.regIntAddr);
        }

        int tmp = 0x00;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            tmp = 0x80;

        // Carry Flag
        setFlag(MOS6502Flags.CARRY_FLAG, (value & 0x01) != 0);

        value = ((value >> 1) + tmp) & MOS6502Constants.MASK_LAST_BYTE;

        // Negative Flag
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (value & 0b10000000) == 0b10000000);

        // Zero Flag
        setFlag(MOS6502Flags.ZERO_FLAG, value == 0x00);

        if (this.addressingMode != MOS6502AddressingMode.ACCUMULATOR){
            cpuWrite(regIntAddr, value);
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
        int flags = pullFromStack();
        if (PRINT_TRACE)
            System.out.println("RTI Flags Pulled: " + String.format("%02X", flags));

        flags = flags & 0b11001111;

        if (PRINT_TRACE)
            System.out.println("RTI Flags Masked: " + String.format("%02X", flags));

        this.regStatus = flags;

        this.regPC = pullFromStack();
        this.regPC = this.regPC | (pullFromStack() << 8);
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
        this.regPC = this.regPC | (pullFromStack() << 8);
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
        int value = cpuRead(this.regIntAddr);

        value = (value ^ 0x00FF);

        int result = this.regACC + value;
        if (isFlagSet(MOS6502Flags.CARRY_FLAG))
            result ++;

        setFlag(MOS6502Flags.CARRY_FLAG, (result & 0xFF00) != 0);
        setFlag(MOS6502Flags.ZERO_FLAG, (result & 0x00FF) == 0);
        setFlag(MOS6502Flags.OVERFLOW_FLAG, ((result ^ this.regACC) & (result ^ value) & 0x0080) != 0);
        setFlag(MOS6502Flags.NEGATIVE_FLAG, (result & 0x0080) != 0);

        this.regACC = (result & 0x0FF);
    }

    /**
     * Stores the value to the memory address set. Used for STA, STX, STY
     * @param value value to store in memory
     * @throws ProcessorException Can throw a ProcessorException if there is an issue writing to memory
     */
    private void ST(int value) throws ProcessorException {
        if (PRINT_TRACE)
            System.out.println("ST : " + String.format("%02X", value));

        cpuWrite(regIntAddr, value);
    }

    @Override
    public void onFlagChange(Flag flag) throws FlagException {
        if (flag instanceof NMIFlag && flag.getFlagValue() == NMIFlag.NMI){
            this.nmiTriggered = true;
        }

        else if (flag instanceof  HaltFlag && flag.getFlagValue() == HaltFlag.HALT){
            // TODO start HALT
        }

        else if (flag instanceof HaltFlag && flag.getFlagValue() == HaltFlag.START){
            // TODO REsume CPU execution
        }
    }
}
