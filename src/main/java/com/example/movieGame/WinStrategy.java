package com.example.movieGame;

public interface WinStrategy {

    //Boolean returns T if the player wins the game
    //I.e. able to name 5 movies with the
    boolean checkWin(Player player, Movie movie);

}
