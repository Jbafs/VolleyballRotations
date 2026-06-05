package com.volleyballrotations.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Team extends Identifiable{
    private final Roster roster;
    private final ObservableList<Lineup> lineups = FXCollections.observableArrayList();
    private final StringProperty name  = new SimpleStringProperty("");
    private final StringProperty color = new SimpleStringProperty("#4488ff");

    public Team(String id, String name, String color, Roster roster, Lineup... lineups){
        super(id);
        this.roster = roster;
        this.name.set(name);
        this.color.set(color);
        this.lineups.addAll(lineups);
    }

    public Team(String name){
        this.name.set(name);
        roster = new Roster();
    }

    /**Ui binds */
    public StringProperty nameProperty()  { return name; }
    public StringProperty colorProperty() { return color; }

    public Roster getRoster()        { return roster; }
    public ObservableList<Lineup> lineupList(){ return lineups; }
    public String getName()          { return name.get(); }
    public void   setName(String v)  { name.set(v); }
    public String getColor()         { return color.get(); }
    public void   setColor(String v) { color.set(v); }
    @Override public String toString() { return getName(); }
}
