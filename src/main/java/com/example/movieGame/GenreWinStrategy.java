package com.example.movieGame;

/**
 * Implements the WinStrategy interface
 * Checks whether a player has won based on their selected win condition (for genre)
 * A player wins if they name 5 movies of a particular genre
 *
 */
public class GenreWinStrategy implements WinStrategy {

    //Keeps track of the genre entered by the player at the start
    private final String targetGenre;

    /**
     * Constructor to create the preferred win strategy
     *
     * @param targetGenre user selected genre (horror, comedy, etc.)
     *
     */
    public GenreWinStrategy(String targetGenre) {
        this.targetGenre = targetGenre;
    }

    /**
     * Checks to see if a player has won the game
     * Boolean returns T if the player wins the game
     * I.e. able to name 5 movies with the win genre
     *
     * @param player the player object
     * @param movie the movie object
     */
    @Override
    public boolean checkWin(Player player, Movie movie) {

        if (movie == null || movie.getGenre() == null) {
            return false;
        }

        //Checks if the movie is the correct genre
        //Note: This is called only after the linkages is checked
        //If yes, the progress towards win counter is incremented by one
        if (movie.getGenre() != null && movie.getGenre().contains(targetGenre)) {
            player.setProgressTowardWin();
        }

        //Returns true with the player wins the game
        //I.e. they have more than 5 points
        return player.getProgressTowardWin() >= 5;
    }
}
