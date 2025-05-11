package com.example.movieGame;

//import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//TODO delete this line - update just for the sake of github updating

/**
 * tracks logic of gameplay
 *
 */
@Controller
public class ControllerClass {
    private GamePlay gamePlay;

    @GetMapping("/")
    public String initialDisplay() {
        return "ViewUI"; // Loads ViewUI.html from templates
    }
    
    @PostMapping("/setup")
    public String handleSetup(
            @RequestParam("player1") String player1,
            @RequestParam("player2") String player2,
            @RequestParam("winCondition") String winCondition,
            HttpSession session,
            Model model
    ) {
        if (player1.isEmpty() || player2.isEmpty() || winCondition.isEmpty()) {
            model.addAttribute("error", "Missing input");
            return "ViewUI"; // You could redirect to an error page if you prefer
        }

        GamePlay gamePlay = new GamePlay(player1, player2);
        gamePlay.setWinCondition(winCondition);
        session.setAttribute("gamePlay", gamePlay); // Store game in session

        return "redirect:/game"; // Redirect to the game view
    }

    @PostMapping("/endGame")
    @ResponseBody
    public Map<String,String> handleEndGame(@RequestParam("reason") String reason, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        Map<String, String> result = new HashMap<>();

        if (gamePlay == null) {
            result.put("error", "No active game");
            return result;
        }

        String winner = "Unknown", loser = "Unknown";

        switch(reason) {
            case "time":
                //set winner as activeplayer (ie the person who ran out of time)
                loser = gamePlay.getActivePlayerName();
                winner = gamePlay.getPlayer1().getUserName();
                if (loser.equals(gamePlay.getPlayer1().getUserName())) {
                    winner = gamePlay.getPlayer2().getUserName();
                }
                break;
            case "winConditionMet":
                //TODO update logic
                //winner = gamePlay.getWinner().getUserName();  // You'd implement this logic
                //loser = gamePlay.getLoser().getUserName();
                break;
            case "movieAlreadyUsed":
                //TODO update logic
                //player submits a duplicate movie
                //winner = gamePlay.getInactivePlayer().getUserName();
                //loser = gamePlay.getActivePlayer().getUserName();
                break;
            case "connectionUsedTooManyTimes":
                //TODO update logic
                //player uses the connection too many times
                //winner = gamePlay.getInactivePlayer().getUserName();
                //loser = gamePlay.getActivePlayer().getUserName();
                break;
            default:
                winner = "Unknown";
                loser = "Unknown";
                break;
        }

        result.put("winner", winner);
        result.put("loser", loser);
        result.put("reason", mapReasonToText(reason));

        return result;
    }

    private String mapReasonToText(String reasonCode) {
        switch (reasonCode) {
            case "time": return "Timer ran out";
            case "winConditionMet": return "Win condition met";
            case "movieAlreadyUsed": return "Movie already used";
            case "connectionUsedTooManyTimes": return "Connection used too many times";
            default: return "Game ended";
        }
    }



    // add another request param for disambiguation (year?)
    // map < str title, movie> title -> title + movie.year
    @PostMapping("/submitMovie")
    @ResponseBody

    public Map<String, Object> handleMovieSubmission(@RequestParam("movieId") int movieId, HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) {
            response.put("status", "error");
            response.put("message","No Active Game");




            return response;
        }

        //find the object using the movieID
        Movie selectedMovie = gamePlay.findTermById(movieId);
        String errorType = "";
        errorType = gamePlay.userEntry(selectedMovie);

        if (selectedMovie == null) {
            response.put("status", "error");
            response.put("message","Movie Not Found");
            return response;
        }

        String result = gamePlay.userEntry(selectedMovie);

        if (result.equals("Win condition met")) {
            response.put("status", "win");
            response.put("winner", gamePlay.getActivePlayerName());
            return response;
        } else if (!result.equals("Valid User Entry")) {
            response.put("status", "error");
            response.put("message", result);
            return response;
        }

        // If successful, return updated game state
        response.put("status", "success");
        response.put("activePlayer", gamePlay.getActivePlayerName());
        response.put("player1Progress", gamePlay.getPlayer1().getProgressTowardWin());
        response.put("player2Progress", gamePlay.getPlayer2().getProgressTowardWin());

        // Include the movie that was just played
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("title", selectedMovie.getMovieTitle());
        movieData.put("releaseYear", selectedMovie.getReleaseYear());
        movieData.put("genre", new ArrayList<>(selectedMovie.getGenre()));

        // Include connections
        List<Map<String, String>> connections = new ArrayList<>();
        if (selectedMovie.getLinksToPreviousMovie() != null) {
            for (SingleConnection connection : selectedMovie.getLinksToPreviousMovie()) {
                Map<String, String> connectionData = new HashMap<>();
                connectionData.put("type", connection.getConnectionType());
                connectionData.put("name", connection.getName());
                connections.add(connectionData);
            }
        }
        movieData.put("connections", connections);
        response.put("submittedMovie", movieData);

        return response;

        //TODO add game update logic as well
        //TODO add logic to handle the errors (errorType) correctly and flow them through to the endGame page



        // get the name of the movie from input
        // search for matching movie in the movies list
        //Movie movie = gamePlay.getMovieFromTitle(movieTitle);
        //
        // verify that it is a valid movie
        //MoveResult newMove = gamePlay.validateMove(movie);
        // validation is handled by gameplay.userEntry
        // if so


        //gamePlay.userEntry(movie);

        // give movie object to gameplay object
        // if not a valid connection, allow a new choice
        //




        //return "OK";
    }


    @GetMapping("/autocomplete")
    @ResponseBody
    public List<Map<String, String>> autocomplete(@RequestParam("query") String query, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) return List.of();

        Autocomplete autocomplete = gamePlay.getAutocomplete();
        List<ITerm> suggestions = autocomplete.getSuggestions(query);

        return suggestions.stream()
                .limit(8)
                .map(term -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(term.getWeight()));  // Movie ID is stored as the weight
                    map.put("term", term.getTerm());  // Movie title
                    return map;
                })
                .toList();
    }




    @GetMapping("/gamestate")
    @ResponseBody
    public GamePlay getGameState(HttpSession session) {
        return (GamePlay) session.getAttribute("gamePlay");
    }

   /* @GetMapping("/gamestate")
    @ResponseBody
    public Map<String, Object> getGameState(HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        Map<String, Object> state = new HashMap<>();

        if (gamePlay == null) {
            state.put("status", "error");
            state.put("message", "No active game");
            return state;
        }

        // Match the property names with what the UI expects
        state.put("activePlayerName", gamePlay.getActivePlayerName());
        state.put("numberOfRounds", gamePlay.getNumberOfRounds());
        state.put("winCondition", gamePlay.getWinCondition());

        state.put("player1", gamePlay.getPlayer1());
        state.put("player2", gamePlay.getPlayer2());

        state.put("firstMovie", gamePlay.getFirstMovie());
        state.put("lastFiveMovies", new ArrayList<>(gamePlay.lastFiveMovies));

        return state;
    }*/

    @GetMapping("/game")
    public String showGameScreen(Model model) {
        //update active player
        /*if (gamePlay.getPlayer1().getIsActive()) {
            //pass attribute back to view
            model.addAttribute("activePlayer", gamePlay.getPlayer1().getUserName());
            //update active status
            gamePlay.getPlayer1().setIsActive(false);
            gamePlay.getPlayer2().setIsActive(true);
        } else {
            //update active status
            gamePlay.getPlayer2().setIsActive(false);
            gamePlay.getPlayer1().setIsActive(true);
        }*/
        //update win conditoin
        //TODO
        //model.addAttribute("winCondition", gamePlay.getWinCondition());

        //update number of rounds
        //TODO
        //model.addAttribute("roundCount", gamePlay.getRoundCount());
        return "ViewUI";
    }
}