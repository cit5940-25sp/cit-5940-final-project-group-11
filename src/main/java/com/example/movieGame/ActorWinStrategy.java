package com.example.movieGame;

import java.util.Set;

public class ActorWinStrategy implements WinStrategy {

    private final String targetActor;

    public ActorWinStrategy(String targetActor) {
        this.targetActor = targetActor;
    }

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
