package com.example.movieGame;

/**
 * main.java.com.example.movieGame.Player object to track main.java.com.example.movieGame.Player 1 and main.java.com.example.movieGame.Player 2
 *
 */
public class Player {

    private String userName;
    private int progressTowardWin;
    private boolean isActive;

    /**
     * Constructor
     */
    public Player(String name, boolean activePlayer) {
        this.userName = name; //set player's name
        this.progressTowardWin = 0; //TODO - does having this as an integer work? given the way WinCondition is set up?
        this.isActive = activePlayer;
    }

    /**
     * Return main.java.com.example.movieGame.Player's username
     *
     * @return player's username
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Return progress toward win.
     * Counts number of
     *
     * @return progress toward win
     */
    public int getProgressTowardWin() {
        //TODO: note - this might not be necessary given how the winCondition classes are set up...
        // (possibly remove)
        return this.progressTowardWin;
    }

    public void incrementProgressTowardWin() {
        this.progressTowardWin++;
    }

    /**
     * Return whether the player is active
     *
     * @return 1 if player is active, 0 if not
     */
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     * set active player status
     * @Parameter whether they're the active player
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    /**
     * set progress toward win
     */
    public void setProgressTowardWin() {
        //increment progress toward win when player submits a successful entry
        this.progressTowardWin = this.progressTowardWin + 1;
    }
}