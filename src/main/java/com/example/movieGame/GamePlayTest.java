package com.example.movieGame;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test class for GamePlay class
 *
 */
public class GamePlayTest {

    //private GamePlay gamePlay;
    //private Movie movie1, movie2, movie3;

    @Test
    public void testRandomMovieSelection() throws IOException {

        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieList = MovieLoader.createMovieFromFiles(); // THIS WAS MISSING

        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();
        GamePlay gamePlay = new GamePlay("P1", "P2");

        // Test that random movie selection works
        Movie selectedMovie = gamePlay.randomMovieSelection();
        assertNotNull("Random movie selection should return a movie", selectedMovie);
        assertTrue("Selected movie should be added to used movies",
                gamePlay.moviesUsed.contains(selectedMovie.getMovieID()));
        assertTrue("Selected movie should be in lastFiveMovies",
                gamePlay.lastFiveMovies.contains(selectedMovie));
    }

    @Test
    public void testCheckValidLinkage() {

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


    @Test
    public void testConnectionBetweenLoneRangerAndAlice() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieList = MovieLoader.createMovieFromFiles();

        HashMap<Integer, Movie> movieMap = MovieLoader.getMoviesHashMap();


        // Get our specific test movies by ID
        Movie loneRanger = movieMap.get(57201);    // The Lone Ranger
        Movie aliceThroughLookingGlass = movieMap.get(241259);    // Alice Through the Looking Glass

        // Create GamePlay
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

        // Basic validation
        assertTrue("Expected to find a valid connection", result.isValid());
        assertFalse("Expected to find at least one connection", result.getConnections().isEmpty());

        // Collect actor connections from the result
        Set<String> actorConnectionsFound = new HashSet<>();
        Set<String> directorConnectionsFound = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnectionsFound.add(connection.getName());
            }
        }
        for (SingleConnection connection : result.getConnections()) {
            if ("Director".equals(connection.getConnectionType())) {
                directorConnectionsFound.add(connection.getName());
            }
        }

        // Verify the two expected actors are found
        assertTrue("Johnny Depp should be identified as a shared actor",
                actorConnectionsFound.contains("Johnny Depp"));
        assertTrue("Helena Bonham Carter should be identified as a shared actor",
                actorConnectionsFound.contains("Helena Bonham Carter"));
        assertEquals("There should be no director in common" ,directorConnectionsFound.size(), 0);

        // Verify no extra actors were found
        assertEquals("Should find exactly 2 shared actors", 2, actorConnectionsFound.size());
    }



    @Test
    public void testSomethingElse() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create GamePlay
        GamePlay play = new GamePlay("Sam", "Angela");

        // Set a win condition
        play.setWinCondition("Drama");

        // Get the Harry Potter movies
        Movie philStoneHP = play.findTermById(671);
        Movie chamberSecretsHP = play.findTermById(672);

        // Check if the movies were found
        assertNotNull("Philosopher's Stone should be found", philStoneHP);
        assertNotNull("Chamber of Secrets should be found", chamberSecretsHP);

        // Debug - print actor info to check for common connections
        System.out.println("Movie 1 actors: " + philStoneHP.getActors());
        System.out.println("Movie 2 actors: " + chamberSecretsHP.getActors());

        // Find shared actors (for debugging)
        HashSet<String> sharedActors = new HashSet<>(philStoneHP.getActors());
        sharedActors.retainAll(chamberSecretsHP.getActors());
        System.out.println("Shared actors: " + sharedActors);
        System.out.println(sharedActors.size());

        // If no shared actors, manually create a connection for testing


        // Reset the game state
        while (!play.lastFiveMovies.isEmpty()) {
            play.lastFiveMovies.poll();
        }
        play.moviesUsed.clear();

        // Set up the first movie properly
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());

        // Verify the setup
        System.out.println("First movie: " + play.getFirstMovie().getMovieTitle());
        System.out.println("Last movie in queue: " + ((LinkedList<Movie>)play.lastFiveMovies).getLast().getMovieTitle());

        // Now submit the second movie
        MoveResult result = play.validateMove(chamberSecretsHP);
        System.out.println("Validation result: " + (result.isValid() ? "Valid" : "Invalid"));
        if (!result.isValid()) {
            System.out.println("Error: " + result.getErrorMessage());
        }

        // Now test userEntry
        String entryResult = play.userEntry(chamberSecretsHP);
        System.out.println("User entry result: " + entryResult);
    }





    @Test
    public void testHarryPotterConnectionsandThenNapoleonDynamite() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create a new GamePlay instance
        GamePlay play = new GamePlay("Sam", "Angela");

        // Get the Harry Potter movies
        Movie philStoneHP = play.findTermById(671);
        Movie chamberSecretsHP = play.findTermById(672);
        Movie prisonerAzkabanHP = play.findTermById(673);
        Movie halfBloodHP = play.findTermById(767);
        Movie orderPhoenixHP = play.findTermById(675);
        Movie gobFireHP = play.findTermById(674);
        Movie napDynamite = play.findTermById(8193);


        assertNotNull("Philosopher's Stone should be found", philStoneHP);
        assertNotNull("Chamber of Secrets should be found", chamberSecretsHP);
        assertNotNull("Prisoner of Azkaban should be found", prisonerAzkabanHP);

        // Clear previous movies and set the first movie
        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());

        // Validate connection between Philosopher's Stone and Chamber of Secrets
        MoveResult result = play.validateMove(chamberSecretsHP);
        assertTrue("Expected to find valid connections", result.isValid());
        assertFalse("Expected at least one connection", result.getConnections().isEmpty());

        assertNull("Valid connections return a null err message", result.getErrorMessage());

        // Collect actor connections
        Set<String> actorConnections = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnections.add(connection.getName());
            }
        }

        // Expected common actors
        Set<String> expectedActors = new HashSet<>(Arrays.asList(
                "Daniel Radcliffe", "Emma Watson", "Alan Rickman"
        ));

        // Assert that expected actors are in the connection list
        for (String actor : expectedActors) {
            assertTrue("Expected actor connection for " + actor, actorConnections.contains(actor));
        }

        // Add again
        result = play.validateMove(prisonerAzkabanHP);
        assertTrue("Expected to find valid connections", result.isValid());
        assertFalse("Expected at least one connection", result.getConnections().isEmpty());

        // Collect actor connections
        Set<String> actorConnections1 = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnections1.add(connection.getName());
            }
        }

        for (String actor : expectedActors) {
            assertTrue("Expected actor connection for " + actor, actorConnections1.contains(actor));
        }


        result = play.validateMove(napDynamite);
        assertFalse("Expected not to find valid connections", result.isValid());

        assertTrue("Expected no connections", result.getConnections().isEmpty());
        assertEquals("No valid connection should return a no valid message",
                "No valid connection found between movies", result.getErrorMessage());









    }

    @Test
    public void testHarryPotterOverloadUssage() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create a new GamePlay instance
        GamePlay play = new GamePlay("Sam", "Angela");

        // Get the Harry Potter movies
        Movie philStoneHP = play.findTermById(671);
        Movie chamberSecretsHP = play.findTermById(672);
        Movie prisonerAzkabanHP = play.findTermById(673);
        Movie perksOfBeingWallflower = play.findTermById(84892);

        assertNotNull("Philosopher's Stone should be found", philStoneHP);
        assertNotNull("Chamber of Secrets should be found", chamberSecretsHP);
        assertNotNull("Prisoner of Azkaban should be found", prisonerAzkabanHP);

        // Clear previous movies and set the first movie
        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());

        // Set Daniel Radcliffe and Alan Rickman usage to 3
        play.setActorUsage("Daniel Radcliffe", 3);
        play.setActorUsage("Alan Rickman", 3);

        // Validate connection with Chamber of Secrets
        MoveResult result = play.validateMove(chamberSecretsHP);
        assertTrue("Expected to find valid connections", result.isValid());
        assertFalse("Expected at least one connection", result.getConnections().isEmpty());
        // Valid connection, so error message is null
        assertNull("Valid connections return a null err message", result.getErrorMessage());


        // Collect actor connections
        Set<String> actorConnections = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnections.add(connection.getName());
            }
        }

        // Expected common actors (only Emma Watson should be valid)
        Set<String> expectedActors = new HashSet<>(Arrays.asList("Emma Watson", "Rupert Grint"));

        // Assert only Emma Watson is in the connection list
        for (String actor : expectedActors) {
            assertTrue("Expected actor connection for " + actor, actorConnections.contains(actor));
        }

        assertFalse("Daniel Radcliffe should not be included", actorConnections.contains("Daniel Radcliffe"));
        assertFalse("Alan Rickman should not be included", actorConnections.contains("Alan Rickman"));

        Set<String> overusedConnections = new HashSet<>();
        for (SingleConnection connection : result.getOverusedConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                overusedConnections.add(connection.getName());
            }
        }

        assertTrue("Daniel Radcliffe should be overused", overusedConnections.contains("Daniel Radcliffe"));
        assertTrue("Alan Rickman should be overused", overusedConnections.contains("Alan Rickman"));
        assertFalse("Emma Watson should not be overused", overusedConnections.contains("Emma Watson"));


    }

    @Test
    public void testOverloadedSolitaryConnection() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create a new GamePlay instance
        GamePlay play = new GamePlay("Sam", "Angela");

        // Get the Harry Potter movies
        Movie philStoneHP = play.findTermById(671);
        Movie chamberSecretsHP = play.findTermById(672);
        Movie prisonerAzkabanHP = play.findTermById(673);
        Movie perksOfBeingWallflower = play.findTermById(84892);

        assertNotNull("Philosopher's Stone should be found", philStoneHP);


        // Clear previous movies and set the first movie
        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());

        // Set Emma usage to 3 POBAWF won't add properly

        play.setActorUsage("Emma Watson", 3);

        // Invalidate connection with the Perks of Being A wallflower
        MoveResult result = play.validateMove(perksOfBeingWallflower);
        assertFalse("Expected to find valid connections", result.isValid());
        assertTrue("Expected at least one connection", result.getConnections().isEmpty());
        // Valid connection, so error message is null
        assertEquals("Connection made too many times", result.getErrorMessage());
        //assertNotNull(("Valid connections return a null err message", result.getErrorMessage());


        // Collect actor connections
        Set<String> actorConnections = new HashSet<>();
        for (SingleConnection connection : result.getConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                actorConnections.add(connection.getName());
            }
        }

        // Expected common actors (only Emma Watson should be valid)
        Set<String> expectedActors = new HashSet<>(Arrays.asList("Emma Watson"));

        // Assert only Emma Watson is in the connection list
        for (String actor : expectedActors) {
            assertFalse("No expected actor connection for " + actor, actorConnections.contains(actor));
        }

        assertFalse("Emma should not be included", actorConnections.contains("Emma Watson"));


        Set<String> overusedConnections = new HashSet<>();
        for (SingleConnection connection : result.getOverusedConnections()) {
            if ("Actor".equals(connection.getConnectionType())) {
                overusedConnections.add(connection.getName());
            }
        }


        assertTrue("Emma Watson should be overused", overusedConnections.contains("Emma Watson"));
    }


    @Test
    public void testUserEntry() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create a new GamePlay instance
        GamePlay play = new GamePlay("Sam", "Angela");

        // Get the Harry Potter movies
        Movie philStoneHP = play.findTermById(671);
        Movie chamberSecretsHP = play.findTermById(672);
        Movie prisonerAzkabanHP = play.findTermById(673);
        Movie halfBloodHP = play.findTermById(767);
        Movie orderPhoenixHP = play.findTermById(675);
        Movie gobFireHP = play.findTermById(674);
        Movie perksOfBeingWallflower = play.findTermById(84892);
        Movie napDynamite = play.findTermById(8193);

        assertNotNull("Philosopher's Stone should be found", philStoneHP);
        assertNotNull("Chamber of Secrets should be found", chamberSecretsHP);
        assertNotNull("Prisoner of Azkaban should be found", prisonerAzkabanHP);
        assertNotNull("Half Blood Prince should be found", halfBloodHP);
        assertNotNull("Order of the Phoenix should be found", orderPhoenixHP);
        assertNotNull("The goblet of Fire should be found", gobFireHP);
        assertNotNull("Perks of Being a Wallflower should be found", perksOfBeingWallflower);

        // Clear previous movies and set the first movie
        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());
        play.setWinCondition("GenreWinStrategy");



        // User entry with Chamber of Secrets
        String entryResult = play.userEntry(chamberSecretsHP);
        assertEquals("Valid User Entry", entryResult);

        // Verify that Chamber of Secrets is now in lastFiveMovies and moviesUsed
        assertTrue("Chamber of Secrets should be in lastFiveMovies", play.lastFiveMovies.contains(chamberSecretsHP));
        assertTrue("Chamber of Secrets should be in moviesUsed", play.moviesUsed.contains(chamberSecretsHP.getMovieID()));



        // Verify common connections in Chamber of Secrets
        ArrayList<SingleConnection> chamberSecretsList = chamberSecretsHP.getLinksToPreviousMovie();
        assertNotNull("Links to previous movie should not be null", chamberSecretsList);
        assertFalse("Links to previous movie should not be empty", chamberSecretsList.isEmpty());

        Set<String> expectedConnections = new HashSet<>(Arrays.asList("Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Chris Columbus"));
        Set<String> actualConnections = new HashSet<>();
        for (SingleConnection connection : chamberSecretsList) {
            actualConnections.add(connection.getName());
        }

        for (String expected : expectedConnections) {
            assertTrue("Expected connection for " + expected, actualConnections.contains(expected));
        }



        // Verify player switch
        String activePlayerAfterEntry = play.getActivePlayer().getUserName();
        assertEquals("Expected player to switch after valid entry", play.getPlayer2().getUserName(), activePlayerAfterEntry);

        // Now do perks of being a wallflower

        entryResult = play.userEntry(perksOfBeingWallflower);

        activePlayerAfterEntry = play.getActivePlayer().getUserName();
        assertEquals("Expected player to switch after valid entry", play.getPlayer1().getUserName(), activePlayerAfterEntry);
        assertEquals("Valid User Entry", entryResult);

        ArrayList<SingleConnection> wallflowerList = perksOfBeingWallflower.getLinksToPreviousMovie();



        assertEquals("This connection should only be Actor Emma Watson:",
                "Actor", wallflowerList.get(0).getConnectionType());
        assertEquals("This connection should only be Actor Emma Watson:",
                "Emma Watson", wallflowerList.get(0).getName());
        assertEquals("Only emma should be here", 1, wallflowerList.size());




        // Set usage for Daniel Radcliffe to 3 and validate another entry
        //play.setActorUsage("Daniel Radcliffe", 3);
        entryResult = play.userEntry(prisonerAzkabanHP);
        activePlayerAfterEntry = play.getActivePlayerName();
        assertEquals("Expected player to switch after valid entry", "Angela", activePlayerAfterEntry);
        assertEquals("Valid User Entry", entryResult);
        ArrayList<SingleConnection> azkabanList = prisonerAzkabanHP.getLinksToPreviousMovie();

        // Again only Emma Should be here.
        assertEquals("This connection should only be Actor Emma Watson:",
                "Actor", azkabanList.get(0).getConnectionType());
        assertEquals("This connection should only be Actor Emma Watson:",
                "Emma Watson", azkabanList.get(0).getName());
        assertEquals("Only emma should be here", 1, azkabanList.size());

        System.out.println("azkaban done");

        entryResult = play.userEntry(gobFireHP);
        assertEquals("Valid User Entry", entryResult);

        // Collect overused connections
        Set<String> overusedConnections = new HashSet<>();
        for (SingleConnection connection : prisonerAzkabanHP.getOverloadedLinks()) {
            if ("Actor".equals(connection.getConnectionType())) {
                overusedConnections.add(connection.getName());
            }
        }

        // Check that Daniel Radcliffe is overused
        //assertTrue("Daniel Radcliffe should be overused", overusedConnections.contains("Daniel Radcliffe"));
        assertFalse("Emma Watson should not be overused", overusedConnections.contains("Emma Watson"));

        System.out.println("User entry test completed.");
    }


}
