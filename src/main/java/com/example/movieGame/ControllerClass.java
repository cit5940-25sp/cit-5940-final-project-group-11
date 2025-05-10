package com.example.movieGame;

//import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/timeUp")
    @ResponseBody
    public ResponseEntity<String> handleTimeUp(HttpSession session) {
        GamePlay gamePlay = (GamePlay) session.getAttribute("gamePlay");
        if (gamePlay == null) {
            return ResponseEntity.badRequest().body("No active game");
        }

        // You can modify the logic here: TODO: update this to add logic for what to do when timer runs out
        //gamePlay.incrementRound();  // assuming such a method exists
        //gamePlay.switchPlayer();    // assuming you track active player

        return ResponseEntity.ok("Time processed");
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