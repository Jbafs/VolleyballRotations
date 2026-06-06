package com.volleyballrotations.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a volleyball team, owning a roster of players and one or more lineups.
 */
public class Team extends Identifiable{
    private final Roster roster;
    private final ObservableList<Lineup> lineups = FXCollections.observableArrayList();
    private final StringProperty name  = new SimpleStringProperty("");
    /** Hex color string used to render this team's player tokens on the court. */
    private final StringProperty color = new SimpleStringProperty("#4488ff");

    /** Full constructor used when loading a saved team with a known ID and existing lineups. */
    public Team(String id, String name, String color, Roster roster, Lineup... lineups){
        super(id);
        this.roster = roster;
        this.name.set(name);
        this.color.set(color);
        this.lineups.addAll(lineups);
    }

    /** Convenience constructor for creating a brand-new team with an empty roster. */
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
