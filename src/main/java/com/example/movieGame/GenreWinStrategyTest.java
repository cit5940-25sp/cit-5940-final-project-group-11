package com.example.movieGame;
import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

/**
 * Test class for GenreWinStrategy class
 *
 */
public class GenreWinStrategyTest {

    //Create fields to track
    private Player player;
    private GenreWinStrategy strategy;

    //Test the constructor
    @Test
    public void testGenreWinStrategyConstructor() throws NoSuchFieldException, IllegalAccessException {

        //Create test GenreWinStrategy
        strategy = new GenreWinStrategy("Action");

        //Check initialisation
        var field = GenreWinStrategy.class.getDeclaredField("targetGenre");
        field.setAccessible(true);
        String actualGenre = (String) field.get(strategy);
        assertEquals("Action", actualGenre);
    }

    //Test that genre increments correctly when correct
    @Test
    public void testGenreMatchesAndProgressIncrements() {

       //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new GenreWinStrategy("Action");

        //Generate a random movie with the Action genre
        HashSet<String> genres = new HashSet<>();
        genres.add("Action");
        Movie movie = new Movie("Action Movie", 1, 2020L, genres,
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter progresses and that the player has not won
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(player.getProgressTowardWin(), 1);
        assertFalse(hasWon);
    }



    //Test that genre does not increment when incorrect
    @Test
    public void testGenreDoesNotMatch() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new GenreWinStrategy("Action");

        //Generate a random movie with the Comedy genre
        HashSet<String> genres = new HashSet<>();
        genres.add("Comedy");
        Movie movie = new Movie("Funny Movie", 2, 2020L, genres,
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter does not progress and that the player has not won
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(player.getProgressTowardWin(), 0);
        assertFalse(hasWon);

    }

    @Test
    public void testWinAfterFiveMatchingMovies() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new GenreWinStrategy("Action");

        //Generate a random movie with the Action genre
        HashSet<String> genres = new HashSet<>();
        genres.add("Action");

        Movie movie = new Movie("Action Movie", 3, 2020L, genres,
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter progresses and that the player wins once it reaches 5
        boolean hasWon = false;
        for (int i = 0; i < 5; i++) {
            hasWon = strategy.checkWin(player, movie);
        }
        assertEquals(player.getProgressTowardWin(), 5);
        assertTrue(hasWon);
    }

    @Test
    public void testNullGenreHandledGracefully() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new GenreWinStrategy("Action");

        //Create null genre movie
        Movie movie = new Movie("Unknown Genre Movie", 4, 2020L, null,
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Test win is not incremented
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(player.getProgressTowardWin(), 0);
        assertFalse(hasWon);
    }

}
