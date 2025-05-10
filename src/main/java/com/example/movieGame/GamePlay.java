package com.example.movieGame;

import java.util.*;


public class GamePlay
{
    ArrayList<Integer> moviesUsed; //list of movie IDs ot track if it's been used before

    private int actorConnectionUsage; //# of times an actor connection has been made;
    private int directorConnectionUsage; //# of times a director connection has been made;
    private int writerConnectionUsage; //# of times a writer connection has been made;
    private int cinematographerConnectionUsage; //# of times a cinematographer connection has been made;
    private int composerConnectionUsage; //# of times a composer connection has been made;

    private Map<String, Integer> actorUsage = new HashMap<>();      // Maps actor name to usage count
    private Map<String, Integer> directorUsage = new HashMap<>();   // Maps director name to usage count
    private Map<String, Integer> writerUsage = new HashMap<>();     // Maps writer name to usage count
    private Map<String, Integer> cinematographerUsage = new HashMap<>(); // Maps cinematographer to usage count
    private Map<String, Integer> composerUsage = new HashMap<>();   // Maps composer name to usage count

    private int numberOfRounds; //tracks # of rounds played (for display)

    //Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    //Movie firstMovie; //first randomly selected movie

    private Timer gameTimer;
    private boolean timerActive;
    private final int maxTimePerTurn = 30;
    private int timeRemaining;

    Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
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
     *
     * tracks time for each player
     *
     */
    public void thirtySecondTimerStart() {
        //TODO
        //not sure what it should return/ how this would work

        if (gameTimer != null) {
            gameTimer.cancel();
        }

        gameTimer = new Timer();
        timerActive = true;
        timeRemaining = maxTimePerTurn;



    }

    /**
     * updates game state and variables based on user entry
     *
     * @return error message citing reason for the error
     * @parameter
     */
    public String userEntry(Movie movie) {

        // First validate the move
        MoveResult result = validateMove(movie);

        if (!result.isValid()) {
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

            // Choose one connection to update usage counters
            // In the game, we only need one valid connection, even though we display all
            SingleConnection primaryConnection = result.getConnections().get(0);

            // Update the appropriate usage counter for the primary connection
            String type = primaryConnection.getConnectionType();
            String name = primaryConnection.getName();

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

            // Store all valid connections for display
            movie.linksToPreviousMovie.addAll(result.getConnections());
        }

        // Add movie to used list
        moviesUsed.add(movie.getMovieID());

        // Increment number of rounds played
        numberOfRounds++;

        // Add to last five movies and remove oldest if needed
        lastFiveMovies.add(movie);
        if (lastFiveMovies.size() > 5) {
            lastFiveMovies.poll();
        }

        // Switch active player
        player1.setIsActive(!player1.getIsActive());
        player2.setIsActive(!player2.getIsActive());

        return "Valid User Entry";
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

        // We'll collect all valid connections
        List<SingleConnection> validConnections = new ArrayList<>();

        // Check actors
        for (String actor : previousMovie.getActors()) {
            if (movie.getActors().contains(actor)) {
                int usage = actorUsage.getOrDefault(actor, 0);
                if (usage < 3) {
                    // Found a valid actor connection that hasn't been used 3 times
                    validConnections.add(new SingleConnection("Actor", actor));
                }
            }
        }

        // Check directors
        for (String director : previousMovie.getDirectors()) {
            if (movie.getDirectors().contains(director)) {
                int usage = directorUsage.getOrDefault(director, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Director", director));
                }
            }
        }

        // Check writers
        for (String writer : previousMovie.getWriters()) {
            if (movie.getWriters().contains(writer)) {
                int usage = writerUsage.getOrDefault(writer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Writer", writer));
                }
            }
        }

        // Check cinematographers
        for (String cinematographer : previousMovie.getCinematographers()) {
            if (movie.getCinematographers().contains(cinematographer)) {
                int usage = cinematographerUsage.getOrDefault(cinematographer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Cinematographer", cinematographer));
                }
            }
        }

        // Check composers
        for (String composer : previousMovie.getComposers()) {
            if (movie.getComposers().contains(composer)) {
                int usage = composerUsage.getOrDefault(composer, 0);
                if (usage < 3) {
                    validConnections.add(new SingleConnection("Composer", composer));
                }
            }
        }

        // If we didn't find any valid connections
        if (validConnections.isEmpty()) {
            return MoveResult.failure("No valid connection found between movies");
        }

        // We found at least one valid connection
        return MoveResult.success(validConnections);


    }

    public void setActorUsage(String name, int freq) {

        actorUsage.put(name,freq);
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


