package com.rosscon.llce.components.graphics.NES2C02;

/**
 * A loopy register inspired by https://wiki.nesdev.com/w/index.php/PPU_scrolling
 *
 *  yyy NN YYYYY XXXXX
 *  ||| || ||||| +++++-- coarse x scroll
 *  ||| || +++++-------- coarse y scroll
 *  ||| ++-------------- nametable select (yx)
 *  +++----------------- fine y scroll
 */
public class LoopyRegister {

    /**
     * Values Held
     */
    private int coarseX;
    private int coarseY;
    private int nametableX;
    private int nametableY;
    private int fineY;

    /**
     * Masks
     */
    private final int COARSE_X_MASK     = 0b0000000000011111;
    private final int COARSE_Y_MASK     = 0b0000000000011111;
    private final int NAMETABLE_X_MASK  = 0b0000000000000001;
    private final int NAMETABLE_Y_MASK  = 0b0000000000000001;
    private final int FINE_Y_MASK       = 0b0000000000000111;


    public LoopyRegister(){
        this.coarseX = 0x00;
        this.coarseY = 0x00;
        this.nametableX = 0x00;
        this.nametableY = 0x00;
        this.fineY = 0x00;
    }

    public LoopyRegister(int value){
        this.coarseX = value & COARSE_X_MASK;
        this.coarseY = (value >>> 5) & COARSE_Y_MASK;
        this.nametableX = (value >>> 10) & NAMETABLE_X_MASK;
        this.nametableY = (value >>> 11) & NAMETABLE_Y_MASK;
        this.fineY = (value >>> 12) & FINE_Y_MASK;
    }

    public void setCoarseX(int coarseX) {
        this.coarseX = coarseX & COARSE_X_MASK;
    }

    public void setCoarseY(int coarseY) {
        this.coarseY = coarseY & COARSE_Y_MASK;
    }

    public void setNametableX(int nametableX) {
        this.nametableX = nametableX & NAMETABLE_X_MASK;
    }

    public void setNametableY(int nametableY) {
        this.nametableY = nametableY & NAMETABLE_Y_MASK;
    }

    public void setFineY(int fineY) {
        this.fineY = fineY & FINE_Y_MASK;
    }

    public int getCoarseX() {
        return coarseX;
    }

    public int getCoarseY() {
        return coarseY;
    }

    public int getNametableX() {
        return nametableX;
    }

    public int getNametableY() {
        return nametableY;
    }

    public int getFineY() {
        return fineY;
    }

    /**
     * Reconstruct the value of the register
     * @return value held in the register
     */
    public int getValue (){
        int value = 0x00;
        value += (coarseX & COARSE_X_MASK);
        value += (coarseY & COARSE_Y_MASK) << 5;
        value += (nametableX & NAMETABLE_X_MASK) << 10;
        value += (nametableY & NAMETABLE_Y_MASK) << 11;
        value += (fineY & FINE_Y_MASK) << 12;
        return value;
    }

}
