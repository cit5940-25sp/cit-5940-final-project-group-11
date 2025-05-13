package com.example.movieGame;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Movie object tracks information about each movie (title, ID, actors, directors, writers, composers, release year, genre etc)
 *
 */
public class Movie {
    private String movieTitle;
    private int movieID;

    private HashSet<String> actors;
    private HashSet<String> directors;
    private HashSet<String> writers;
    private HashSet<String> cinematographers;
    private HashSet<String> composers;

    private Long releaseYear;
    private HashSet<String> genre; //Updated to a set of genres
    ArrayList<SingleConnection> linksToPreviousMovie;  //list of connections to previous movie

    ArrayList<SingleConnection> overloadedLinksToPreviousMovie;

    /**
     * Constructor to create the Movie object and initialize variables
     *
     * @param title movie title
     * @param movieID movie id
     * @param releaseYear movie release year
     * @param genre genre of movie
     * @param actors
     * @param directors
     * @param writers
     * @param cinematographers
     * @param composers
     */
    public Movie(String title, int movieID, Long releaseYear, HashSet<String> genre, HashSet<String> actors, HashSet<String> directors, HashSet<String> writers,
                 HashSet<String> cinematographers, HashSet<String> composers) {
        this.movieTitle = title;
        this.genre = genre;
        this.movieID= movieID;
        this.releaseYear = releaseYear;
        this.actors = actors;
        this.directors = directors;
        this.writers = writers;
        this.cinematographers = cinematographers;
        this.composers = composers;

        this.linksToPreviousMovie = new ArrayList<>();
        this.overloadedLinksToPreviousMovie = new ArrayList<>();
    }


    /**
     * Setter for cinematographers
     * @param cinematographers
     */
    public void setCinematographers(HashSet<String> cinematographers) {
        this.cinematographers = cinematographers;
    }

    /**
     * Getter for movie title
     * @return movie title
     */
    public String getMovieTitle() {
        return movieTitle;
    }

    /**
     * Getter for movie ID
     * @return movie ID
     */
    public int getMovieID() {
        return movieID;
    }

    /**
     * Getter for actors
     * @return actors
     */
    public HashSet<String> getActors() {
        return actors;
    }

    /**
     * Getter for directors
     * @return directors
     */
    public HashSet<String> getDirectors() {
        return directors;
    }

    /**
     * Getter for writers
     * @return writers
     */
    public HashSet<String> getWriters() {
        return writers;
    }

    /**
     * Getter for cinematographers
     * @return cinematographers
     */
    public HashSet<String> getCinematographers() {
        return cinematographers;
    }

    /**
     * Getter for composers
     * @return composers
     */
    public HashSet<String> getComposers() {
        return composers;
    }

    /**
     * Getter for movie release year
     * @return movie release year
     */
    public Long getReleaseYear() {
        return releaseYear;
    }

    /**
     * Getter for movie genre
     * @return movie genre
     */
    public HashSet<String> getGenre() {
        return genre;
    }

    /**
     * Getter for ArrayList of connections between current and previous movie selected
     * @return ArrayList of connections
     */
    public ArrayList<SingleConnection> getLinksToPreviousMovie() {
        return linksToPreviousMovie;
    }

    /**
     * Getter for ArrayList of links/connections that have been used
     * @return ArrayList of links/connections that have been used
     */
    public ArrayList<SingleConnection> getOverloadedLinks() {
        return overloadedLinksToPreviousMovie;
    }

    /**
     * Getter for ArrayList of connections between current and previous movie selected
     * @return movieTitle
     */
    @Override
    public String toString() {
        return this.movieTitle;
    }

    /**
     * Setter for ArrayList of links/connections that have been used
     * @param overusedConnections; an array list of links/connections that have been used
     */
    public void setOverloadedLinks(ArrayList<SingleConnection> overusedConnections) {
        this.overloadedLinksToPreviousMovie = overusedConnections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie movie = (Movie) o;
        return movieID == movie.movieID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieID);
    }
}
