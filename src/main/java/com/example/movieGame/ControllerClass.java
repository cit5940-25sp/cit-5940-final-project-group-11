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
    public Map<String, String> handleEndGame(@RequestParam("reason") String reason, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        Map<String, String> result = new HashMap<>();

        if (gamePlay == null) {
            result.put("error", "No active game");
            return result;
        }

        String winner = "Unknown";
        String loser = "Unknown";

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

        result.put("winner", winner);
        result.put("loser", loser);
        result.put("reason", "Timer ran out");

        return result;
    }





    // add another request param for disambiguation (year?)
    // map < str title, movie> title -> title + movie.year
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

        switch (message) {
            case "Movie already used" -> {
                response.put("resultScreen", true);
                response.put("reason", "Movie already used");
                findWinnerAndLoser(response, gamePlay);
            }
            case "No previous movie to connect to" -> {
                response.put("resultScreen", true);
                response.put("reason", "No previous movie to connect to");
                findWinnerAndLoser(response, gamePlay);
            }
            case "No valid connection found between movies" -> {
                response.put("resultScreen", true);
                response.put("reason", "No valid connection found between movies");
                findWinnerAndLoser(response, gamePlay);
            }
            case "Connection made too many times" -> {
                response.put("resultScreen", true);
                response.put("reason", "Connection made too many times");
                response.put("overloadedLinks", selectedMovie.getOverloadedLinks());
                //String test = selectedMovie.getOverloadedLinks().toString();
                //response.put("overloaded", test);

                findWinnerAndLoser(response, gamePlay);
            }
            case "Valid User Entry" -> {
                response.put("resultScreen", false); // No need to show result screen
                response.put("message", "Valid connection");
            }
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

    public void findWinnerAndLoser(Map<String, Object> response, GamePlay gamePlay) {
        String active = gamePlay.getActivePlayerName();
        if (active.equals(gamePlay.getPlayer1().getUserName())) {
            response.put("winner", gamePlay.getPlayer2().getUserName());
            response.put("loser", gamePlay.getPlayer1().getUserName());
        } else {
            response.put("winner", gamePlay.getPlayer1().getUserName());
            response.put("loser", gamePlay.getPlayer2().getUserName());
        }
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
                    map.put("id", String.valueOf(term.getWeight()));  // assuming ITerm has getId()
                    map.put("term", term.getTerm());
                    return map;
                })
                .toList();
    }




    @GetMapping("/gamestate")
    @ResponseBody
    public GamePlay getGameState(HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        return gamePlay;  // assuming GamePlay has getters for player1/player2
    }
    @GetMapping("/game")
    public String showGameScreen(Model model) {
        return "ViewUI";
    }
}