package com.volleyballrotations.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Team extends Identifiable{
    private final ObservableList<Lineup> lineups = FXCollections.observableArrayList();
    private final StringProperty name   = new SimpleStringProperty("");

    public Team(String id, String name, Lineup... lineups){
        super(id);
        setName(name);
        this.lineups.addAll(lineups);
    }

    public ObservableList<Lineup> lineupList(){ return lineups; }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
}
