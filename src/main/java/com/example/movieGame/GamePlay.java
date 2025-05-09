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
    Queue<Movie> lastFiveMovies; //LinkedList of movie objects showing last 5 (FIFO)
    Movie firstMovie; //first randomly selected movie

    private Timer gameTimer;
    private boolean timerActive;
    private final int maxTimePerTurn = 30;
    private int timeRemaining;

    private Player player1;
    private Player player2;
    private String player1Name;
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
        //TODO (call class here)

        //randomly select movie
        firstMovie = randomMovieSelection();
    }

    /**
     * selects first move for the start of the game from within index
     *
     */
    public Movie randomMovieSelection() {

        try {
            // Check if data is loaded; if not, load it

            if (MovieLoader.createMovieFromFiles() == null || MovieLoader.createMovieFromFiles().isEmpty()) {
                // If data hasn't been loaded yet, load it
                MovieLoader.creditCSVRead();
                MovieLoader.moviesCSVRead();
            }

            List<Movie> availableMovies = MovieLoader.createMovieFromFiles();


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
        //check for a valid connection (return error message if issue)
        //increment xxxConnectionUsage variables for all matching connections
        //TODO

        //check for duplicate movie (return error if issue)
        if (moviesUsed.contains(movie.getMovieID())) {
            return "Error:Movie already used.";
        }

        boolean validConnection = false;
        List<SingleConnection> connections = new ArrayList<>();

        Movie previousMovie = null;
        if (!lastFiveMovies.isEmpty()) {
            previousMovie = ((LinkedList<Movie>) lastFiveMovies).getLast();
        } else if (firstMovie != null) {
            previousMovie = firstMovie;
        } else {
            moviesUsed.add(movie.getMovieID());
            lastFiveMovies.add(movie);
            numberOfRounds ++;

        }


        assert previousMovie != null;
        for (String actor : previousMovie.getActors()) {
            if (movie.getActors().contains(actor)){
                if (actorConnectionUsage >=3) {
                    return "Error: Actor connection used too many times.";
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

    public MoveResult validateMove(Movie movie) {

        if (moviesUsed.contains(movie.getMovieID())) {
            return MoveResult.failure("Movie already used");
        }

        Movie previousMovie = null;



        if (!lastFiveMovies.isEmpty()) {
            previousMovie = ((LinkedList<Movie>) lastFiveMovies).getLast();
        } else if (firstMovie != null) {
            previousMovie = firstMovie;
        } else {
            return MoveResult.failure("no previous movie to connect to");

        }

        SingleConnection validConnection = null;
        for (String actor : previousMovie.getActors()) {
            if (movie.getActors().contains(actor)) {
                int usage = actorUsage.getOrDefault(actor,0);
                if (usage < 3) {
                    validConnection = new SingleConnection("Actor", actor);
                    break;
                }
            }
        }
        if (validConnection == null) {
            // Check directors only if we haven't found a valid connection yet
            for (String director : previousMovie.getDirectors()) {
                if (movie.getDirectors().contains(director)) {
                    int usage = directorUsage.getOrDefault(director, 0);
                    if (usage < 3) {
                        validConnection = new SingleConnection("Director", director);
                        break; // Found a valid connection, no need to check more directors
                    }
                }
            }
        }

        // Still no valid connection? Check writers
        if (validConnection == null) {
            for (String writer : previousMovie.getWriters()) {
                if (movie.getWriters().contains(writer)) {
                    int usage = writerUsage.getOrDefault(writer, 0);
                    if (usage < 3) {
                        validConnection = new SingleConnection("Writer", writer);
                        break;
                    }
                }
            }
        }

        // Still no valid connection? Check cinematographers
        if (validConnection == null) {
            for (String cinematographer : previousMovie.getCinematographers()) {
                if (movie.getCinematographers().contains(cinematographer)) {
                    int usage = cinematographerUsage.getOrDefault(cinematographer, 0);
                    if (usage < 3) {
                        validConnection = new SingleConnection("Cinematographer", cinematographer);
                        break;
                    }
                }
            }
        }

        // Still no valid connection? Check composers
        if (validConnection == null) {
            for (String composer : previousMovie.getComposers()) {
                if (movie.getComposers().contains(composer)) {
                    int usage = composerUsage.getOrDefault(composer, 0);
                    if (usage < 3) {
                        validConnection = new SingleConnection("Composer", composer);
                        break;
                    }
                }
            }
        }

        // If we didn't find any valid connection
        if (validConnection == null) {
            return MoveResult.failure("No valid connection found between movies");
        }

        // We found a valid connection
        List<SingleConnection> connections = new ArrayList<>();
        connections.add(validConnection);
        return MoveResult.success(connections);



    }

    public void setActorUsage(String name, int freq) {

        actorUsage.put(name,freq);
    }


    /**
     *
     *
     */

    //TODO need getters
}


