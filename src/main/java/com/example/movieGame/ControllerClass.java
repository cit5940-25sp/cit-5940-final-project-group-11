package com.example.movieGame;

//import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        session.setAttribute("gamePlay", gamePlay); // Store game in session

        return "redirect:/game"; // Redirect to the game view
    }

    @GetMapping("/gamestate")
    @ResponseBody
    public GamePlay getGameState() {
        return gamePlay;
    }
    @GetMapping("/game")
    public String showGameScreen(Model model) {
        //update active player
        if (gamePlay.getPlayer1().getIsActive()) {
            //pass attribute back to view
            model.addAttribute("activePlayer", gamePlay.getPlayer1().getUserName());
            //update active status
            gamePlay.getPlayer1().setIsActive(false);
            gamePlay.getPlayer2().setIsActive(true);
        } else {
            //update active status
            gamePlay.getPlayer2().setIsActive(false);
            gamePlay.getPlayer1().setIsActive(true);
        }
        //update win conditoin
        //TODO
        //model.addAttribute("winCondition", gamePlay.getWinCondition());

        //update number of rounds
        //TODO
        //model.addAttribute("roundCount", gamePlay.getRoundCount());
        return "ViewUI";
    }
}