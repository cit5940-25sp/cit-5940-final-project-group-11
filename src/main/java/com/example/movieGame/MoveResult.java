package com.example.movieGame;

import java.util.Collections;
import java.util.List;

public class MoveResult {

    private boolean isValid;
    private String errorMessage;
    private List<SingleConnection> connections;



    private MoveResult(boolean isValid, String errorMessage, List<SingleConnection>connections) {
        this.connections = connections;
        this.errorMessage = errorMessage;
        this.isValid = isValid;
    }

    public static MoveResult success(List<SingleConnection> connections) {
        return new MoveResult(true, null, connections);
    }

    public static MoveResult failure(String errorMessage) {
        return new MoveResult(false, errorMessage, Collections.emptyList());
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



}
