package com.example.movieGame;

public class GenreWinStrategy implements WinStrategy {

    //Keeps track of the genre entered by the player at the start
    private final String targetGenre;

    //Constructor to create the preferred win strategy
    public GenreWinStrategy(String targetGenre) {
        this.targetGenre = targetGenre;
    }

    @Override
    public boolean checkWin(Player player, Movie movie) {

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
