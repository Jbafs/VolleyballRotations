package com.volleyballrotations.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class State extends Identifiable{
    private final StringProperty name   = new SimpleStringProperty("");
    private final List<Position> positions = new ArrayList<>(6);
    private static final double[][] defaults = {{3.0/2,1.0/3},{4.0/3,5.0/6},{.5, 5.0/6},
            {.5,5.0/6},{.5,1.0/3},{.5,1.0/3}};

    /**
     * Default rotation
     * @param rot - current rotation
     * @param players - players in starting order
     */
    public State(int rot, Player[] players){
        setName("Default");
        Player[] curRot = new Player[6];
        int ind = 0;
        for(int i = rot; i < 6; i++) curRot[ind++] = players[i];
        for(int i = 0; i<rot; i++) curRot[ind++] = players[i];

        for(int i = 0; i < rot; i++)
            positions.add(new Position(defaults[i][0], defaults[i][1], curRot[i], curRot[i].getPos()));
    }

    public List<Position> positionList(){ return positions; }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
}
