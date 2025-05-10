package com.example.movieGame;

public interface WinConditionStrategy {

    /**
     * Returns true or false depending on if the given movie and player satisfy the win conditions
     * @param movie the movie entered
     * @param player the player who just entered the movie
     */
    boolean isSatisfied(Movie movie, Player player);

}
