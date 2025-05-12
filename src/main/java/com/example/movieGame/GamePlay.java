package com.example.movieGame;

import java.util.*;

/**
 * Handles the primary logic and actions for the game play.
 * Selects a random movie at the beginning
 * Each time a user submits a movie entry, it checks for valid connections, increments round count, switches the active player, etc.
 *
 */
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
    boolean gameEnded = false;

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
     * @param player1Name name of player 1
     * @param player2Name name of player 2
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


    /**
     * Builds the index and adds each movie to autocomplete
     *
     */
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
     * Selects first move for the start of the game
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


     /**
     * updates game state and variables based on user entry
     *
     * @return message citing whethere there's an error, or if the connection is valid
     * @param movie movie selected by user
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

            //movie.overloadedLinksToPreviousMovie.addAll(result.getOverusedConnections());
            movie.setOverloadedLinks((ArrayList<SingleConnection>)result.getOverusedConnections());


            /*System.out.println("After adding:");
            System.out.println("  - movie.overloadedLinksToPreviousMovie size: " +
            movie.overloadedLinksToPreviousMovie.size());
            System.out.println("  - contents: ");
            for (SingleConnection conn : movie.overloadedLinksToPreviousMovie) {
                System.out.println("    * " + conn.getConnectionType() + ": " + conn.getName());
            }*/
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

    /**
     * Returns the active player
     *
     * @return active player
     */
    public Player getActivePlayer() {
        if (player1.getIsActive()) {
            return player1;
        } else {
            return player2;
        }
    }

    /**
     * Switches who the active player is
     *
     */
    public void switchActivePlayer() {
        player1.setIsActive(!player1.getIsActive());
        player2.setIsActive(!player2.getIsActive());
        player1.setIsInactive(!player1.getIsInactive());
        player2.setIsActive(!player2.getIsInactive());
    }

    /**
     * Checks whether the move is valid or invalid
     *
     * @param movie accepts the movie being checked against the most recent movie
     * @return returns an object indicating whether the move was valid, why it isn't (if not), connections, and overused connections
     */
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

    /**
     * Setter for actor usage
     *
     * @param name name of the actor
     * @param freq frequency with which the actor has been used
     */
    public void setActorUsage(String name, int freq) {
        actorUsage.put(name,freq);
    }

    /**
     * Based on the movie id selected by the user, this method finds the corresponding Movie object from within the hashmap and returns it
     * This is used in conjunction with autocomplete dropdown functionality
     *
     * @return returns the corresponding Movie object
     * @param movieId the id of the movie selected by the user
     */
    public Movie findTermById(int movieId) {
        return availableMoviesHashMap.get(movieId);
    }

    /**
     * Getter for player1 object
     *
     * @return player 1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Getter for player2 object
     *
     * @return player 2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Getter for active player
     *
     * @return name of active player
     */
    public String getActivePlayerName() {
        return getActivePlayer().getUserName();
    }

    /**
     * Getter for number of rounds
     *
     * @return number of rounds played
     */
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    /**
     * Getter for first movie object
     *
     * @return Movie object for first movie
     */
    public Movie getFirstMovie() {
        return firstMovie;
    }

    /**
     * Getter for autocomplete (used in ControllerClass)
     *
     * @return Autocomplete
     */
    public Autocomplete getAutocomplete() {
        return autocomplete;
    }

    /**
     * Getter for queue of last five movies
     *
     * @return queue of last five movies
     */
    public Queue<Movie> getLastFiveMovies() {
        return lastFiveMovies;
    }

    /**
     * Getter for win condition
     *
     * @return win condition selected by user
     */
    public String getWinCondition() {
        return winCondition;
    }

    /**
     * Setter for first movie object
     *
     * @param movie movie object to be designated as the first movie
     */
    public void setFirstMovie(Movie movie) {
        this.firstMovie = movie;
    }

    /**
     * Setter for win condition
     *
     * @param winCondition win condition selected by user
     */
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