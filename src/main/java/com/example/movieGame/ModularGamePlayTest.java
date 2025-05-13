package com.example.movieGame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * This test is for GamePlay, and MoveResult
 * It is an updated version of the old one, with more modular testing.
 */
public class ModularGamePlayTest {

    // here we setup gameplay and movie map, and instantiate some movies that we will use commonly
    private GamePlay gamePlay;
    private Map<Integer, Movie> movieMap;

    // Some commonly used movie instances
    private Movie harryPotter1;
    private Movie harryPotter2;
    private Movie harryPotter3;
    private Movie loneRanger;
    private Movie aliceThroughLookingGlass;

    /**
     * this helper method will set up the movie data, so we don't have to do it each time
     */
    @BeforeMethod
    public void setup() throws IOException {
        // Load movie data from files
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieList = MovieLoader.createMovieFromFiles();
        movieMap = MovieLoader.getMoviesHashMap();

        // Make a new GamePlay instance for each test
        gamePlay = new GamePlay("Player1", "Player2");

        // Set a random default win condition
        gamePlay.setWinCondition("Fantasy");

        // Initialize commonly used movies

        // Philosopher's Stone (Alan Rickman, Emma Watson, Daniel Radcliffe)
        harryPotter1 = gamePlay.findTermById(671);
        // Chamber of Secrets (Alan Rickman, Emma Watson, Daniel Radcliffe)
        harryPotter2 = gamePlay.findTermById(672);
        harryPotter3 = gamePlay.findTermById(673);  // Prisoner of Azkaban
        loneRanger = movieMap.get(57201); // THE Lone Ranger (Johnny Depp)
        // Alice Through the looking glass (Johnny Depp, Helena Bonham Carter, Alan Rickman)
        aliceThroughLookingGlass = movieMap.get(241259);
    }

    /**
     * Helper method to set up the game with first movie of my choice
     */
    private void setupGameWithFirstMovie(Movie firstMovie) {
        // Clear any existing movies
        while (!gamePlay.lastFiveMovies.isEmpty()) {
            gamePlay.lastFiveMovies.poll();
        }
        gamePlay.moviesUsed.clear();

        // Set the first movie
        gamePlay.setFirstMovie(firstMovie);
        gamePlay.lastFiveMovies.add(firstMovie);
        gamePlay.moviesUsed.add(firstMovie.getMovieID());
    }

    /**
     * Test that findTermById returns the correct movies by ID
     */
    @Test
    public void testFindTermById() {
        // Test with valid IDs
        Movie movie = gamePlay.findTermById(671);
        assertNotNull("Should find Harry Potter 1 by ID", movie);
        assertEquals("Harry Potter and the Philosopher's Stone", movie.getMovieTitle());

        // Test with invalid IDs
        assertNull("Should return null for a too large ID", gamePlay.findTermById(999999999));
        assertNull("Should return null for a negative movie ID", gamePlay.findTermById(-123));
    }

    @Test
    public void testRandomMovie() {
        // Call random movie selection
        Movie selectedMovie = gamePlay.randomMovieSelection();
        // Ensure movie exists
        assertNotNull("Random movie selection should return a movie", selectedMovie);
        // Test that the movie is in movies used.
        assertTrue("Selected movie should be added to used movies",
                gamePlay.moviesUsed.contains(selectedMovie.getMovieID()));
        // Test that movie is in the last five movies
        assertTrue("Selected movie should be in lastFiveMovies",
                gamePlay.lastFiveMovies.contains(selectedMovie));
    }

    /**
     * Test that validateMove correctly identifies shared actors between movies
     */
    @Test
    public void testValidateMoveSameActors() throws IOException {
        setupGameWithFirstMovie(loneRanger);

        assertEquals("First movie should be lone ranger", loneRanger, gamePlay.getFirstMovie());

        MoveResult result = gamePlay.validateMove(aliceThroughLookingGlass);

        // Check the result says valid with connections
        assertTrue("Move should be valid with shared actors Johnny Depp", result.isValid());
        assertFalse("Should have found connections", result.getConnections().isEmpty());

        // Get actor connections
        Set<String> actorConnections = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnections.add(connection.getName());
            }
        }

        // Verify specific actors were found
        assertTrue("Johnny Depp should be a shared actor",
                actorConnections.contains("Johnny Depp"));
        assertTrue("Helena Bonham Carter should be a shared actor",
                actorConnections.contains("Helena Bonham Carter"));
        assertEquals("There should be 2 shared actors", 2, actorConnections.size());
    }

    /**
     * Test that validateMove correctly handles when a movie has already been played
     */
    @Test
    public void testValidateMoveWithUsedMovie() {
        setupGameWithFirstMovie(harryPotter1);

        // Add HP2 to the used movies list
        gamePlay.moviesUsed.add(harryPotter2.getMovieID());

        // Try to pass the already used movie
        MoveResult result = gamePlay.validateMove(harryPotter2);

        assertFalse("Move should be invalid due to prior use", result.isValid());
        assertEquals("Movie already used", result.getErrorMessage());
    }

    /**
     * Test that validateMove correctly handles overused connections
     */
    @Test
    public void testOverusedConnections() {
        setupGameWithFirstMovie(harryPotter1);

        // set usage for specific actors to simulate overuse
        gamePlay.setActorUsage("Daniel Radcliffe", 3);
        gamePlay.setActorUsage("Alan Rickman", 3);

        // Movie should share these overused actors
        MoveResult result = gamePlay.validateMove(harryPotter2);

        // Move should still be valid because Emma Watson is still a valid connection
        assertTrue("Move should be valid with at least one non-overused connection", result.isValid());

        // Check valid connections (shouldn't include overused actors)
        Set<String> validActors = new HashSet<>();
        for (SingleConnection conn : result.getConnections()) {
            if ("Actor".equals(conn.getConnectionType())) {
                validActors.add(conn.getName());
            }
        }
        assertFalse("Daniel Radcliffe should not be in valid connections",
                validActors.contains("Daniel Radcliffe"));
        assertFalse("Alan Rickman should not be in valid connections",
                validActors.contains("Alan Rickman"));
        assertTrue("Emma Watson should be in valid connections",
                validActors.contains("Emma Watson"));

        // Check overused connections
        Set<String> overusedActors = new HashSet<>();
        for (SingleConnection conn : result.getOverusedConnections()) {
            if ("Actor".equals(conn.getConnectionType())) {
                overusedActors.add(conn.getName());
            }
        }
        assertTrue("Daniel Radcliffe should be in overused connections",
                overusedActors.contains("Daniel Radcliffe"));
        assertTrue("Alan Rickman should be in overused connections",
                overusedActors.contains("Alan Rickman"));
    }

    /**
     * Test case where all connections are overused
     */
    @Test
    public void testAllConnectionsOverused() {
        setupGameWithFirstMovie(harryPotter1);

        // POBAWF only shares the Emma Watson Connection
        Movie perksOfBeingWallflower = gamePlay.findTermById(84892);

        // Set Emma Watson usage to the limit
        gamePlay.setActorUsage("Emma Watson", 3);

        // Submit movie
        MoveResult result = gamePlay.validateMove(perksOfBeingWallflower);

        // Move should be invalid because all connections are overused
        assertFalse("Move should be invalid with all connections overused", result.isValid());
        assertEquals("Connection made too many times", result.getErrorMessage());

        // Check overused connections
        Set<String> overusedActors = new HashSet<>();
        for (SingleConnection conn : result.getOverusedConnections()) {
            if ("Actor".equals(conn.getConnectionType())) {
                overusedActors.add(conn.getName());
            }
        }
        assertTrue("Emma Watson should be in overused connections",
                overusedActors.contains("Emma Watson"));
    }

    /**
     * Test that userEntry processes valid moves correctly
     */
    @Test
    public void testUserEntryValidMove() {
        setupGameWithFirstMovie(harryPotter1);

        // Submit a valid user entry
        String result = gamePlay.userEntry(harryPotter2);

        // Check result
        assertEquals("Valid User Entry", result);

        // Check game state updates
        assertTrue("Movie should be added to lastFiveMovies",
                gamePlay.getLastFiveMovies().contains(harryPotter2));
        assertTrue("Movie should be added to moviesUsed",
                gamePlay.moviesUsed.contains(harryPotter2.getMovieID()));
        assertEquals("Round counter should increment", 1, gamePlay.getNumberOfRounds());

        // Check player switching
        assertEquals("Active player should switch to Player2",
                "Player2", gamePlay.getActivePlayerName());
    }

    /**
     * Test that userEntry correctly handles invalid moves
     */
    @Test
    public void testUserEntryInvalidMove() {
        setupGameWithFirstMovie(harryPotter1);

        // Find a movie with no connections
        Movie napDynamite = gamePlay.findTermById(8193);

        // This user Entry should be invalid, zero connections.
        String result = gamePlay.userEntry(napDynamite);

        // Check result
        assertEquals("No valid connection found between movies", result);

        // Game state should not change
        assertFalse("Movie should not be added to lastFiveMovies",
                gamePlay.getLastFiveMovies().contains(napDynamite));
        assertFalse("Movie should not be added to moviesUsed",
                gamePlay.moviesUsed.contains(napDynamite.getMovieID()));
        assertEquals("Round counter should not increment", 0, gamePlay.getNumberOfRounds());

        // Active player should not change
        assertEquals("Active player should remain Player1",
                "Player1", gamePlay.getActivePlayerName());
    }

    /**
     * Test win condition being successfully met
     */
    @Test
    public void testWinConditionMet() {
        setupGameWithFirstMovie(harryPotter1);

        // Set a specific win condition Fantasy, so Harry Potter satisfies it
        gamePlay.setWinCondition("Fantasy");

        assertEquals("game condition should be fantasy", "Fantasy", gamePlay.getWinCondition());
        assertTrue("harry potter 2 is fantasy", harryPotter2.getGenre().contains("Fantasy"));

        // Set player progress to be 1 away from a win
        gamePlay.getPlayer1().setProgressInt(4);

        // Submit a fantasy movie
        String result = gamePlay.userEntry(harryPotter2);

        // Check result
        assertEquals("Win condition met", result);
        assertTrue("Game should be ended", gamePlay.gameEnded);
    }

    /**
     * Test that cinematographer connections work correctly, because these are rare and I couldn't find any
     */
    @Test
    public void testCinematographerConnection() {
        // Get specific movies
        Movie grand = gamePlay.findTermById(120467);  // Grand Budapest Hotel
        Movie rushmore = gamePlay.findTermById(11545);  // Rushmore

        // Set up cinematographers and update the indexes
        // Again, these are rare, I had to add my own
        grand.setCinematographers(new HashSet<>(Set.of("Robert Yeoman")));
        rushmore.setCinematographers(new HashSet<>(Set.of("Robert Yeoman")));

        // Update the backwards index
        if (!gamePlay.moviesByCinematographer.containsKey("Robert Yeoman")) {
            gamePlay.moviesByCinematographer.put("Robert Yeoman", new HashSet<>());
        }
        gamePlay.moviesByCinematographer.get("Robert Yeoman").add(grand);
        gamePlay.moviesByCinematographer.get("Robert Yeoman").add(rushmore);

        // Set up game
        setupGameWithFirstMovie(grand);
        gamePlay.setWinCondition("Drama");

        // Validate the move
        MoveResult result = gamePlay.validateMove(rushmore);

        // Check results
        assertTrue("Move should be valid with cinematographer connection", result.isValid());

        // Check that Robert Yeoman is a connection
        boolean foundCinematographer = false;
        for (SingleConnection conn : result.getConnections()) {
            if ("Cinematographer".equals(conn.getConnectionType()) &&
                    "Robert Yeoman".equals(conn.getName())) {
                foundCinematographer = true;
                break;
            }
        }
        assertTrue("Should find Robert Yeoman as cinematographer connection", foundCinematographer);
    }

    @Test
    public void testUserEntryWithOnlyOverusedConnections() {
        setupGameWithFirstMovie(harryPotter1);

        // Get Perks of Being a Wallflower (shares Emma Watson with Harry Potter)
        Movie perksOfBeingWallflower = gamePlay.findTermById(84892);
        assertNotNull("Perks of Being a Wallflower should be found", perksOfBeingWallflower);

        // Set Emma Watson usage to overused
        gamePlay.setActorUsage("Emma Watson", 3);

        // First make sure that validateMove correctly identifies this as invalid from overuse
        MoveResult validateResult = gamePlay.validateMove(perksOfBeingWallflower);
        assertFalse("Move should be invalid with only overused connections", validateResult.isValid());
        assertEquals("Error message should indicate overused connections",
                "Connection made too many times", validateResult.getErrorMessage());
        assertFalse("Overused connections list should not be empty",
                validateResult.getOverusedConnections().isEmpty());

        // test userEntry with the same setup
        String entryResult = gamePlay.userEntry(perksOfBeingWallflower);

        // check the result
        assertEquals("Connection made too many times", entryResult);

        // Check that the movie's linksToPreviousMovie contains the overused connections for UI display
        assertNotNull("linksToPreviousMovie should not be null", perksOfBeingWallflower.getLinksToPreviousMovie());
        assertFalse("linksToPreviousMovie should not be empty", perksOfBeingWallflower.getLinksToPreviousMovie().isEmpty());

        // Verify that Emma Watson is in the links to the previous movie as an overused connection
        boolean foundEmmaWatson = false;
        for (SingleConnection conn : perksOfBeingWallflower.getLinksToPreviousMovie()) {
            if ("Actor".equals(conn.getConnectionType()) && "Emma Watson".equals(conn.getName())) {
                foundEmmaWatson = true;
                break;
            }
        }
        assertTrue("Emma Watson should be in linksToPreviousMovie for UI display", foundEmmaWatson);

        // Verify that the game state didn't change, because the round hasn't moved on
        assertFalse("Movie should not added to lastFiveMovies",
                gamePlay.getLastFiveMovies().contains(perksOfBeingWallflower));
        assertFalse("Movie should not be added to moviesUsed",
                gamePlay.moviesUsed.contains(perksOfBeingWallflower.getMovieID()));
        assertEquals("Round counter should not increase", 0, gamePlay.getNumberOfRounds());
        assertEquals("Active player should not change", "Player1", gamePlay.getActivePlayerName());
    }


    @Test
    public void testActorWinCondition() {
        setupGameWithFirstMovie(harryPotter1);

        // Set an actor win condition for Alan Rickman
        gamePlay.setWinCondition("actor:Alan Rickman");

        // Set Sam progress to one away
        gamePlay.getPlayer1().setProgressInt(4);

        // Get an Alan Rickman Movie
        Movie loveActually = gamePlay.findTermById(508);
        assertNotNull("Love Actually should be found", loveActually);

        // Make sure Alan Rickman is not overused yet
        gamePlay.setActorUsage("Alan Rickman", 0);  // Reset usage to be safe

        //Check that validateMove says this move is valid
        MoveResult validateResult = gamePlay.validateMove(loveActually);
        assertTrue("Move should be valid with Alan Rickman connection", validateResult.isValid());

        // Check that Alan Rickman is a connection
        boolean foundAlanRickman = false;
        for (SingleConnection conn : validateResult.getConnections()) {
            if ("Actor".equals(conn.getConnectionType()) && "Alan Rickman".equals(conn.getName())) {
                foundAlanRickman = true;
                break;
            }
        }
        assertTrue("Alan Rickman should be identified as a connection", foundAlanRickman);


        String entryResult = gamePlay.userEntry(loveActually);

        // check if win condition
        assertEquals("Win condition met", entryResult);
        assertTrue("Game should be ended", gamePlay.gameEnded);

    }

}