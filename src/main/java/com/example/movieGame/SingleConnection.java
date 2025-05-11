package com.example.movieGame;

/**
 * tracks connections between movies (ex: Actor - Leo DiCaprio; Genre - horror, etc.)
 *
 */
public class SingleConnection {
    private final String connectionType; //ex: genre, actor, composer, etc.
    private final String name;    //ex: Leonardo DiCaprio

    private boolean overused;

    public SingleConnection(String connectionType, String name) {
        this.connectionType = connectionType;
        this.name = name;
        this.overused = false;
    }

    public SingleConnection(String connectionType, String name, Boolean overused) {
        this.connectionType = connectionType;
        this.name = name;
        this.overused = overused;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getName() {
        return name;
    }

    public void setOverused(boolean overused) {
        this.overused = overused;
    }

    @Override
    public String toString() {
        return connectionType + ": " + name + (overused ? ": overused " : "");
    }
}
