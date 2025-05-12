package com.example.movieGame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

/**
 * Test class for Movie class
 *
 */
public class MovieTest {

    private HashSet<String> genre = new HashSet<>();
    private HashSet<String> actors = new HashSet<>();
    private HashSet<String> directors = new HashSet<>();
    private HashSet<String> writers = new HashSet<>();
    private HashSet<String> cinematographers = new HashSet<>();
    private HashSet<String> composers = new HashSet<>();

    private Movie movie;
    private SingleConnection actorConnection;
    private SingleConnection directorConnection;

    @Test
    public void getMovieTitle() {
        //EXPECTED
        String expected = "Titanic";
        //ACTUAL
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        String actual = movie.getMovieTitle();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getMovieID() {
        //EXPECTED
        int expected = 597;
        //ACTUAL
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        int actual = movie.getMovieID();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getActors() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("Leonardo DiCaprio");
        expected.add("Kate Winslet");
        //ACTUAL
        actors.add("Leonardo DiCaprio");
        actors.add("Kate Winslet");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getActors();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getDirectors() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("James Cameron");
        //ACTUAL
        directors.add("James Cameron");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getDirectors();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getWriters() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("James Cameron");
        //ACTUAL
        writers.add("James Cameron");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getWriters();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getCinematographers() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("Russell Carpenter");
        //ACTUAL
        cinematographers.add("Russell Carpenter");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getCinematographers();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getComposers() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("Russell Carpenter");
        //ACTUAL
        composers.add("Russell Carpenter");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getComposers();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getReleaseYear() {
        //EXPECTED
        Long expected = 1997L;
        //ACTUAL
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        Long actual = movie.getReleaseYear();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getGenre() {
        //EXPECTED
        HashSet<String> expected = new HashSet<>();
        expected.add("Drama");
        //ACTUAL
        genre.add("Drama");
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        HashSet<String> actual = movie.getGenre();
        //COMPARE
        assertEquals(expected,actual);
    }
    @BeforeMethod
    public void setUp() {
        // Create a movie with sample data
        HashSet<String> actors = new HashSet<>();
        actors.add("Tom Hanks");
        actors.add("Leonardo DiCaprio");

        HashSet<String> directors = new HashSet<>();
        directors.add("Steven Spielberg");

        HashSet<String> writers = new HashSet<>();
        writers.add("Aaron Sorkin");

        HashSet<String> cinematographers = new HashSet<>();
        cinematographers.add("Roger Deakins");

        HashSet<String> composers = new HashSet<>();
        composers.add("John Williams");

        HashSet<String> genres = new HashSet<>();
        genres.add("Drama");

        movie = new Movie("Test Movie", 1, 2023L, genres, actors, directors, writers, cinematographers, composers);

        // Create some SingleConnection objects
        actorConnection = new SingleConnection("Actor", "Tom Hanks");
        directorConnection = new SingleConnection("Director", "Steven Spielberg", false);

        // Add connections to the movie
        movie.getLinksToPreviousMovie().add(actorConnection);
        movie.getLinksToPreviousMovie().add(directorConnection);
    }
    @Test
    public void testGetLinksToPreviousMovie() {
        //created using claude
        // Create a new movie
        Movie testMovie = new Movie("Test Movie", 1, 2023L,
                new HashSet<>(), new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>(), new HashSet<>());

        // Initially, links should be empty
        assertEquals(testMovie.getLinksToPreviousMovie().size(), 0, "Initially should have no links");

        // Create a connection and add it to the movie's links
        SingleConnection actorConnection = new SingleConnection("Actor", "Meryl Streep");
        testMovie.getLinksToPreviousMovie().add(actorConnection);

        // Get the links
        ArrayList<SingleConnection> result = testMovie.getLinksToPreviousMovie();

        // Verify the links list has the connection we added
        assertEquals(result.size(), 1, "Should have 1 connection");
        assertEquals(result.get(0).getConnectionType(), "Actor", "Should have correct connection type");
        assertEquals(result.get(0).getName(), "Meryl Streep", "Should have correct name");
    }
    @Test
    public void toStringTest() {
        //EXPECTED
        String expected = "Titanic";
        //ACTUAL
        Movie movie = new Movie("Titanic",597,1997L,genre,actors,directors,writers,cinematographers,composers);
        String actual = movie.toString();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getOverLoadedLinks() {
        //Created using Claude:
        // Create a simple overloaded connections list
        ArrayList<SingleConnection> overloadedConnections = new ArrayList<>();
        SingleConnection overusedConnection = new SingleConnection("Actor", "Brad Pitt", true);
        overloadedConnections.add(overusedConnection);

        // Set the overloaded links
        movie.setOverloadedLinks(overloadedConnections);

        // Get the overloaded links
        ArrayList<SingleConnection> result = movie.getOverloadedLinks();

        // Assert the result is the same list we set
        assertEquals(result, overloadedConnections, "Should return the overloaded links that were set");
    }
    @Test
    public void setOverloadedLinks() {
        //Created using Claude:
        // Create a simple overloaded connections list
        ArrayList<SingleConnection> overloadedConnections = new ArrayList<>();
        SingleConnection overusedConnection = new SingleConnection("Director", "Christopher Nolan", true);
        overloadedConnections.add(overusedConnection);

        // Initially the overloaded links should be empty
        assertEquals(movie.getOverloadedLinks().size(), 0, "Initially should have no overloaded links");

        // Set the overloaded links
        movie.setOverloadedLinks(overloadedConnections);

        // Verify that getOverloadedLinks returns the list we set
        ArrayList<SingleConnection> result = movie.getOverloadedLinks();
        assertEquals(result.size(), 1, "Should have 1 overloaded connection after setting");
        assertEquals(result.get(0).getConnectionType(), "Director", "Should have correct connection type");
        assertEquals(result.get(0).getName(), "Christopher Nolan", "Should have correct name");
    }
}