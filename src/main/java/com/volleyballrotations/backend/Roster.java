package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Holds the observable list of players belonging to a single team.
 */
public class Roster {
    private final ObservableList<Player> players = FXCollections.observableArrayList();

    public Roster(){

    }

    /** Creates a roster pre-populated with the given players. */
    public Roster(Player... ps){
        players.addAll(ps);
    }

    public ObservableList<Player> playerList(){ return players; }

    /** Creates a new Player from the given details and appends it to this roster. */
    public void addPlayer(String n, int num, String def){
        players.add(new Player(n,num,def));
    }
}
