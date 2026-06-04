package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Rotation {
    private final ObservableList<State> states = FXCollections.observableArrayList();

    public Rotation(int r, Player... ps) {
        states.add(new State(r,ps));
    }

    public Rotation(State... s){
        states.addAll(s);
    }

    public ObservableList<State> stateList(){ return states; }
}
