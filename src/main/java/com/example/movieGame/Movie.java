package com.example.movieGame;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * main.java.com.example.movieGame.Movie Object
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

    //Constructor to create the Movie object
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




    public String getMovieTitle() {
        return movieTitle;
    }

    public int getMovieID() {
        return movieID;
    }

    public HashSet<String> getActors() {
        return actors;
    }

    public HashSet<String> getDirectors() {
        return directors;
    }

    public HashSet<String> getWriters() {
        return writers;
    }

    public HashSet<String> getCinematographers() {
        return cinematographers;
    }

    public HashSet<String> getComposers() {
        return composers;
    }

    public Long getReleaseYear() {
        return releaseYear;
    }

    public HashSet<String> getGenre() {
        return genre;
    }

    public ArrayList<SingleConnection> getLinksToPreviousMovie() {
        return linksToPreviousMovie;
    }
    public ArrayList<SingleConnection> getOverloadedLinks() {
        return overloadedLinksToPreviousMovie;
    }

    @Override
    public String toString() {
        return this.movieTitle;
    }


    public void setOverloadedLinks(ArrayList<SingleConnection> overusedConnections) {
        this.overloadedLinksToPreviousMovie = overusedConnections;
    }
}
