package com.example.movieGame;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class MovieLoaderTest {


    // TODO - Properly test the remaining arrays
    @Test
    public void testMovieCreator() throws IOException {

        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        String actualTitle = movieOutputs.get(0).getMovieTitle();
        int actualID = movieOutputs.get(0).getMovieID();
        HashSet<String> actualGenre = movieOutputs.get(0).getGenre();
        Long actualYear = movieOutputs.get(0).getReleaseYear();
        HashSet<String> actualActors = movieOutputs.get(0).getActors();
        HashSet<String> actualDirectors = movieOutputs.get(0).getDirectors();
        HashSet<String> actualCinema = movieOutputs.get(0).getCinematographers();
        HashSet<String> actualComposer = movieOutputs.get(0).getComposers();
        HashSet<String> actualWriter = movieOutputs.get(0).getWriters();

        assertEquals("Napoleon Dynamite", actualTitle);
        assertEquals(8193, actualID);
        assertTrue(actualActors.contains("Jon Heder"));

        GamePlay play = new GamePlay("P1", "P2");

        System.out.println(play.randomMovieSelection());


    }

}
