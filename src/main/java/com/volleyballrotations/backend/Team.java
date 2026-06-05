package com.volleyballrotations.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Team extends Identifiable{
    private final Roster roster;
    private final ObservableList<Lineup> lineups = FXCollections.observableArrayList();
    private final StringProperty name   = new SimpleStringProperty("");

    public Team(String id, String name, Roster roster, Lineup... lineups){
        super(id);
        this.roster = roster;
        this.name.set(name);
        this.lineups.addAll(lineups);
    }

    public Team(String name){
        this.name.set(name);
        roster = new Roster();
    }

    /**Ui binds */
    public StringProperty nameProperty(){ return name; }

    public Roster getRoster()        { return roster; }
    public ObservableList<Lineup> lineupList(){ return lineups; }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
    @Override public String toString() { return getName(); }
}
