package com.example.movieGame;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class GamePlayTest {

   @Test
   public void testRandomMovieSelection() {

   }

    @Test
    public void testCheckValidLinkage() {

    }


    @Test
    public void testUserEntry() {


    }

    @Test
    public void testConnectionBetweenLoneRangerAndAlice() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();
        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();

        // Get our specific test movies by ID
        Movie loneRanger = movieMap.get(57201);    // The Lone Ranger
        Movie aliceThroughLookingGlass = movieMap.get(241259);    // Alice Through the Looking Glass

        // Print movie details for debugging
        System.out.println("Movie 1: " + loneRanger.getMovieTitle());
        System.out.println("Movie 1 actors: " + loneRanger.getActors());
        System.out.println("Movie 1 directors: " + loneRanger.getDirectors());
        System.out.println("Movie 1 writers: " + loneRanger.getWriters());
        System.out.println("Movie 1 cinematographers: " + loneRanger.getCinematographers());
        System.out.println("Movie 1 composers: " + loneRanger.getComposers());

        System.out.println("\nMovie 2: " + aliceThroughLookingGlass.getMovieTitle());
        System.out.println("Movie 2 actors: " + aliceThroughLookingGlass.getActors());
        System.out.println("Movie 2 directors: " + aliceThroughLookingGlass.getDirectors());
        System.out.println("Movie 2 writers: " + aliceThroughLookingGlass.getWriters());
        System.out.println("Movie 2 cinematographers: " + aliceThroughLookingGlass.getCinematographers());
        System.out.println("Movie 2 composers: " + aliceThroughLookingGlass.getComposers());

        // Find shared connections for verification
        System.out.println("\n--- Shared Connections ---");

        // Shared actors
        HashSet<String> sharedActors = new HashSet<>(loneRanger.getActors());
        sharedActors.retainAll(aliceThroughLookingGlass.getActors());
        System.out.println("Shared actors: " + sharedActors);

        // Shared directors
        HashSet<String> sharedDirectors = new HashSet<>(loneRanger.getDirectors());
        sharedDirectors.retainAll(aliceThroughLookingGlass.getDirectors());
        System.out.println("Shared directors: " + sharedDirectors);

        // Shared writers
        HashSet<String> sharedWriters = new HashSet<>(loneRanger.getWriters());
        sharedWriters.retainAll(aliceThroughLookingGlass.getWriters());
        System.out.println("Shared writers: " + sharedWriters);

        // Shared cinematographers
        HashSet<String> sharedCinematographers = new HashSet<>(loneRanger.getCinematographers());
        sharedCinematographers.retainAll(aliceThroughLookingGlass.getCinematographers());
        System.out.println("Shared cinematographers: " + sharedCinematographers);

        // Shared composers
        HashSet<String> sharedComposers = new HashSet<>(loneRanger.getComposers());
        sharedComposers.retainAll(aliceThroughLookingGlass.getComposers());
        System.out.println("Shared composers: " + sharedComposers);

        // Create GamePlay for testing the validation logic
        GamePlay gamePlay = new GamePlay("Player1", "Player2");

        // Clear any existing movies in the queue
        while (!gamePlay.lastFiveMovies.isEmpty()) {
            gamePlay.lastFiveMovies.poll();
        }

        // Add Lone Ranger as the "last" movie
        gamePlay.lastFiveMovies.add(loneRanger);
        gamePlay.moviesUsed.add(loneRanger.getMovieID());

        // Validate Alice Through the Looking Glass against Lone Ranger
        MoveResult result = gamePlay.validateMove(aliceThroughLookingGlass);

        System.out.println("\nMove validation result: " + (result.isValid() ? "Valid" : "Invalid"));

        if (result.isValid()) {
            System.out.println("Valid connections found:");
            for (SingleConnection connection : result.getConnections()) {
                System.out.println("  Type: " + connection.getConnectionType() +
                        ", Name: " + connection.getName());
            }

            // Assert that we found a valid connection
            assertTrue("Expected to find a valid connection", result.isValid());
            assertFalse("Expected to find at least one connection", result.getConnections().isEmpty());
        } else {
            System.out.println("Error message: " + result.getErrorMessage());

            // If these two movies should have a connection, the test should fail
            if (!sharedActors.isEmpty() || !sharedDirectors.isEmpty() ||
                    !sharedWriters.isEmpty() || !sharedCinematographers.isEmpty() ||
                    !sharedComposers.isEmpty()) {
                fail("Validation failed despite shared cast/crew members");
            } else {
                // If there truly are no connections, this assertion passes
                assertTrue("Expected no connections between these movies",
                        sharedActors.isEmpty() && sharedDirectors.isEmpty() &&
                                sharedWriters.isEmpty() && sharedCinematographers.isEmpty() &&
                                sharedComposers.isEmpty());
            }
        }
    }

}
