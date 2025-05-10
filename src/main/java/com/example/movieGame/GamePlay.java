package com.example.movieGame;

import java.util.*;


public class GamePlay
{
    private ArrayList<Integer> moviesUsed; //list of movie IDs ot track if it's been used before

    private int actorConnectionUsage; //# of times an actor connection has been made;
    private int directorConnectionUsage; //# of times a director connection has been made;
    private int writerConnectionUsage; //# of times a writer connection has been made;
    private int cinematographerConnectionUsage; //# of times a cinematographer connection has been made;
    private int composerConnectionUsage; //# of times a composer connection has been made;

    private int numberOfRounds; //tracks # of rounds played (for display)
    private Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    private Movie firstMovie; //first randomly selected movie
    private String winCondition; //TODO move this to Win class once it's set up

    private Player player1;
    private Player player2;
    private String player1Name;

    private Autocomplete autocomplete;

    private List<Movie> availableMovies;

    private ArrayList<SingleConnection> linksToPreviousMovie;  //list of connections to previous movie

    /**
     * Constructor initializes variables, creates players and designates first active player,
     * sets up index with movies, randomly select movie
     *
     */
    public GamePlay(String player1Name, String player2Name) {
        //initialize variables
        moviesUsed = new ArrayList<>();
        Map<String, Integer> connectionsUsageMap;

        actorConnectionUsage = 0;
        directorConnectionUsage = 0;
        writerConnectionUsage = 0;
        cinematographerConnectionUsage = 0;
        composerConnectionUsage = 0;

        numberOfRounds = 0;
        lastFiveMovies = new LinkedList<>();

        //create and designate players
        player1 = new Player(player1Name, true);
        player2 = new Player(player2Name, false);

        //set up data file
        try {
            MovieLoader.creditCSVRead();
            MovieLoader.moviesCSVRead();
            availableMovies = MovieLoader.createMovieFromFiles();
        } catch (Exception e) {
            System.err.println("Error generating movie list.");
        }

        //randomly select movie
        //TODO REMOVE later - for UI testing only
        /*HashSet<String> genre = new HashSet<>(Arrays.asList("Horror", "Drama", "Action"));
        HashSet<String> actors = new HashSet<>();
        HashSet<String> directors = new HashSet<>();
        HashSet<String> writers = new HashSet<>();
        HashSet<String> cinematographers = new HashSet<>();
        HashSet<String> composers = new HashSet<>();
        firstMovie = new Movie("Jaws",24913, 1909L,genre,actors,directors,writers,cinematographers,composers);*/
        //randomly select first movie
        //TODO (keep the below line, remove above after done with UI testing)
        firstMovie = randomMovieSelection();

        //build trie to be used in the autocomplete
        autocomplete = new Autocomplete();
        autocomplete.buildTrie("autocompleteTesting.txt",5); //TODO update this filename to be csv after done testing
    }

    /**
     * selects first move for the start of the game from within index
     *
     */
    public Movie randomMovieSelection() {

        try {
            // Check if data is loaded; if not, load it
            //TODO - not 100% sure, but i think that the next few lines of text are creating the full list of movies object 3 times
            // (twice in the if statement and then once when setting up available movies)
            // i moved the MovieLoader.creditCSVRead() and .moviesCSVRead() to the constructor, so the data gets loaded immediately
            // i think then that the next 6 lines can all go away, if availableMovies becomes an instance variable instead and is also
            // initialized in the constructor
            // I made this change, and seems like it still works
            /*if (MovieLoader.createMovieFromFiles() == null || MovieLoader.createMovieFromFiles().isEmpty()) {
                // If data hasn't been loaded yet, load it
                MovieLoader.creditCSVRead();
                MovieLoader.moviesCSVRead();
            }
            List<Movie> availableMovies = MovieLoader.createMovieFromFiles();*/


            // check that there are movies available
            if (availableMovies.isEmpty()) {
                System.err.println("Error: No movies available for selection");
                return null;
            }


            // new random to select movie
            Random random = new Random();
            Movie selectedMovie;


            // make sure that we only attempt a max of 100 times,
            // so we don't get stuck looking for a movie if all have been used
            int attempts = 0;
            final int maxAttempts = 100;


            // get a random movie, check again if that movie is ued
            do {
                int randomIndex = random.nextInt(availableMovies.size());
                selectedMovie = availableMovies.get(randomIndex);
                attempts++;

                if (attempts >= maxAttempts) {
                    System.err.println("Error: Could not find an unused movie after " + maxAttempts + " attempts.");
                    return null;
                }

            } while (moviesUsed.contains(selectedMovie.getMovieID()));

            // Update used movies and last five movies
            moviesUsed.add(selectedMovie.getMovieID());
            if (lastFiveMovies.size() >= 5) {
                lastFiveMovies.poll();
            }
            lastFiveMovies.add(selectedMovie);

            this.firstMovie = selectedMovie;

            System.out.println("Randomly selected movie: " + selectedMovie.getMovieTitle());
            return selectedMovie;

        } catch (Exception e) {
            System.err.println("Error selecting random movie: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //Checks whether the movie entered has a valid linkage
    public boolean checkValidLinkage(Movie movie) {

        //If the movie entered is the first movie, no valid linkage required
        if (lastFiveMovies.isEmpty())  {
            return true;
        }

        //Extract the previous movie
        //Return true if there was no previous movie (as above)
        Movie previous = ((LinkedList<Movie>) lastFiveMovies).peekLast();
        if (previous == null) return true;


        //Compare across each of the criteria to determine connections
        return sharesConnection(movie.getActors(), previous.getActors()) ||
                sharesConnection(movie.getDirectors(), previous.getDirectors()) ||
                sharesConnection(movie.getWriters(), previous.getWriters()) ||
                sharesConnection(movie.getCinematographers(), previous.getCinematographers()) ||
                sharesConnection(movie.getComposers(), previous.getComposers());

    }

    //Private helper method to determine whether there is a shared connection
    //Connection defined as actor, director, writer, cinematographer or composer
    private boolean sharesConnection(Set<String> movieOne, Set<String> movieTwo) {
        for (String value : movieOne) {
            if (movieTwo.contains(value)) {
                return true;
            }
        }
        return false;
    }



    /**
     * tracks time for each player
     *
     */
    public void thirtySecondTimer() {
        //TODO
        //not sure what it should return/ how this would work
    }

    /**
     * updates game state and variables based on user entry
     *
     * @return error message citing reason for the error
     * @parameter
     */
    public String userEntry(Movie movie) {

        //check for a valid linkage (return error if no link)
        if (!checkValidLinkage(movie)) {
            return "Error: Movie is not connected to the last movie played.";
        }

        //check for duplicate movie (return error if issue)
        if (moviesUsed.contains(movie.getMovieID())) {
            return "Error: main.java.com.example.movieGame.Movie already used.";
        }

        //increment xxxConnectionUsage variables for all matching connections
        //Keep track of links to the previous movie with getLinkstoPreviousMovie
        Movie previous = ((LinkedList<Movie>) lastFiveMovies).peekLast();
        if (previous != null) {
            movie.getLinksToPreviousMovie().clear();

            for (String actor : movie.getActors()) {
                if (previous.getActors().contains(actor)) {
                    movie.getLinksToPreviousMovie().add(new SingleConnection("actor", actor));
                    actorConnectionUsage++;
                }
            }
            for (String director : movie.getDirectors()) {
                if (previous.getDirectors().contains(director)) {
                    movie.getLinksToPreviousMovie().add(new SingleConnection("director", director));
                    directorConnectionUsage++;
                }
            }
            for (String writer : movie.getWriters()) {
                if (previous.getWriters().contains(writer)) {
                    movie.getLinksToPreviousMovie().add(new SingleConnection("writer", writer));
                    writerConnectionUsage++;
                }
            }
            for (String cinematographer : movie.getCinematographers()) {
                if (previous.getCinematographers().contains(cinematographer)) {
                    movie.getLinksToPreviousMovie().add(new SingleConnection("cinematographer", cinematographer));
                    cinematographerConnectionUsage++;
                }
            }
            for (String composer : movie.getComposers()) {
                if (previous.getComposers().contains(composer)) {
                    movie.getLinksToPreviousMovie().add(new SingleConnection("composer", composer));
                    composerConnectionUsage++;
                }
            }
        }

        //check for 3+ connections (return error message if issue)
        if (actorConnectionUsage > 3) { //TODO - check - >3 or >=3?
            return "Error: Actor used too many times.";
        }
        if (directorConnectionUsage > 3) { //TODO - check - >3 or >=3?
            return "Error: Director used too many times.";
        }
        if (writerConnectionUsage > 3) { //TODO - check - >3 or >=3?
            return "Error: Writer used too many times.";
        }
        if (cinematographerConnectionUsage > 3) { //TODO - check - >3 or >=3?
            return "Error: Cinematographer used too many times.";
        }
        if (composerConnectionUsage > 3) { //TODO - check - >3 or >=3?
            return "Error: Composer used too many times.";
        }

        //add movie name to moviesUsed variable
        moviesUsed.add(movie.getMovieID());

        //increment number of rounds played
        numberOfRounds++;

        //add main.java.com.example.movieGame.Movie object to lastFiveMovies & remove first item
        lastFiveMovies.add(movie);
        lastFiveMovies.poll();

        //update active player
        if (player1.getIsActive() == false) {
            player1.setIsActive(true);
            player2.setIsActive(false);
        } else {
            player1.setIsActive(false);
            player2.setIsActive(true);
        }

        return "Valid User Entry";
    }


    /**
     *
     *
     */



    //TODO need getters/setters

    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public String getActivePlayerName() {
        if (player1.getIsActive()) {
            return player1.getUserName();
        } else {
            return player2.getUserName();
        }
    }
    public int getNumberOfRounds() {
        return numberOfRounds;
    }
    public Movie getFirstMovie() {
        return firstMovie;
    }
    public Autocomplete getAutocomplete() {
        return autocomplete;
    }

    //TODO move this to win class once created and update UI accordingly
    public String getWinCondition() {
        return winCondition;
    }
    //TODO move this to win class once created and update UI accordingly
    public void setWinCondition(String winCondition) {
        this.winCondition = winCondition;
    }
}


