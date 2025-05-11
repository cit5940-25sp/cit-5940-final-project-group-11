package com.example.movieGame;

import java.io.IOException;
import java.util.*;


public class GamePlay
{
    ArrayList<Integer> moviesUsed; //list of movie IDs ot track if it's been used before

    private int actorConnectionUsage; //# of times an actor connection has been made;
    private int directorConnectionUsage; //# of times a director connection has been made;
    private int writerConnectionUsage; //# of times a writer connection has been made;
    private int cinematographerConnectionUsage; //# of times a cinematographer connection has been made;
    private int composerConnectionUsage; //# of times a composer connection has been made;

    //Win Strategy tracker
    private WinStrategy winStrategy;
    private boolean gameEnded = false;

    //Index Maps
    private Map<String, Movie> moviesByTitle;


    //Usage Maps

    private Map<String, Integer> actorUsage = new HashMap<>();      // Maps actor name to usage count
    private Map<String, Integer> directorUsage = new HashMap<>();   // Maps director name to usage count
    private Map<String, Integer> writerUsage = new HashMap<>();     // Maps writer name to usage count
    private Map<String, Integer> cinematographerUsage = new HashMap<>(); // Maps cinematographer to usage count
    private Map<String, Integer> composerUsage = new HashMap<>();   // Maps composer name to usage count

    private int numberOfRounds; //tracks # of rounds played (for display)

    //Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    //Movie firstMovie; //first randomly selected movie

    private Timer gameTimer;

    private final int maxTimePerTurn = 30;


    Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    private Movie firstMovie; //first randomly selected movie
    private String winCondition; //TODO move this to Win class once it's set up




    private Player player1;
    private Player player2;


    private Autocomplete autocomplete;

    private List<Movie> availableMovies;
    private HashMap<Integer, Movie> availableMoviesHashMap;

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
        player1 = new Player(player1Name, true,false);
        player2 = new Player(player2Name, false,true);

        //set up data file
        try {
            MovieLoader.creditCSVRead();
            MovieLoader.moviesCSVRead();
            availableMovies = MovieLoader.createMovieFromFiles();
            availableMoviesHashMap = MovieLoader.getMoviesHashMap(); //used to look up movie object after pulling from autocomplete

            // build the index after loading the movies
            buildIndex();
        } catch (Exception e) {
            System.err.println("Error generating movie list.");
        }

        //randomly select movie
        //firstMovie = randomMovieSelection();
        firstMovie = moviesByTitle.get("titanic");
        lastFiveMovies.add(firstMovie);
        moviesUsed.add(firstMovie.getMovieID());

    }



    // build the index here, and add each movie to autocomplete
    private void buildIndex() {
        moviesByTitle = new HashMap<>();
        autocomplete = new Autocomplete();

        // iterate over all movies
        for (Movie movie : availableMovies) {
            //put in map of movie title, movie
            moviesByTitle.put(movie.getMovieTitle().toLowerCase(), movie);

            // create autocomplete trie
            String titleAndYear = movie.getMovieTitle().toLowerCase() + ", " + movie.getReleaseYear();
            autocomplete.addWord(titleAndYear, movie.getMovieID());
            //TODO: note - tolowercase functionality here/in autocomplete means dropdown shows as all lowercase
            // to show camelCase in teh dropdown, could change weight to be a string that records the camel case title
            // and return that instead of the all lowercase version...actually that may not work...regardless,
            // think we should should leave as-is and only come back to it later if we have time



        }
    }


    /**
     * selects first move for the start of the game from within index
     *
     */


    public Movie randomMovieSelection() {

        try {
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


    public Movie getMovieFromTitle(String title) {
        title = title.strip().toLowerCase();

        return moviesByTitle.get(title);
    }



     /* updates game state and variables based on user entry
     *
     * @return error message citing reason for the error
     * @parameter
     */
    public String userEntry(Movie movie) {

        //Return game has already ended if gameEnded is true
        if (gameEnded) {
            return "Game already ended";
        }

        // First validate the move
        MoveResult result = validateMove(movie);


        // invalid move, with overused connections
        if (!result.isValid()) {
            // If the error is "Connection made too many times", store the overused connections
            // for display in the UI
            if ("Connection made too many times".equals(result.getErrorMessage()) &&
                    !result.getOverusedConnections().isEmpty()) {
                if (movie.getLinksToPreviousMovie() == null) {
                    movie.linksToPreviousMovie = new ArrayList<>();
                } else {
                    movie.linksToPreviousMovie.clear();
                }
                // Store the overused connections for UI display
                movie.linksToPreviousMovie.addAll(result.getOverusedConnections());
            }
            return result.getErrorMessage(); // Return the error from validation
        }


        // Move is valid, process it
        if (!result.getConnections().isEmpty()) {
            // Store all connections with the movie
            if (movie.getLinksToPreviousMovie() == null) {
                movie.linksToPreviousMovie = new ArrayList<>();
            } else {
                movie.linksToPreviousMovie.clear(); // Clear any existing connections
            }


           // SingleConnection primaryConnection = result.getConnections().get(0);

            // Update the appropriate usage counter for the primary connection

            for (SingleConnection connection : result.getConnections()) {
                String type = connection.getConnectionType();
                String name = connection.getName();

                switch (type) {
                    case "Actor":
                        actorUsage.put(name, actorUsage.getOrDefault(name, 0) + 1);
                        break;
                    case "Director":
                        directorUsage.put(name, directorUsage.getOrDefault(name, 0) + 1);
                        break;
                    case "Writer":
                        writerUsage.put(name, writerUsage.getOrDefault(name, 0) + 1);
                        break;
                    case "Cinematographer":
                        cinematographerUsage.put(name, cinematographerUsage.getOrDefault(name, 0) + 1);
                        break;
                    case "Composer":
                        composerUsage.put(name, composerUsage.getOrDefault(name, 0) + 1);
                        break;
                }
            }




            // Store all valid connections for display
            movie.linksToPreviousMovie.addAll(result.getConnections());
            movie.overloadedLinksToPreviousMovie.addAll(result.getOverusedConnections());
        }

        // Add movie to used list
        moviesUsed.add(movie.getMovieID());

        //Check whether the move resulted in winning
        boolean hasWon = winStrategy.checkWin(getActivePlayer(), movie);
        if (hasWon) {
            gameEnded = true;
            return "Win condition met";
        }

        // Increment number of rounds played
        numberOfRounds++;

        // Add to last five movies and remove oldest if needed
        lastFiveMovies.add(movie);
        if (lastFiveMovies.size() > 5) {
            lastFiveMovies.poll();
        }

        // Switch active player
        switchActivePlayer();

        return "Valid User Entry";
    }

    public Player getActivePlayer() {
        if (player1.getIsActive()) {
            return player1;
        } else {
            return player2;
        }
    }
    public Player getInactivePlayer() {
        if (player1.getIsInactive()) {
            return player1;
        } else {
            return player2;
        }
    }

    public void switchActivePlayer() {
        player1.setIsActive(!player1.getIsActive());
        player2.setIsActive(!player2.getIsActive());
        player1.setIsInactive(!player1.getIsInactive());
        player2.setIsActive(!player2.getIsInactive());
    }


    public MoveResult validateMove(Movie movie) {

        // Check if movie has been used already
        if (moviesUsed.contains(movie.getMovieID())) {
            return MoveResult.failure("Movie already used");
        }

        // Get the previous movie to compare against
        Movie previousMovie = null;
        if (!lastFiveMovies.isEmpty()) {
            previousMovie = ((LinkedList<Movie>) lastFiveMovies).getLast();
        } else if (firstMovie != null) {
            previousMovie = firstMovie;
        } else {
            return MoveResult.failure("No previous movie to connect to");
        }

        // We'll collect all valid connections AND overused connections
        List<SingleConnection> validConnections = new ArrayList<>();
        ArrayList<SingleConnection> overusedConnections = new ArrayList<>();

        boolean foundAnyConnection = false;
        //boolean foundOverusedConnection = false;


        // Check actors
        for (String actor : previousMovie.getActors()) {
            if (movie.getActors().contains(actor)) {

                foundAnyConnection = true;
                int usage = actorUsage.getOrDefault(actor, 0);
                if (usage < 3) {
                    // Found a valid actor connection that hasn't been used 3 times
                    validConnections.add(new SingleConnection("Actor", actor));
                } else {
                    overusedConnections.add(new SingleConnection("Actor", actor, true));
                }
            }
        }

        // Check directors
        for (String director : previousMovie.getDirectors()) {
            if (movie.getDirectors().contains(director)) {
                foundAnyConnection = true;
                int usage = directorUsage.getOrDefault(director, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Director", director));
                } else {
                    overusedConnections.add(new SingleConnection("Director", director,true));
                }
            }
        }

        // Check writers
        for (String writer : previousMovie.getWriters()) {
            if (movie.getWriters().contains(writer)) {
                foundAnyConnection = true;
                int usage = writerUsage.getOrDefault(writer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Writer", writer));
                } else {
                    overusedConnections.add(new SingleConnection("Writer", writer,true));
                }
            }
        }

        // Check cinematographers
        for (String cinematographer : previousMovie.getCinematographers()) {
            if (movie.getCinematographers().contains(cinematographer)) {

                foundAnyConnection = true;
                int usage = cinematographerUsage.getOrDefault(cinematographer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Cinematographer", cinematographer));
                } else {
                    overusedConnections.add(new SingleConnection("Cinematographer", cinematographer,true));
                }
            }
        }

        // Check composers
        for (String composer : previousMovie.getComposers()) {
            if (movie.getComposers().contains(composer)) {

                foundAnyConnection = true;
                int usage = composerUsage.getOrDefault(composer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Composer", composer));
                } else {
                    overusedConnections.add(new SingleConnection("Composer", composer, true));

                }
            }
        }

        // If we didn't find any valid connections
        /*if (validConnections.isEmpty()) {
            return MoveResult.failure("No valid connection found between movies");
        }*/
        movie.setOverloadedLinks(overusedConnections);
        if (!validConnections.isEmpty()) {
            return MoveResult.success(validConnections,overusedConnections);
        }

        if (foundAnyConnection) {
            return MoveResult.failure("Connection made too many times", overusedConnections);
        }

        return MoveResult.failure("No valid connection found between movies");



        // We found at least one valid connection
        //return MoveResult.success(validConnections);


    }

    public void setActorUsage(String name, int freq) {

        actorUsage.put(name,freq);
    }


    public Movie findTermById(int movieId) {
        return availableMoviesHashMap.get(movieId);
    }

    private boolean checkIfContributesToWinCondition (Movie movie) {
        String winCond = this.winCondition;
        if (winCondition.equals("horror") && movie.getGenre().contains("Horror")) {
            return true;
        } else if (winCondition.equals("comedy") && movie.getGenre().contains("Comedy")) {
            return true;
        } else if (winCondition.equals("action") && movie.getGenre().contains("Action")) {
            return true;
        } else if (winCondition.equals("sciFi") &&
                (movie.getGenre().contains("Science Fiction") || movie.getGenre().contains("Sci-Fi"))) {
            return true;
        } else if (winCondition.equals("drama") && movie.getGenre().contains("Drama")) {
            return true;
        }

        return false;


    }

    /**
     *
     *
     */



    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public String getActivePlayerName() {
        /*if (player1.getIsActive()) {
            return player1.getUserName();
        } else {
            return player2.getUserName();
        }*/
        return getActivePlayer().getUserName();
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
    public Queue<Movie> getLastFiveMovies() {
        return lastFiveMovies;
    }

    //TODO move this to win class once created and update UI accordingly
    public String getWinCondition() {

        return winCondition;

    }

    //TODO move this to win class once created and update UI accordingly
    public void setWinCondition(String winCondition) {

        //Set the winCondition as what is entered in the UI
        //Define the strategy based on the entered genre
        this.winCondition = winCondition;

        if (winCondition.toLowerCase().startsWith("actor:")) {
            String actorName = winCondition.substring(6).trim();  // skip "Actor:"
            this.winStrategy = new ActorWinStrategy(actorName);
        } else {
            this.winStrategy = new GenreWinStrategy(winCondition);
        }


        //this.winStrategy = new GenreWinStrategy(winCondition);

    }


}


