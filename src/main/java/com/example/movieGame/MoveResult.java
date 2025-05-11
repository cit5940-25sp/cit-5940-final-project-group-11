package com.example.movieGame;

import java.util.Collections;
import java.util.List;

public class MoveResult {

    private boolean isValid;
    private String errorMessage;
    private List<SingleConnection> connections;

    private List<SingleConnection> overusedConnections;



    private MoveResult(boolean isValid, String errorMessage, List<SingleConnection>connections, List<SingleConnection> overusedConnections) {
        this.connections = connections;
        this.errorMessage = errorMessage;
        this.isValid = isValid;
        this.overusedConnections = overusedConnections;
    }

    public static MoveResult success(List<SingleConnection> connections) {
        return new MoveResult(true, null, connections, Collections.emptyList());
    }

    public static MoveResult success(List<SingleConnection>connections, List<SingleConnection> overusedConnections) {
        return new MoveResult(true, null, connections,overusedConnections);
    }

    public static MoveResult failure(String errorMessage) {
        return new MoveResult(false, errorMessage, Collections.emptyList(), Collections.emptyList());
    }

    public static MoveResult failure(String errorMessage, List<SingleConnection> overusedConnections) {
        return new MoveResult(false, errorMessage, Collections.emptyList(), overusedConnections);
    }

    public boolean isValid() {
        return isValid;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public List<SingleConnection> getConnections() {
        return connections;
    }

    public List<SingleConnection> getOverusedConnections() {
        return overusedConnections;
    }
}
