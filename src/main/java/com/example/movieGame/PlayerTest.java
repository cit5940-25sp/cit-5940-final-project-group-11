package com.example.movieGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Player class
 *
 */
public class PlayerTest {

    //Tests that the constructor correctly creates a Player object
    //Tests the getUserName method
    //Tests getIsActive and getIsInactive
    @Test
    public void testConstructorInitializesCorrectly() {
        Player player = new Player("A", true, false);
        assertEquals("A", player.getUserName());
        assertEquals(0, player.getProgressTowardWin());
        assertTrue(player.getIsActive());
        assertFalse(player.getIsInactive());
    }

    //Test setProgressTowardsWin properly increments the progress
    //Tests getProgressTowardsWin
    @Test
    public void testSetProgressTowardWinIncrements() {
        Player player = new Player("A", true, false);
        player.setProgressTowardWin();
        assertEquals(1, player.getProgressTowardWin());

        player.setProgressTowardWin();
        assertEquals(2, player.getProgressTowardWin());
    }

    //Tests the setter and getter for activeStatus
    @Test
    public void testSetIsActive() {
        Player player = new Player("Charlie", false, true);
        assertFalse(player.getIsActive());
        player.setIsActive(true);
        assertTrue(player.getIsActive());
    }

    //Tests the setter and getter for inactiveStatus
    @Test
    public void testSetIsInactive() {
        Player player = new Player("Dana", true, false);
        assertFalse(player.getIsInactive());
        player.setIsInactive(true);
        assertTrue(player.getIsInactive());
    }

}
