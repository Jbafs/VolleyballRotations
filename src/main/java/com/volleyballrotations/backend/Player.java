package com.volleyballrotations.backend;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player extends Identifiable{
    private final IntegerProperty number = new SimpleIntegerProperty();
    private final StringProperty name   = new SimpleStringProperty("");
    private final StringProperty defaultPos   = new SimpleStringProperty("");

    // new player → generate an id
    public Player(String n, int num, String def) {
        name.set(n);
        number.set(num);
        defaultPos.set(def);
    }

    // loaded player → keep the saved id
    public Player(String id, String n, int num, String def) {
        super(id);
        name.set(n);
        number.set(num);
        defaultPos.set(def);
    }

    public int    getNumber()       { return number.get(); }
    public void   setNumber(int v)  { number.set(v); }
    public String getName()         { return name.get(); }
    public void   setName(String v) { name.set(v); }
    public String getPos()         { return defaultPos.get(); }
    public void   setPos(String v) { defaultPos.set(v); }
}