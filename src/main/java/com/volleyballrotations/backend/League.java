package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class League {
    private final ObservableList<Team> teams = FXCollections.observableArrayList();
    public ObservableList<Team> getTeams() { return teams; }
}
