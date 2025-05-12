package com.example.movieGame;

/**
 * Tracks connections between movies (ex: Actor - Leo DiCaprio; Genre - horror, etc.)
 *
 */
public class SingleConnection {
    private final String connectionType; //ex: genre, actor, composer, etc.
    private final String name;    //ex: Leonardo DiCaprio

    private boolean overused;

    /**
     * Constructor to initialize the connection type and name for valid connections
     * @param connectionType type of connection (ex: genre, actor, composer, etc.)
     * @param name name of the connection (ex: Leonardo DiCaprio)
     */
    public SingleConnection(String connectionType, String name) {
        this.connectionType = connectionType;
        this.name = name;
        this.overused = false;
    }

    /**
     * Constructor to initialize the connection type and name for overused connections
     * @param connectionType type of connection (ex: genre, actor, composer, etc.)
     * @param name name of the connection (ex: Leonardo DiCaprio)
     * @param overused true/false to indicate if the connection has been overused
     */
    public SingleConnection(String connectionType, String name, Boolean overused) {
        this.connectionType = connectionType;
        this.name = name;
        this.overused = overused;
    }

    /**
     * Getter for connection Type
     *
     * @return connection type
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * Getter for connection name
     *
     * @return connection name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for overused
     *
     * @param overused indicated true/false for whether the connection is overused
     */
    public void setOverused(boolean overused) {
        this.overused = overused;
    }

    /**
     * Converts connection type to string
     *
     * @return string of full connection type information
     */
    @Override
    public String toString() {
        return connectionType + ": " + name + (overused ? ": overused " : "");
    }
}
