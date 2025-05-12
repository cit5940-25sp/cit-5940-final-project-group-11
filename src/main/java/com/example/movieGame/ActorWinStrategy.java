package com.example.movieGame;

import java.util.Set;

/**
 * Implements the WinStrategy interface
 * Checks whether a player has won based on their selected win condition (for actor)
 * A player wins if they name 5 movies with a specified actor in it
 * The actor themselves can't be used as an explicit connection more than 3 times.
 * (For example, Tom Hanks can be used as a connection 3 times, and then for the remaining 2 he has to be in the movie,
 * but another connection (writer, director, another ector, etc.) must be used instead
 *
 */
public class ActorWinStrategy implements WinStrategy {

    private final String targetActor;

    /**
     * Constructor to create the preferred win strategy and select the actor
     *
     * @param targetActor user selected actor
     *
     */
    public ActorWinStrategy(String targetActor) {
        this.targetActor = targetActor;
    }

    /**
     * Checks to see if a player has won the game
     * Boolean returns T if the player wins the game
     * I.e. able to name 5 movies with the chosen actor
     *
     * @param player the player object
     * @param movie the movie object
     */
    @Override
    public boolean checkWin(Player player, Movie movie) {

        if (movie == null || movie.getGenre() == null) {
            return false;
        }

        Set<String> actors = movie.getActors();
        if (actors != null && actors.contains(targetActor)) {
            player.setProgressTowardWin();
        }

        return player.getProgressTowardWin() >= 5;
    }

}
