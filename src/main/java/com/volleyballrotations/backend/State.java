package com.volleyballrotations.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A named snapshot of all six player positions on the court for a given rotation.
 *
 * <p>Coordinate system: both x and y are 0–1, representing one half of the court (1:1 ratio).
 * x = sideline-to-sideline (0=left, 1=right from the team's perspective)
 * y = depth (0=near net, 1=back line)
 */
public class State extends Identifiable{
    private final StringProperty name   = new SimpleStringProperty("");
    private final List<Position> positions = new ArrayList<>(6);

    /** Default (x, y) court coordinates for each of the six rotation slots. */
    private static final double[][] defaults = {{5.0/6,1.0/3},{5.0/6,5.0/6},{.5, 5.0/6},
            {1.0/6,5.0/6},{1.0/6,1.0/3},{.5,1.0/3}};

    /**
     * Builds a default state by rotating the player array so that player at index {@code rot}
     * becomes position 1, then places each player at the standard default court coordinates.
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

    /** Creates a state from a known ID, name, and pre-built positions (used when loading saved data). */
    public State(String id, String name, Position... pos){
        super(id);
        this.name.set(name);
        positions.addAll(List.of(pos));
    }

    /**Ui binds */
    public StringProperty nameProperty(){ return name; }

    /** Returns a deep copy of this state with a new ID and " Copy" appended to the name. */
    public State copy() {
        return new State(UUID.randomUUID().toString(), getName() + " Copy",
            positions.stream().map(Position::copy).toArray(Position[]::new));
    }

    /**Getters and setters */
    public List<Position> positionList(){ return positions; }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
}
