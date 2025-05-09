package com.example.movieGame;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.testng.AssertJUnit.*;

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

        Movie mov = play.randomMovieSelection();
        System.out.println(mov.getActors());
        System.out.println(mov.getMovieID());
        System.out.println(mov.getMovieTitle());
        System.out.println(mov.getGenre());

        //System.out.println(play.randomMovieSelection());


    }

    @Test
    public void testFindingConnection() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Get the two Pirates movies
        Movie pirates1 = movieOutputs.get(18); // Pirates of the Caribbean: The Curse of the Black Pearl
        Movie pirates2 = movieOutputs.get(38); // Pirates of the Caribbean: Dead Man's Chest

        System.out.println("Movie 1: " + pirates1.getMovieTitle());
        System.out.println("Movie 1 actors: " + pirates1.getActors());
        System.out.println("Movie 1 directors: " + pirates1.getDirectors());

        System.out.println("\nMovie 2: " + pirates2.getMovieTitle());
        System.out.println("Movie 2 actors: " + pirates2.getActors());
        System.out.println("Movie 2 directors: " + pirates2.getDirectors());

        // Find shared actors (for debugging/information)
        HashSet<String> sharedActors = new HashSet<>(pirates1.getActors());
        sharedActors.retainAll(pirates2.getActors());
        System.out.println("\nShared actors: " + sharedActors);

        // Find shared directors
        HashSet<String> sharedDirectors = new HashSet<>(pirates1.getDirectors());
        sharedDirectors.retainAll(pirates2.getDirectors());
        System.out.println("Shared directors: " + sharedDirectors);

        // Create a custom GamePlay for testing
        GamePlay play = new GamePlay("P1", "P2");

        // We need to set pirates1 as the first/previous movie
        // If there's no direct setter, we can create a method for testing
        // For now, let's try a workaround by adding it to lastFiveMovies

        // Clear any existing movies in the queue
        while (!play.lastFiveMovies.isEmpty()) {
            play.lastFiveMovies.poll();
        }

        // Add pirates1 as the "last" movie
        play.lastFiveMovies.add(pirates1);

        play.setActorUsage("David Bailie", 3);


        // If we need to add it to moviesUsed
        play.moviesUsed.add(pirates1.getMovieID());

        // Now validate pirates2 against pirates1
        MoveResult result = play.validateMove(pirates2);

        System.out.println("\nMove validation result: " + (result.isValid() ? "Valid" : "Invalid"));

        if (result.isValid()) {
            System.out.println("Valid connections found:");
            for (SingleConnection connection : result.getConnections()) {
                System.out.println("  Type: " + connection.getConnectionType() + ", Name: " + connection.getName());
            }

            // Assert that we found a valid connection
            assertTrue("Expected to find a valid connection", result.isValid());
        } else {
            System.out.println("Error message: " + result.getErrorMessage());

            // If validation failed unexpectedly, print more debug info
            if (!sharedActors.isEmpty() || !sharedDirectors.isEmpty()) {
                fail("Validation failed despite shared cast/crew");
            }
        }

    }
}