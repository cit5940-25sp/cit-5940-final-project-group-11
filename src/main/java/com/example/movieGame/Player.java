package com.example.movieGame;

/**
 * Player object to track Player 1 and Player 2 active status, name, and progress towards win
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
     * Return player's username
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
     * Set active player status
     * @param isActive whether they're the active player
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    /**
     * Set inactive player status
     * @param isInactive whether they're the inactive player
     */
    public void setIsInactive(boolean isInactive) {
        this.isInactive = isInactive;
    }

    /**
     * Set progress toward win
     * Increments progress toward win when player submits a successful entry
     */
    public void setProgressTowardWin() {
        //increment progress toward win when player submits a successful entry
        this.progressTowardWin = this.progressTowardWin + 1;
    }
}