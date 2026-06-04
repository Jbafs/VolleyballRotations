package com.volleyballrotations.backend;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Pure data for one player marker on the court. (Modded by claude to cut the fat) (modded further by me to use Player)
 */
public class Position {
    private final DoubleProperty xPos = new SimpleDoubleProperty();
    private final DoubleProperty yPos = new SimpleDoubleProperty();
    private Player player;
    private final StringProperty positionName = new SimpleStringProperty("");

    public Position() {}

    public Position(double x, double y, Player pl, String p) {
        xPos.set(x);
        yPos.set(y);
        player = pl;
        positionName.set(p);
    }

    // --- Property accessors: the UI binds to THESE ---
    public DoubleProperty xPosProperty()        { return xPos; }
    public DoubleProperty yPosProperty()        { return yPos; }
    public Player getPlayer() { return player; }
    public StringProperty positionNameProperty(){ return positionName; }

    // --- Plain getters / setters for everyday use ---
    public double getXPos()              { return xPos.get(); }
    public void   setXPos(double v)      { xPos.set(v); }
    public double getYPos()              { return yPos.get(); }
    public void   setYPos(double v)      { yPos.set(v); }
    public int    getNumber()            { return player.getNumber(); }
    public void   setNumber(int v)       { player.setNumber(v); }
    public String getName()              { return player.getName(); }
    public void   setName(String v)      { player.setName(v); }
    public String getPositionName()      { return positionName.get(); }
    public void   setPositionName(String v) { positionName.set(v); }

    /** Deep copy of this position (used when duplicating a rotation/preset). */
    public Position copy() {
        return new Position(getXPos(), getYPos(), getPlayer(), getPositionName());
    }

    @Override
    public String toString() {
        return "Position[#" + getNumber() + " " + getName()
                + " (" + getPositionName() + ") @ "
                + getXPos() + "," + getYPos() + "]";
    }
}