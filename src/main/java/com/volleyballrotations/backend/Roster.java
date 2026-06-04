package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Roster {
    private final ObservableList<Player> players = FXCollections.observableArrayList();

    public Roster(){

    }

    public Roster(Player... ps){
        players.addAll(ps);
    }

    public ObservableList<Player> playerList(){ return players; }
}
