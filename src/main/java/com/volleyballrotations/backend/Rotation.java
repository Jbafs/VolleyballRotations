package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * One of the six serve-receive rotations, holding one or more named court states (presets).
 */
public class Rotation {
    /** Zero-based index (0–5) identifying which rotation this represents. */
    private int rotation;
    private final ObservableList<State> states = FXCollections.observableArrayList();

    /** Creates a rotation with a single default court state built from the given player order. */
    public Rotation(int r, Player... ps) {
        rotation = r;
        states.add(new State(r,ps));
    }

    /** Creates a rotation from pre-built states (used when loading saved data). */
    public Rotation(int r, State... s){
        rotation = r;
        states.addAll(s);
    }

    public int getRotation()               { return rotation; }
    public ObservableList<State> stateList(){ return states; }

}
