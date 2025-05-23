package com.example.movieGame;

import java.util.*;

/**
 * Handles the primary logic and actions for the game play.
 * Selects a random movie at the beginning
 * Each time a user submits a movie entry, it checks for valid connections, increments round count, switches the active player, etc.
 *
 */
public class GamePlay {
    ArrayList<Integer> moviesUsed; //list of movie IDs ot track if it's been used before

    //Win Strategy tracker
    private WinStrategy winStrategy;
    boolean gameEnded = false;

    //Index Maps
    private Map<Integer, Movie> moviesByTitle;

    //Usage Maps
    private Map<String, Integer> actorUsage = new HashMap<>();      // Maps actor name to usage count
    private Map<String, Integer> directorUsage = new HashMap<>();   // Maps director name to usage count
    private Map<String, Integer> writerUsage = new HashMap<>();     // Maps writer name to usage count
    private Map<String, Integer> cinematographerUsage = new HashMap<>(); // Maps cinematographer to usage count
    private Map<String, Integer> composerUsage = new HashMap<>();   // Maps composer name to usage count
    private HashSet<String> actorSet = new HashSet<>(); //tracks all actors in the database

    // add backward indices
    private Map<String, Set<Movie>> moviesByActor = new HashMap<>();
    private Map<String, Set<Movie>> moviesByDirector = new HashMap<>();
    private Map<String, Set<Movie>> moviesByWriter = new HashMap<>();
    Map<String, Set<Movie>> moviesByCinematographer = new HashMap<>();
    private Map<String, Set<Movie>> moviesByComposer = new HashMap<>();
    private Map<String, Set<Movie>> moviesByGenre = new HashMap<>();


    private int numberOfRounds; //tracks # of rounds played (for display)

    private Timer gameTimer;

    private final int maxTimePerTurn = 30;

    Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    private Movie firstMovie; //first randomly selected movie
    private String winCondition;

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
     */
    public GamePlay(String player1Name, String player2Name) {
        //initialize variables
        moviesUsed = new ArrayList<>();
        Map<String, Integer> connectionsUsageMap;

        numberOfRounds = 0;
        lastFiveMovies = new LinkedList<>();

        //create and designate players
        player1 = new Player(player1Name, true, false);
        player2 = new Player(player2Name, false, true);

        //set up data file
        try {
            MovieLoader.creditCSVRead();
            MovieLoader.moviesCSVRead();
            availableMovies = MovieLoader.createMovieFromFiles();
            availableMoviesHashMap = MovieLoader.getMoviesHashMap(); //used to look up movie object after pulling from autocomplete
            actorSet = MovieLoader.getActorHashSet();

            // build the index after loading the movies
            buildIndex();
        } catch (Exception e) {
            System.err.println("Error generating movie list.");
        }

        //randomly select movie
        //firstMovie = randomMovieSelection();
        firstMovie = moviesByTitle.get(597); // titanic
        lastFiveMovies.add(firstMovie);
        moviesUsed.add(firstMovie.getMovieID());
    }


    /**
     * Builds the index and adds each movie to autocomplete
     */
    private void buildIndex() {
        // Initialize existing maps
        moviesByTitle = new HashMap<>();

        // Initialize backward index maps
        moviesByActor = new HashMap<>();
        moviesByDirector = new HashMap<>();
        moviesByWriter = new HashMap<>();
        moviesByCinematographer = new HashMap<>();
        moviesByComposer = new HashMap<>();
        moviesByGenre = new HashMap<>();

        autocomplete = new Autocomplete();

        // iterate over all movies
        for (Movie movie : availableMovies) {

            // Add to forward index
            moviesByTitle.put(movie.getMovieID(), movie);

            // create autocomplete trie
            String titleAndYear = movie.getMovieTitle().toLowerCase() + ", " + movie.getReleaseYear();
            autocomplete.addWord(titleAndYear, movie.getMovieID());


            // Add to backward indexes

            // Add to actors index
            for (String actor : movie.getActors()) {
                if (!moviesByActor.containsKey(actor)) {
                    moviesByActor.put(actor, new HashSet<>());
                }
                moviesByActor.get(actor).add(movie);
            }

            // Add to directors index
            for (String director : movie.getDirectors()) {
                if (!moviesByDirector.containsKey(director)) {
                    moviesByDirector.put(director, new HashSet<>());
                }
                moviesByDirector.get(director).add(movie);
            }

            // Add to writers index
            for (String writer : movie.getWriters()) {
                if (!moviesByWriter.containsKey(writer)) {
                    moviesByWriter.put(writer, new HashSet<>());
                }
                moviesByWriter.get(writer).add(movie);
            }

            // Add to cinematographers index
            for (String cinematographer : movie.getCinematographers()) {
                if (!moviesByCinematographer.containsKey(cinematographer)) {
                    moviesByCinematographer.put(cinematographer, new HashSet<>());
                }
                moviesByCinematographer.get(cinematographer).add(movie);
            }

            // Add to composers index
            for (String composer : movie.getComposers()) {
                if (!moviesByComposer.containsKey(composer)) {
                    moviesByComposer.put(composer, new HashSet<>());
                }
                moviesByComposer.get(composer).add(movie);
            }

            // Add to genres index
            for (String genre : movie.getGenre()) {
                if (!moviesByGenre.containsKey(genre)) {
                    moviesByGenre.put(genre, new HashSet<>());
                }
                moviesByGenre.get(genre).add(movie);
            }

        }
    }


    /**
     * Selects first move for the start of the game
     *
     * @return returns the movie object randomly selected
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
     * @param movie movie selected by user
     * @return message citing whethere there's an error, or if the connection is valid
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
            movie.setOverloadedLinks((ArrayList<SingleConnection>) result.getOverusedConnections());


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


        // Check if movie already used
        if (moviesUsed.contains(movie.getMovieID())) {
            return MoveResult.failure("Movie already used");
        }

        // get previous movie to compare with
        Movie previousMovie = null;
        if (!lastFiveMovies.isEmpty()) {
            previousMovie = ((LinkedList<Movie>) lastFiveMovies).getLast();
        } else if (firstMovie != null) {
            previousMovie = firstMovie;
        } else {
            return MoveResult.failure("No previous movie to connect to");
        }

        // Get all valid and overused connections
        List<SingleConnection> validConnections = new ArrayList<>();
        ArrayList<SingleConnection> overusedConnections = new ArrayList<>();
        // Set this boolean, to be used for movies with only ONE valid connection that is overused
        boolean foundAnyConnection = false;

        // Check actors using the index
        for (String actor : previousMovie.getActors()) {
            if (moviesByActor.containsKey(actor)) {
                Set<Movie> moviesWithActor = moviesByActor.get(actor);
                if (moviesWithActor.contains(movie)) {
                    foundAnyConnection = true;
                    int usage = actorUsage.getOrDefault(actor, 0);

                    // Depending on the usage, add to correct respective list as a single connection
                    if (usage < 3) {
                        validConnections.add(new SingleConnection("Actor", actor));
                    } else {
                        overusedConnections.add(new SingleConnection("Actor", actor, true));
                    }
                }
            }
        }

        // Check directors
        for (String director : previousMovie.getDirectors()) {
            if (moviesByDirector.containsKey(director)) {
                Set<Movie> moviesWithDirector = moviesByDirector.get(director);
                if (moviesWithDirector.contains(movie)) {
                    foundAnyConnection = true;
                    int usage = directorUsage.getOrDefault(director, 0);
                    if (usage < 3) {
                        validConnections.add(new SingleConnection("Director", director));
                    } else {
                        overusedConnections.add(new SingleConnection("Director", director, true));
                    }
                }
            }
        }

        // Check writers
        for (String writer : previousMovie.getWriters()) {
            if (moviesByWriter.containsKey(writer)) {
                Set<Movie> moviesWithWriter = moviesByWriter.get(writer);
                if (moviesWithWriter.contains(movie)) {
                    foundAnyConnection = true;
                    int usage = writerUsage.getOrDefault(writer, 0);
                    if (usage < 3) {
                        validConnections.add(new SingleConnection("Writer", writer));
                    } else {
                        overusedConnections.add(new SingleConnection("Writer", writer, true));
                    }
                }
            }
        }

        // Check cinematographers
        for (String cinematographer : previousMovie.getCinematographers()) {
            if (moviesByCinematographer.containsKey(cinematographer)) {
                Set<Movie> moviesWithCinematographer = moviesByCinematographer.get(cinematographer);
                if (moviesWithCinematographer.contains(movie)) {
                    foundAnyConnection = true;
                    int usage = cinematographerUsage.getOrDefault(cinematographer, 0);
                    if (usage < 3) {
                        validConnections.add(new SingleConnection("Cinematographer", cinematographer));
                    } else {
                        overusedConnections.add(new SingleConnection("Cinematographer", cinematographer, true));
                    }
                }
            }
        }

        // Check composers
        for (String composer : previousMovie.getComposers()) {
            if (moviesByComposer.containsKey(composer)) {
                Set<Movie> moviesWithComposer = moviesByComposer.get(composer);
                if (moviesWithComposer.contains(movie)) {
                    foundAnyConnection = true;
                    int usage = composerUsage.getOrDefault(composer, 0);
                    if (usage < 3) {
                        validConnections.add(new SingleConnection("Composer", composer));
                    } else {
                        overusedConnections.add(new SingleConnection("Composer", composer, true));
                    }
                }
            }
        }

        // Store the overused connections to display
        movie.setOverloadedLinks(overusedConnections);

        // Determine if valid
        if (!validConnections.isEmpty()) {
            return MoveResult.success(validConnections, overusedConnections);
        }


        // If no valid connections, BUT we have found a connection, we know it was used too many times
        if (foundAnyConnection) {
            return MoveResult.failure("Connection made too many times", overusedConnections);
        }

        return MoveResult.failure("No valid connection found between movies");
    }



    /**
     * Setter for actor usage
     *
     * @param name name of the actor
     * @param freq frequency with which the actor has been used
     */
    public void setActorUsage(String name, int freq) {
        actorUsage.put(name, freq);
    }

    /**
     * Based on the movie id selected by the user, this method finds the corresponding Movie object from within the hashmap and returns it
     * This is used in conjunction with autocomplete dropdown functionality
     *
     * @param movieId the id of the movie selected by the user
     * @return returns the corresponding Movie object
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
     * Getter for actor hashset
     *
     * @return win condition selected by user
     */
    public HashSet<String> getActorHashSet() {
        return actorSet;
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






