package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Rotation {
    private int rotation;
    private final ObservableList<State> states = FXCollections.observableArrayList();

    public Rotation(int r, Player... ps) {
        rotation = r;
        states.add(new State(r,ps));
    }

    public Rotation(int r, State... s){
        rotation = r;
        states.addAll(s);
    }

    public int getRotation()               { return rotation; }
    public ObservableList<State> stateList(){ return states; }

}
