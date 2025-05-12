package com.example.movieGame;

//import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Tracks logic of gameplay. Handles actions that trigger changes to the UI
 * Created using ChatGPT prompting for help managing SpringBoot
 *
 */
@Controller
public class ControllerClass {
    private GamePlay gamePlay;

    /**
     * Loads the initial display for the user
     *
     */
    @GetMapping("/")
    public String initialDisplay() {
        return "ViewUI"; // Loads ViewUI.html from templates
    }

    /**
     * Initiates game setup
     * Takes in the players' names and allows the user to select the win condition
     *
     */
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
            return "ViewUI";
        }

        GamePlay gamePlay = new GamePlay(player1, player2);
        gamePlay.setWinCondition(winCondition);
        session.setAttribute("gamePlay", gamePlay); // Store game in session

        return "redirect:/game"; // Redirect to the game view
    }

    /**
     * Shows the end game screen after the timer runs out
     * @param reason reason for the game ending
     * @param session game session
     *
     */
    @PostMapping("/endGame")
    @ResponseBody
    public Map<String, String> handleEndGame(@RequestParam("reason") String reason, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        Map<String, String> result = new HashMap<>();

        if (gamePlay == null) {
            result.put("error", "No active game");
            return result;
        }

        String winner = "Unknown";
        String loser = "Unknown";

        //if time runs out, show the end game screen
        switch (reason) {
            case "time":
                loser = gamePlay.getActivePlayerName();
                winner = gamePlay.getPlayer1().getUserName();
                if (loser.equals(winner)) {
                    winner = gamePlay.getPlayer2().getUserName();
                }
                break;
            default:
                break;
        }

        //capture the winner and loser
        result.put("winner", winner);
        result.put("loser", loser);
        result.put("reason", "Timer ran out");

        return result;
    }


    /**
     * Tracks logic of gameplay. Handles actions that trigger changes to the UI
     * Created using ChatGPT prompting for help managing SpringBoot
     *
     */
    @PostMapping("/submitMovie")
    @ResponseBody
    public Map<String, Object> handleMovieSubmission(@RequestParam("movieId") int movieId, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        Map<String, Object> response = new HashMap<>();

        if (gamePlay == null) {
            response.put("error", "No active game");
            return response;
        }

        Movie selectedMovie = gamePlay.findTermById(movieId);
        String message = gamePlay.userEntry(selectedMovie);

        //take actions based on whether the entry is valid or invalid
        switch (message) {
            //if the movie has been used, show a red error message and let the user keep guessing
            case "Movie already used" -> {
                response.put("resultScreen", true);
                response.put("reason", "Movie already used");
                findWinnerAndLoser(response, gamePlay);
            }
            //if no previous movie to connect to, show a red error message and let the user keep guessing
            case "No previous movie to connect to" -> {
                response.put("resultScreen", true);
                response.put("reason", "No previous movie to connect to");
                findWinnerAndLoser(response, gamePlay);
            }
            //if no valid connections ahve been found between movies, show a red error message and let the user keep guessing
            case "No valid connection found between movies" -> {
                response.put("resultScreen", true);
                response.put("reason", "No valid connection found between movies");
                findWinnerAndLoser(response, gamePlay);
            }
            //if the connection has been made too many times, show a red error message and let the user keep guessing
            case "Connection made too many times" -> {
                response.put("resultScreen", true);
                response.put("reason", "Connection made too many times");
                response.put("overloadedLinks", selectedMovie.getOverloadedLinks());
                findWinnerAndLoser(response, gamePlay);
            }
            //if the user entry is valid, update the screen
            case "Valid User Entry" -> {
                response.put("resultScreen", false); // No need to show result screen
                response.put("message", "Valid connection");
            }
            //if the win condition has been met, show the end game screen
            case "Win condition met" -> {
                response.put("resultScreen", true);
                response.put("reason", "Win condition met");

                String winner = gamePlay.getActivePlayer().getUserName();
                String loser = winner.equals(gamePlay.getPlayer1().getUserName())
                        ? gamePlay.getPlayer2().getUserName()
                        : gamePlay.getPlayer1().getUserName();

                response.put("winner", winner);
                response.put("loser", loser);
            }
            default -> {
                response.put("error", "Unknown message");
            }
        }
        return response;
    }

    /**
     * Helper function to identify whether player 1 or 2 is the winner/loser
     *
     * @param response a Map to capture the winner/loser
     * @param gamePlay the active gameplay object
     */
    private void findWinnerAndLoser(Map<String, Object> response, GamePlay gamePlay) {
        String active = gamePlay.getActivePlayerName();
        if (active.equals(gamePlay.getPlayer1().getUserName())) {
            response.put("winner", gamePlay.getPlayer2().getUserName());
            response.put("loser", gamePlay.getPlayer1().getUserName());
        } else {
            response.put("winner", gamePlay.getPlayer1().getUserName());
            response.put("loser", gamePlay.getPlayer2().getUserName());
        }
    }

    /**
     * Displays the dropdown of options
     *
     * @param query the user's typed query/prefix
     * @param session gameplay http session
     * @return returns the list of suggested autocomplete options
     *
     */
    @GetMapping("/autocomplete")
    @ResponseBody
    public List<Map<String, String>> autocomplete(@RequestParam("query") String query, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) return List.of();

        //get suggestions
        Autocomplete autocomplete = gamePlay.getAutocomplete();
        List<ITerm> suggestions = autocomplete.getSuggestions(query);

        //return the suggestions in the dropdown
        return suggestions.stream()
                .limit(8)
                .map(term -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(term.getWeight()));  // assuming ITerm has getId()
                    map.put("term", term.getTerm());
                    return map;
                })
                .toList();
    }


    /**
     * Returns the current state of the game stored in the user's session
     * @param session gameplay http session
     * @return gameplay object
     *
     */
    @GetMapping("/gamestate")
    @ResponseBody
    public GamePlay getGameState(HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        return gamePlay;
    }

    /**
     * Returns the view for the game screen
     *
     * @param model Spring Framework interface acting as a container for data
     * @return the view name
     */
    @GetMapping("/game")
    public String showGameScreen(Model model) {
        return "ViewUI";
    }
}