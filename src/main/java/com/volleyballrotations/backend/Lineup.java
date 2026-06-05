package com.volleyballrotations.backend;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Lineup extends Identifiable{
    private final StringProperty name = new SimpleStringProperty("Lineup");
    private final List<Rotation> rotations = new ArrayList<>(6);

    public Lineup(Player... players){
        for (int i = 0; i < 6; i++) rotations.add(new Rotation(i,players));
    }

    public Lineup(String id, Rotation... rs){
        super(id);
        if (rs.length != 6) throw new IllegalArgumentException("expected 6 rotations, got " + rs.length);
        rotations.addAll(List.of(rs));
    }

    public StringProperty nameProperty() { return name; }
    public String getName()              { return name.get(); }
    public void   setName(String v)      { name.set(v); }
    public List<Rotation> rotationList() { return rotations; }
    @Override public String toString()   { return getName(); }
}
