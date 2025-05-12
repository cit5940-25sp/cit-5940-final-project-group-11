package com.example.movieGame;

/**
 * Interface to implement the Strategy design pattern
 * This interface allows various win conditions to be applied/utilized
 *
 */
public interface WinStrategy {

    /**
     * Checks to see if a player has won the game
     * Boolean returns T if the player wins the game
     * I.e. able to name 5 movies with the
     *
     * @param player the player object
     * @param movie the movie object
     */
    boolean checkWin(Player player, Movie movie);

}
