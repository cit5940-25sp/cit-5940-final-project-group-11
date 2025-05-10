package com.example.movieGame;

//import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String handleMovieSubmission(@RequestParam("movie") String movieTitle, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) {
            return "No active game";
        }

        //THIS COLLECTS THE MOVIE FROM THE USER'S ENTRY
        //TODO use a setter method in GamePlay to update the movie
        // need a way to take the string entered by the user and find the right movie object

        // TODO: Logic to validate the movie, update game state, check win conditions, etc.


        // get the name of the movie from input
        // search for matching movie in the movies list
        Movie movie = gamePlay.getMovieFromTitle(movieTitle);
        //
        // verify that it is a valid movie
        //MoveResult newMove = gamePlay.validateMove(movie);
        // validation is handled by gameplay.userEntry
        // if so


        gamePlay.userEntry(movie);

        // give movie object to gameplay object
        // if not a valid connection, allow a new choice
        //

        return "OK";
    }


    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam("query") String query, HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) return List.of();

        Autocomplete autocomplete = gamePlay.getAutocomplete(); // adjust as needed
        List<ITerm> suggestions = autocomplete.getSuggestions(query);

        int k = 8;
        return suggestions.stream()
                .limit(k)
                .map(ITerm::getTerm) // or getQuery(), etc.
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