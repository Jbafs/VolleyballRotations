package com.volleyballrotations.backend;

import java.util.UUID;

/**
 * Base class that gives every domain object a stable, unique string identifier.
 */
public abstract class Identifiable {
    private final String id;

    /** Creates a new instance with a randomly generated UUID. */
    public Identifiable(){
        this(UUID.randomUUID().toString());
    }

    /** Creates an instance with the given pre-existing identifier (used when loading saved data). */
    public Identifiable(String id){
        this.id = id;
    }

    public String getId()           { return id; }
}
