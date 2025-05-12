package com.example.movieGame;

/**
 * main.java.com.example.movieGame.Player object to track main.java.com.example.movieGame.Player 1 and main.java.com.example.movieGame.Player 2
 *
 */
public class Player {

    private String userName;
    private int progressTowardWin;
    private boolean isActive;
    private boolean isInactive;

    /**
     * Constructor
     */
    public Player(String name, boolean activePlayer, boolean inactivePlayer) {
        this.userName = name; //set player's name
        this.progressTowardWin = 0;
        this.isActive = activePlayer;
        this.isInactive = inactivePlayer;
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
        return this.progressTowardWin;
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
     * Return whether the player is inactive
     *
     * @return 1 if player is inactive, 0 if not
     */
    public boolean getIsInactive() {
        return this.isInactive;
    }

    /**
     * set active player status
     * @Parameter whether they're the active player
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    /**
     * set active player status
     * @Parameter whether they're the active player
     */
    public void setIsInactive(boolean isInactive) {
        this.isInactive = isInactive;
    }

    /**
     * set progress toward win
     */
    public void setProgressTowardWin() {
        //increment progress toward win when player submits a successful entry
        this.progressTowardWin = this.progressTowardWin + 1;
    }

    public void setProgressInt(int i) {
        this.progressTowardWin = i;
    }
}