package com.example.movieGame;

public class GenreWinCondition implements WinConditionStrategy{

    private final String setGenre; //Chosen genre winning is based on
    private final int count; //Count required for a win

    public GenreWinCondition(String setGenre, int count) {
        this.setGenre = setGenre;
        this.count = count;
    }

    @Override
    public boolean isSatisfied(Movie movie, Player player) {

        //Check if the current movie is of the genre required
        //If so, increment the existing player's win score by 1
        if (movie.getGenre().contains(setGenre)) {
            player.setProgressTowardWin(); // increment counter
        }

        //Return true if the total win score exceeds the set count
        //False otherwise
        return player.getProgressTowardWin() >= count;
    }
}
