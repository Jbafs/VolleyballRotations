package com.volleyballrotations.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Coordinate system works on the x axis being 0-2 and the y axis being 0-1
 * (as the 1:1 ratio of one half of the volleyball court)
 */
public class State extends Identifiable{
    private final StringProperty name   = new SimpleStringProperty("");
    private final List<Position> positions = new ArrayList<>(6);

    private static final double[][] defaults = {{5.0/6,1.0/3},{5.0/6,5.0/6},{.5, 5.0/6},
            {1.0/6,5.0/6},{1.0/6,1.0/3},{.5,1.0/3}};

    /**
     * Default rotation
     * @param rot - current rotation
     * @param players - players in starting order
     */
    public State(int rot, Player[] players){
        name.set("Default");
        Player[] curRot = new Player[6];
        int ind = 0;
        for(int i = rot; i < 6; i++) curRot[ind++] = players[i];
        for(int i = 0; i<rot; i++) curRot[ind++] = players[i];

        for(int i = 0; i < 6; i++)
            positions.add(new Position(defaults[i][0], defaults[i][1], curRot[i], curRot[i].getPos()));
    }

    public State(String id, String name, Position... pos){
        super(id);
        this.name.set(name);
        positions.addAll(List.of(pos));
    }

    /**Ui binds */
    public StringProperty nameProperty(){ return name; }

    public State copy() {
        return new State(UUID.randomUUID().toString(), getName() + " Copy",
            positions.stream().map(Position::copy).toArray(Position[]::new));
    }

    /**Getters and setters */
    public List<Position> positionList(){ return positions; }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
}
