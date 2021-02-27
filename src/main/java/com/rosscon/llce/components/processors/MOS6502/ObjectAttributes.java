package com.rosscon.llce.components.processors.MOS6502;

/**
 * Encapsulate the details for a sprite
 */
public class ObjectAttributes {

    private int xPos;
    private int yPos;
    private int id;
    private int attribute;

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getId() {
        return id;
    }

    public int getAttribute() {
        return attribute;
    }

    public ObjectAttributes (int xPos, int yPos, int id, int attribute){
        this.xPos = xPos;
        this.yPos = yPos;
        this.id = id;
        this.attribute = attribute;
    }

}
