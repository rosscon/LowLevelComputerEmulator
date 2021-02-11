package com.rosscon.llce.components.processors.NMOS6502;

import com.rosscon.llce.components.busses.Bus;
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


    /**
     * Registers
     */
    private byte[]  pc;     // Program Counter
    private byte[]  sp;     // Stack Pointer
    private byte    acc;    // Accumulator;
    private byte    inx;    // Index Register X
    private byte    iny;    // Index Register Y
    private byte    status; // Processor Status [C][Z][I][D][B][V][N]
    private byte[]  addr;   // A custom register used for building addresses over multiple cycles
    private boolean carry;  // A custom carry register for handling ABS X,Y

    /**
     * Getters for unit testing
     */
    public byte[] getPc() {
        return pc;
    }

    public byte[] getSp() {
        return sp;
    }

    public byte getAcc() {
        return acc;
    }

    public byte getInx() {
        return inx;
    }

    public byte getIny() {
        return iny;
    }

    public byte getStatus() {
        return status;
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
    public NMOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag) {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
    }

    public NMOS6502(Clock clock, Bus addressBus, Bus dataBus, Flag rwFlag, boolean printTrace) {
        super(clock, addressBus, dataBus, rwFlag);
        reset();
        this.PRINT_TRACE = printTrace;
    }

    /**
     * Reset/Initialise registers
     */
    private void reset(){
        pc      = new byte[]{ (byte)0xFF, (byte) 0xFC };
        sp      = new byte[]{ (byte)0x01, (byte) 0x00 };
        acc     = (byte) 0x00;
        inx     = (byte) 0x00;
        iny     = (byte) 0x00;
        status  = (byte) 0x00;
        cycles  = 0;
        addr    = new byte[2];

        instructionMapping = new NMOS6502InstructionMapping();
    }

    /**
     * Fetches next instruction and increments the program counter
     */
    private byte fetch() throws ProcessorException {

        byte fetchedData;

        try{
            addressBus.writeDataToBus(pc);
            rwFlag.setFlagValue(true);
            fetchedData = dataBus.readDataFromBus()[0];
            if (PRINT_TRACE)
                System.out.print("Fetch : [" + String.format("%02X", this.pc[0]) +
                    String.format("%02X", this.pc[1]) + "] ");

            pc = ByteArrayUtils.increment(pc);
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
                    if (this.cycles == 2) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                        this.addr[0] = 0x00;
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case ZERO_PAGE_X:
                    if (this.cycles == 3) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                    } else if(this.cycles == 2){
                        this.addr[1] = (byte)(this.addr[1] + this.inx);
                        this.addr[0] = 0x00;
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case ZERO_PAGE_Y:
                    if (this.cycles == 3) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                    } else if(this.cycles == 2){
                        this.addr[1] = (byte)(this.addr[1] + this.iny);
                        this.addr[0] = 0x00;
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case ABSOLUTE:
                    if (this.cycles == 3) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                    } else if (this.cycles == 2) {         // On first cycle read next address in memory
                        this.addr[0] = this.fetch();
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case ABSOLUTE_X:

                    if (this.cycles == 3) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                    } else if (this.cycles == 2) {         // On first cycle read next address in memory
                        this.addr[0] = this.fetch();
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        if (!this.carry){
                            // Extra cycle required on carry
                            if (ByteArrayUtils.willCarryOnAddition(this.addr[1], this.inx)) {
                                this.carry = true;
                                this.cycles ++;
                            }
                            this.addr[1] = (byte)(this.addr[1] + this.inx);
                        } else {
                            this.carry = false;
                            this.addr[0] = (byte)(this.addr[0] + 0x01);
                        }

                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case ABSOLUTE_Y:
                    if (this.cycles == 3) {         // On first cycle read next address in memory
                        this.addr[1] = this.fetch();
                    } else if (this.cycles == 2) {         // On first cycle read next address in memory
                        this.addr[0] = this.fetch();
                    } else if (this.cycles == 1) {  // On second cycle add X to value on data bus
                        if (!this.carry){
                            // Extra cycle required on carry
                            if (ByteArrayUtils.willCarryOnAddition(this.addr[1], this.iny)) {
                                this.carry = true;
                                this.cycles ++;
                            }
                            this.addr[1] = (byte)(this.addr[1] + this.iny);
                        } else {
                            this.carry = false;
                            this.addr[0] = (byte)(this.addr[0] + 0x01);
                        }

                        addressBus.writeDataToBus(addr);
                    }
                    break;

                case INDIRECT:
                    if (this.cycles == 4) {
                        this.addr[1] = this.fetch();
                    } else if (this.cycles == 3) {
                        this.addr[0] = this.fetch();
                        addressBus.writeDataToBus(addr);
                    } else if (this.cycles == 2) {
                        rwFlag.setFlagValue(true);
                        this.addr[1] = dataBus.readDataFromBus()[0];
                    } else if (this.cycles == 1) {
                        addressBus.writeDataToBus(ByteArrayUtils.increment(addressBus.readDataFromBus()));
                        rwFlag.setFlagValue(true);
                        this.addr[0] = dataBus.readDataFromBus()[0];
                        addressBus.writeDataToBus(this.addr);
                    }

                case INDEXED_INDIRECT_X:
                    switch (this.cycles){
                        case 5:
                            this.addr[1] = this.fetch();
                            this.addr[1] = (byte)(this.addr[1] + this.inx);
                            break;
                        case 4:
                            this.addr[0] = 0x00;
                            this.addressBus.writeDataToBus(this.addr);
                            break;
                        case 3:
                            this.rwFlag.setFlagValue(true);
                            this.addr[1] = dataBus.readDataFromBus()[0];
                            break;
                        case 2:
                            byte[] next = addressBus.readDataFromBus();
                            next = ByteArrayUtils.increment(next);
                            this.addressBus.writeDataToBus(next);
                            break;
                        case 1:
                            this.rwFlag.setFlagValue(true);
                            this.addr[0] = dataBus.readDataFromBus()[0];
                            this.addressBus.writeDataToBus(this.addr);
                            break;
                    }
                    break;

                case INDIRECT_INDEXED_Y:
                    switch (this.cycles){
                        case 4:
                            this.addr[0] = 0x00;
                            this.addr[1] = this.fetch();
                            this.addressBus.writeDataToBus(this.addr);
                            break;
                        case 3:
                            this.rwFlag.setFlagValue(true);
                            this.addr[1] = dataBus.readDataFromBus()[0];
                            break;
                        case 2:
                            byte[] next = addressBus.readDataFromBus();
                            next = ByteArrayUtils.increment(next);
                            this.addressBus.writeDataToBus(next);
                            break;
                        case 1:
                            this.rwFlag.setFlagValue(true);
                            this.addr[0] = dataBus.readDataFromBus()[0];

                            if (!this.carry){
                                if (ByteArrayUtils.willCarryOnAddition(this.addr[1], this.iny)) {
                                    this.carry = true;
                                    this.cycles ++;
                                }
                                this.addr[1] = (byte)(this.addr[1] + this.iny);
                            }
                            else {
                                this.carry = false;
                                this.addr[0] = (byte)(this.addr[0] + 0x01);
                            }
                            addressBus.writeDataToBus(addr);

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
        this.status = (byte)(this.status | flag);
    }

    /**
     * Disables a given flag
     * @param flag flag to disable
     */
    private void disableFlag(byte flag) {
        if ((this.status & flag) == flag){
            this.status = (byte)(this.status - flag);
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

        if ((this.status & NMOS6502Flags.DECIMAL_MODE) == NMOS6502Flags.DECIMAL_MODE){
            // BCD addition
            //TODO BCD addition
        } else {
            // Binary addition
            result = (byte)(value + this.acc);
        }

        /**
         * Set Flags
         */
        // Carry Flag
        if (ByteArrayUtils.willCarryOnAddition(value, this.acc))
            enableFlag(NMOS6502Flags.CARRY_FLAG);

        // Zero Flag
        if (value == 0x00)
            enableFlag(NMOS6502Flags.ZERO_FLAG);

        // Overflow Flag
        if ((this.acc & 0b10000000) == 0b00000000 && (result & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.OVERFLOW_FLAG);

        // Negative Flag
        if ((value & 0b10000000) == 0b10000000)
            enableFlag(NMOS6502Flags.NEGATIVE_FLAG);

        // Finally set accumulator with new value
        this.acc = result;
        if (PRINT_TRACE)
            System.out.println("ADC : " + String.format("%02X", this.acc));
    }

    /**
     * Jumps the program counter to the value currently held on the address bus
     * @throws ProcessorException
     */
    private void JMP() throws ProcessorException {
        this.pc = this.addressBus.readDataFromBus();
        if (PRINT_TRACE)
            System.out.println("JMP : " + String.format("%02X", this.pc[0]) + String.format("%02X", this.pc[1]));
    }

    /**
     * Loads the value from memory into the accumulator
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

        this.acc = value;

        if (PRINT_TRACE)
            System.out.println("LDA : " + String.format("%02X", this.acc));
    }

    /**
     * Loads the value from memory into the x register
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

        this.inx = value;

        if (PRINT_TRACE)
            System.out.println("LDX : " + String.format("%02X", this.inx));
    }

    /**
     * Loads the value from memory into the y register
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

        this.iny = value;

        if (PRINT_TRACE)
            System.out.println("LDY : " + String.format("%02X", this.iny));
    }

}
