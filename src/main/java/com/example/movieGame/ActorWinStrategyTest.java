package com.example.movieGame;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test class for ActorWinStrategy class
 *
 */
public class ActorWinStrategyTest {

    private Player player;
    private ActorWinStrategy strategy;

    //Test the constructor
    @Test
    public void testActorWinStrategyConstructor() throws NoSuchFieldException, IllegalAccessException {

        //Define strategy
        strategy = new ActorWinStrategy("Tom Hanks");

        //Check initialisation
        var field = ActorWinStrategy.class.getDeclaredField("targetActor");
        field.setAccessible(true);
        String actualActor = (String) field.get(strategy);
        assertEquals("Tom Hanks", actualActor);
    }

    //Test that genre increments correctly when correct
    @Test
    public void testActorMatchesAndProgressIncrements() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new ActorWinStrategy("Tom Hanks");

        //Generate a random movie with Tom Hanks as the actor
        HashSet<String> actors = new HashSet<>();
        actors.add("Tom Hanks");
        Movie movie = new Movie("Movie With Hanks", 1, 2020L, new HashSet<>(),
                actors, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter progresses and that the player has not won
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(1, player.getProgressTowardWin());
        assertFalse(hasWon);
    }

    @Test
    public void testActorDoesNotMatch() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new ActorWinStrategy("Tom Hanks");

        //Generate a random movie without Tom Hanks
        HashSet<String> actors = new HashSet<>();
        actors.add("Leonardo DiCaprio");
        Movie movie = new Movie("Movie Without Hanks", 2, 2020L, new HashSet<>(),
                actors, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter does not progress and that the player has not won
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(0, player.getProgressTowardWin());
        assertFalse(hasWon);

    }

    @Test
    public void testWinAfterFiveMatchingMovies() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new ActorWinStrategy("Tom Hanks");

        //Generate a random movie with Tom Hanks as the actor
        HashSet<String> actors = new HashSet<>();
        actors.add("Tom Hanks");
        Movie movie = new Movie("Movie With Hanks", 3, 2020L, new HashSet<>(),
                actors, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Check that the counter progresses and that the player wins once it reaches 5
        boolean hasWon = false;
        for (int i = 0; i < 5; i++) {
            hasWon = strategy.checkWin(player, movie);
        }
        assertEquals(5, player.getProgressTowardWin());
        assertTrue(hasWon);
    }

    @Test
    public void testNullActorsHandledGracefully() {

        //Create test player and test strategy
        player = new Player("TestPlayer", true, false);
        strategy = new ActorWinStrategy("Tom Hanks");

        //Create movie with null actors
        Movie movie = new Movie("Movie With Null Actors", 4, 2020L, new HashSet<>(),
                null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        //Test no increment and no win
        boolean hasWon = strategy.checkWin(player, movie);
        assertEquals(0, player.getProgressTowardWin());
        assertFalse(hasWon);
    }
}
