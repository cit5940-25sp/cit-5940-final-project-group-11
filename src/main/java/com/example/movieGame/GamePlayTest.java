package com.example.movieGame;

import org.testng.annotations.Test;

import javax.sound.midi.Soundbank;
import javax.swing.plaf.synth.SynthOptionPaneUI;
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
    public void testCommonCinematographers() throws IOException {
        // Load movie data
        MovieLoader.creditCSVRead();
        MovieLoader.moviesCSVRead();
        List<Movie> movieOutputs = MovieLoader.createMovieFromFiles();

        // Create a new GamePlay instance
        GamePlay play = new GamePlay("Sam", "Angela");

        // Get grandbudapesthotel, moonrise kingdom and rushmore

        Movie grand = play.findTermById(120467);
        Movie rushmore = play.findTermById(11545);
        Movie moonrise = play.findTermById(83666);



        // update the cinematographers of these two movies to make the connection
        grand.setCinematographers(new HashSet<>(Set.of("Robert Yeoman")));
        rushmore.setCinematographers(new HashSet<>(Set.of("Robert Yeoman")));

        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(grand);
        play.lastFiveMovies.add(grand);
        play.moviesUsed.add(grand.getMovieID());
        play.setWinCondition("Drama");

        // initially, we should have no progress
        assertEquals("progress should be zero", 0, play.getPlayer1().getProgressTowardWin());

        // play the movie rushmore
        MoveResult result = play.validateMove(rushmore);
        String entry = play.userEntry(rushmore);


        assertTrue("Move should be valid with common cinematographer", result.isValid());

        ArrayList<SingleConnection> rushmoreLinks = rushmore.getLinksToPreviousMovie();


        // check for Yeoman specifically
        boolean foundCinematographerConnection = false;
        for (SingleConnection connection : rushmoreLinks) {
            if ("Cinematographer".equals(connection.getConnectionType()) &&
                    "Robert Yeoman".equals(connection.getName())) {
                foundCinematographerConnection = true;
                break;
            }
        }

        assertTrue("Should find Robert Yeoman as a cinematographer connection", foundCinematographerConnection);

        assertEquals("progress should increase", 1, play.getPlayer1().getProgressTowardWin());

        // manually set progress toward win to make sure the win goes into effect next round
        play.getPlayer2().setProgressInt(4);
        System.out.println(play.getPlayer2().getProgressTowardWin());

        // play moonrise kingdom, which should go to a drama win
        result = play.validateMove(moonrise);
        assertTrue("Moonrise Kingdom move should be valid", result.isValid());
        entry = play.userEntry(moonrise);

        assertEquals("Win condition met", entry);

        assertTrue("Game should have ended", play.gameEnded);


    }
    @Test
    public void testBadMovieList() {
        GamePlay play = new GamePlay("Player1", "Player2");

        // Try to find a movie with an invalid ID (very large number unlikely to exist)
        Movie nonExistentMovie = play.findTermById(999999999);
        assertNull("Non-existent movie ID should return null", nonExistentMovie);

        // Try to find a movie with a negative ID (invalid)
        Movie negativeIdMovie = play.findTermById(-123);
        assertNull("Negative movie ID should return null", negativeIdMovie);

        //Try using a non-existent movie in a move
        Movie fakeMovie = new Movie("Fake Movie", 9999, 2023L,
                new HashSet<>(), new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>(), new HashSet<>());

        MoveResult result = play.validateMove(fakeMovie);
        assertFalse("Move with fake movie should be invalid", result.isValid());
        assertEquals("Should have no valid connection",
                "No valid connection found between movies", result.getErrorMessage());

    }


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
        Movie loveAckshually = play.findTermById(508);

        assertNotNull("Philosopher's Stone should be found", philStoneHP);
        assertNotNull("Chamber of Secrets should be found", chamberSecretsHP);
        assertNotNull("Prisoner of Azkaban should be found", prisonerAzkabanHP);
        assertNotNull("Half Blood Prince should be found", halfBloodHP);
        assertNotNull("Order of the Phoenix should be found", orderPhoenixHP);
        assertNotNull("The goblet of Fire should be found", gobFireHP);
        assertNotNull("Perks of Being a Wallflower should be found", perksOfBeingWallflower);

        // Clear previous movies and set the first movie as 1st Harry potter movie
        play.lastFiveMovies.clear();
        play.moviesUsed.clear();
        play.setFirstMovie(philStoneHP);
        play.lastFiveMovies.add(philStoneHP);
        play.moviesUsed.add(philStoneHP.getMovieID());
        play.setWinCondition("GenreWinStrategy");



        // User first entry os Chamber of Secrets
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
        assertTrue("Perks of BAWF should be in lastFiveMovies", play.lastFiveMovies.contains(perksOfBeingWallflower));
        assertTrue("Perks of BAWF should be in moviesUsed", play.moviesUsed.contains(perksOfBeingWallflower.getMovieID()));

        activePlayerAfterEntry = play.getActivePlayer().getUserName();
        assertEquals("Expected player to switch after valid entry", play.getPlayer1().getUserName(), activePlayerAfterEntry);
        assertEquals("Valid User Entry", entryResult);

        ArrayList<SingleConnection> wallflowerList = perksOfBeingWallflower.getLinksToPreviousMovie();

        assertEquals("This connection should only be Actor Emma Watson:",
                "Actor", wallflowerList.get(0).getConnectionType());
        assertEquals("This connection should only be Actor Emma Watson:",
                "Emma Watson", wallflowerList.get(0).getName());
        assertEquals("Only emma should be here", 1, wallflowerList.size());




        // Next do POA

        entryResult = play.userEntry(prisonerAzkabanHP);
        assertTrue("Prisoner of Azkaban should be in lastFiveMovies", play.lastFiveMovies.contains(prisonerAzkabanHP));
        assertTrue("Prisoner of Azkaban should be in moviesUsed", play.moviesUsed.contains(prisonerAzkabanHP.getMovieID()));


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






        // now do Goblet of Fire
        entryResult = play.userEntry(gobFireHP);
        assertEquals("Valid User Entry", entryResult);
        assertTrue("Goblet of Fire should be in lastFiveMovies", play.lastFiveMovies.contains(gobFireHP));
        assertTrue("Goblet of Fire should be in moviesUsed", play.moviesUsed.contains(gobFireHP.getMovieID()));

        // Collect overused connections
        Set<String> overusedConnections = new HashSet<>();
        for (SingleConnection connection : prisonerAzkabanHP.getOverloadedLinks()) {
            if ("Actor".equals(connection.getConnectionType())) {
                overusedConnections.add(connection.getName());
            }
        }



        //assertFalse("Emma Watson should not be overused", overusedConnections.contains("Emma Watson"));
        //assertTrue("No overused connections yet", overusedConnections.isEmpty());




        // now, finally, play order of phoenix, so NOW emma should be overloaded

        entryResult = play.userEntry(orderPhoenixHP);
        assertEquals("Valid User Entry", entryResult);
        assertTrue("Goblet of Fire should be in lastFiveMovies", play.lastFiveMovies.contains(orderPhoenixHP));
        assertTrue("Goblet of Fire should be in moviesUsed", play.moviesUsed.contains(orderPhoenixHP.getMovieID()));

        //Emma watson should now be overused


        assertFalse("Emma Watson should not be overused", overusedConnections.contains("Emma Watson"));

        ArrayList<SingleConnection> orderPhoenixoveruse = orderPhoenixHP.overloadedLinksToPreviousMovie;

        assertEquals("only emma should be overused", 1, orderPhoenixoveruse.size());
        assertFalse("First movie should not be in last five anymore", play.lastFiveMovies.contains(philStoneHP));
        assertTrue("But it should still be in previously played", play.moviesUsed.contains(philStoneHP.getMovieID()));





        // add one more harry potter movie to make sure that alan rickman, daniel radcliffe is added to overuse

        entryResult = play.userEntry(halfBloodHP);
        assertEquals("Valid User Entry", entryResult);
        assertTrue("Half-Blood Prince should be in lastFiveMovies", play.lastFiveMovies.contains(halfBloodHP));
        assertTrue("Half-Blood Prince should be in moviesUsed", play.moviesUsed.contains(halfBloodHP.getMovieID()));

        // chamber of secrets kicked
        assertFalse("second movie should not be in last five anymore", play.lastFiveMovies.contains(chamberSecretsHP));
        assertTrue("But it should still be in previously played", play.moviesUsed.contains(chamberSecretsHP.getMovieID()));


        ArrayList<SingleConnection> halfBloodOveruse = halfBloodHP.overloadedLinksToPreviousMovie;


        Set<String> overusedActorNames = new HashSet<>();
        for (SingleConnection conn : halfBloodOveruse) {
            if ("Actor".equals(conn.getConnectionType())) {
                overusedActorNames.add(conn.getName());
            }
        }

        // Verify Alan Rikman, Daniel Radcliffe, and Rupert Grint are in the overused list
        //also Emma watson is STILL in there
        assertTrue("Daniel Radcliffe should be overused", overusedActorNames.contains("Daniel Radcliffe"));
        assertTrue("Alan Rickman should be overused", overusedActorNames.contains("Alan Rickman"));
        assertTrue("Rupert Grint should be overused", overusedActorNames.contains("Rupert Grint"));
        assertTrue("Emma should be overused", overusedActorNames.contains("Emma Watson"));

        Player beforeLove = play.getActivePlayer();

        // Now add love actually, which should NOT work because we used the alan rickman connection too many times

        entryResult = play.userEntry(loveAckshually);

        Player afterLove = play.getActivePlayer();

        assertEquals("active player shoudl not change", beforeLove,afterLove);

        assertEquals("We have attempted this connection too many times (alan rickman)",
                "Connection made too many times", entryResult);

        // Love Actually should NOT be added to lastFiveMovies
        assertFalse("Love Actually should NOT be in lastFiveMovies", play.lastFiveMovies.contains(loveAckshually));

        // Love Actually should NOT be added to moviesUsed
        assertFalse("Love Actually should NOT be in moviesUsed", play.moviesUsed.contains(loveAckshually.getMovieID()));

        // Check that the linksToPreviousMovie contains the overused connections (for UI display)
        ArrayList<SingleConnection> loveActuallyLinks = loveAckshually.getLinksToPreviousMovie();
        assertNotNull("Links to previous movie should not be null", loveActuallyLinks);
        assertFalse("Links to previous movie should not be empty", loveActuallyLinks.isEmpty());



        // Try Napoleon Dynamite (no valid connections
        entryResult = play.userEntry(napDynamite);

        // Should return an error message about no valid connection
        assertEquals("No valid connection found between movies", entryResult);

        // Napoleon Dynamite shouldn't be added to lastFiveMovies

        Queue<Movie> lastFive = play.getLastFiveMovies();
        assertFalse("Napoleon Dynamite should NOT be in lastFiveMovies", lastFive.contains(napDynamite));



        // not in movies used
        assertFalse("Napoleon Dynamite should NOT be in moviesUsed", play.moviesUsed.contains(napDynamite.getMovieID()));

        // There should be no connections
        ArrayList<SingleConnection> napDynamiteLinks = napDynamite.getLinksToPreviousMovie();

        // links should be empty

        assertTrue("Links to previous movie should be empty for Napoleon Dynamite", napDynamiteLinks.isEmpty());

        Movie first = play.getFirstMovie();

        assertEquals("first movie shouldn't change", philStoneHP, first);

        assertEquals(play.getNumberOfRounds(), 6);

    }


}
