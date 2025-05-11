package com.example.movieGame;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.testng.AssertJUnit.*;

public class MovieLoaderTest {

    //Test the method works for a specific example
    @Test
    public void testMovieUpload() throws IOException {

        //Upload all the files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        //Define test case
        int testId = 8193;

        //Write expected output for the test cases
        String expectedTitle = "Napoleon Dynamite";
        String expectedGenre = "Comedy";
        Long expectedYear = 2004L;
        Movie movieExample = MovieLoader.getMoviesHashMap().get(testId);

        //Define assertions
        assertEquals(expectedTitle, movieExample.getMovieTitle());
        assertTrue(movieExample.getGenre().contains(expectedGenre));
        assertEquals(expectedYear, movieExample.getReleaseYear());
        assertTrue(movieExample.getActors().contains("Jon Heder"));
        assertTrue(movieExample.getDirectors().contains("Jared Hess"));
        assertTrue(movieExample.getCinematographers().contains("Munn Powell"));
        assertTrue(movieExample.getWriters().contains("Jared Hess"));
        assertTrue(movieExample.getComposers().isEmpty());

    }


    //Test that the credits CSV file is properly loaded
    @Test
    public void testCredits() throws IOException {

        //Upload all the files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();
        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();

        assertFalse("Movie map should not be empty after creditCSVRead()", movieMap.isEmpty());

        //Define test case
        int testId = 8193;

        //Test whether populated (title, actors, directors, cinematographers, writers, composers)
        Movie movieExample = movieMap.get(testId);
        assertNotNull( "Actors should be populated", movieExample.getActors());
        assertNotNull("Directors should be populated", movieExample.getDirectors());
        assertNotNull("Titles should be populated", movieExample.getMovieTitle());
        assertNotNull("Cinematographers should be populated", movieExample.getCinematographers());
        assertNotNull("Writers should be populated", movieExample.getWriters());
        assertNotNull("Composers should be populated", movieExample.getComposers());

    }

    //Test that the movies CSV file is properly loaded
    @Test
    public void testMovies() throws IOException {

        //Upload all the files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();
        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();

        assertFalse("Movie map should not be empty after movieCSVRead()", movieMap.isEmpty());

        //Define test case
        int testId = 8193;

        //Test whether populated (genre, release date)
        Movie movieExample = movieMap.get(testId);
        assertNotNull( "Genre should be populated", movieExample.getGenre());
        assertNotNull( "Year should be populated", movieExample.getReleaseYear());
    }

    //Test what happens with invalid entries are not created
    @Test
    public void testInvalidEntries() {

    }

    //Test movie object is created correctly for valid and invalid rows
    @Test
    public void testCreateMovieFromFiles() {

    }

    //Test getMoviesHashMap()
    @Test
    public void testGetMoviesHashMap() {

    }

    //Test The Lone Ranger and Alice Through the Looking Glass
    @Test
    public void testLinks() throws IOException {

        //Upload all the files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();
        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();

        //Define test case
        int testIdOne = 57201;
        int testIdTwo = 241259;

        //Check both are uploading the Johnny Depp link
        Movie movieExampleOne = movieMap.get(testIdOne);
        Movie movieExampleTwo = movieMap.get(testIdTwo);
        assertTrue(movieExampleOne.getActors().contains("Johnny Depp"));
        assertTrue(movieExampleTwo.getActors().contains("Johnny Depp"));

    }

    //Test for random movie existence
    @Test
    public void testExistence() throws IOException {

        //Upload all the files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();
        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();

        //Define test case
        int testId = 2062;

        //Check both are uploading the Johnny Depp link
        Movie movieExample = movieMap.get(testId);
        assertEquals("Ratatouille", movieExample.getMovieTitle());
    }


    /*
    @Test
    public void testFindingConnection() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Get the two Pirates movies
        Movie pirates1 = movieOutputs.get(18); // Pirates of the Caribbean: The Curse of the Black Pearl
        Movie pirates2 = movieOutputs.get(38); // Pirates of the Caribbean: Dead Man's Chest

        Movie interstellar = movieOutputs.get(1474); // Pirates of the Caribbean: Dead Man's Chest


        System.out.println(interstellar);

        System.out.println(pirates1);
        System.out.println(pirates2);

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

     */
}