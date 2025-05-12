package com.example.movieGame;

import java.util.Collections;
import java.util.List;

/**
 * Tracks whether the move input by the user is valid and the error message for why if invalid
 *
 */
public class MoveResult {

    private boolean isValid;
    private String errorMessage;
    private List<SingleConnection> connections;
    private List<SingleConnection> overusedConnections;

    /**
     * Constructor
     *
     * @param isValid true if valid, false if invalid
     * @param connections list of connections
     * @param overusedConnections list of connections used too many times
     *
     */
    private MoveResult(boolean isValid, String errorMessage, List<SingleConnection>connections, List<SingleConnection> overusedConnections) {
        this.connections = connections;
        this.errorMessage = errorMessage;
        this.isValid = isValid;
        this.overusedConnections = overusedConnections;
    }

    /**
     * Returns the MoveResult for the connections being validated/checked
     *
     * @param connections list of connections being checked
     * @return returns the MoveResult object tracking whether the move is valid
     *
     */
    public static MoveResult success(List<SingleConnection> connections) {
        return new MoveResult(true, null, connections, Collections.emptyList());
    }

    /**
     * Returns the MoveResult for the connections being validated/checked
     *
     * @param connections list of connections
     * @param overusedConnections list of connections used too many times
     * @return returns the MoveResult object tracking whether the move is valid
     *
     */
    public static MoveResult success(List<SingleConnection>connections, List<SingleConnection> overusedConnections) {
        return new MoveResult(true, null, connections,overusedConnections);
    }

    /**
     * Returns the MoveResult for the connections being validated/checked
     *
     * @param errorMessage issue with the connection
     * @return returns the MoveResult object tracking whether the move is valid
     *
     */
    public static MoveResult failure(String errorMessage) {
        return new MoveResult(false, errorMessage, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Returns the MoveResult for the connections being validated/checked
     *
     * @param errorMessage issue with the connection
     * @param overusedConnections list of connections used too many times
     * @return returns the MoveResult object tracking whether the move is valid
     *
     */
    public static MoveResult failure(String errorMessage, List<SingleConnection> overusedConnections) {
        return new MoveResult(false, errorMessage, Collections.emptyList(), overusedConnections);
    }

    /**
     * Getter for whether the connection is valid
     *
     * @return returns true if valid and false if not
     *
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Getter for error message
     *
     * @return error message
     *
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Getter for connections
     *
     * @return returns a list of connections
     *
     */
    public List<SingleConnection> getConnections() {
        return connections;
    }

    /**
     * Getter for overused connections
     *
     * @return returns the list of connections that have been used too many times
     *
     */
    public List<SingleConnection> getOverusedConnections() {
        return overusedConnections;
    }
}
