package main.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit Testing class, tests some of the games functions
 * and ensures everything is working as expected
 *
 * @author Ethan Chase
 * @author Kyler Verge
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class GameSingletonTest {

    /**
     * Single instance of the game model
     */
    private GameSingleton gsm;

    /**
     * List of players in the game
     */
    List<Player> players;

    /**
     * Sets up list of players in a single Game of RISK in order
     * to test methods in the Game model class.
     */
    @Before
    public void setUp(){
        players = new ArrayList<>();
        players.add(new Player("Ethan", RiskColour.RED));
        players.add(new Player("Anthony", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.YELLOW));
        players.add(new Player("Kyler",RiskColour.BLACK));
        gsm = GameSingleton.getGameInstance(players);
    }

    /**
     * Test getNumActivePlayer():
     *
     * Check how many players are not yet eliminated from the game
     * through getNumActivePlayer() in GameSingleton.java
     */
    @Test
    public void testGetNumActivePlayer() {
        gsm.setUpGame();
        assertEquals(4, gsm.getNumActivePlayer());
    }

    /**
     * Test getMaxBattleDie()
     *
     * Calls getMaxBattleDie() in GameSingleton.java to check if it
     * returns the maximum amount of dice the attacker/defender can roll
     * based on the number of units used in the attack sequence.
     */
    @Test
    public void testGetMaxBattleDie(){
        //Attacking, 3 Units -> 2 Dice
        assertEquals(2, gsm.getMaxBattleDie(3,true));
        //Attacking 4 Units -> 3 Dice
        assertEquals(3,gsm.getMaxBattleDie(4,true));
        //Attacking 2 Units -> 1 Dice
        assertEquals(1,gsm.getMaxBattleDie(2,true));
        //Defending 2 Units -> 1 Dice
        assertEquals(1,gsm.getMaxBattleDie(2,false));
        //Defending 3 Units -> 2 Dice
        assertEquals(2,gsm.getMaxBattleDie(3,false));
    }

    /**
     * Test fortifyPosition() in GameSingleton class
     *
     * Creates two territories with different names, owners and number of units.
     * These territories are passed as parameters to fortifyPosition() in
     * GameSingleton.java with the number of units to move. Checks
     * if the units are moved properly.
     */
    @Test
    public void testFortifyPosition(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);

        Territory t1 = new Territory("Earth");
        t1.setUnits(4);
        assertEquals(4, t1.getUnits());
        t1.setOwner(ethan);
        assertEquals(ethan, t1.getOwner());

        Territory t2 = new Territory("Pluto");
        t2.setUnits(5);
        assertEquals(5, t2.getUnits());
        t2.setOwner(anthony);
        assertEquals(anthony, t2.getOwner());

        gsm.fortifyPosition(t1,t2,2);

        assertEquals("Earth",t1.getName());
        assertEquals("Pluto",t2.getName());
        assertEquals(ethan,t1.getOwner());
        assertEquals(ethan,t2.getOwner());
        assertEquals(2,t1.getUnits());
        assertEquals(7,t2.getUnits());
    }

    /**
     * Test rollDice() in GameSingleton class
     *
     * Checks that when a set of dice are rolled, that the results
     * are within and including 1 and 6.
     */
    @Test
    public void testRollDie(){
        int[] theRolls = gsm.rollDice(3);
        assertEquals(3, theRolls.length);
        for(int i=0;i<3;i++){
            assertTrue(theRolls[i] > 0);
            assertTrue(theRolls[i] < 7);
        }
    }

    /**
     * Test updateNumActivePlayer() in GameSingleton class
     *
     * Creates two territories with different names, owners and units. Only
     * two of the four total players own territories, so the getNumActivePlayer()
     * should return the proper amount.
     */
    @Test
    public void testUpdateNumActivePlayer(){
        Player ethan = players.get(0);
        Player anthony = players.get(1);

        gsm.setNumActivePlayer(players.size());

        Territory t1 = new Territory("Earth");
        t1.setUnits(4);
        assertEquals(4, t1.getUnits());
        t1.setOwner(ethan);
        assertEquals(ethan, t1.getOwner());

        Territory t2 = new Territory("Pluto");
        t2.setUnits(5);
        assertEquals(5, t2.getUnits());
        t2.setOwner(anthony);
        assertEquals(anthony, t2.getOwner());

        gsm.updateNumActivePlayers();
        assertEquals(2, gsm.getNumActivePlayer());

        /* Remove territory from one of the two active players
        Now check if only one player is left.
         */
        anthony.removeTerritory(t2);
        gsm.updateNumActivePlayers();
        assertEquals(false,anthony.isActive());
        assertEquals(1, gsm.getNumActivePlayer());
    }

    /**
     * Test nextPlayer() method in GameSingleton class
     */
    @Test
    public void testNextPlayer(){
        //Four Player Game
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        Player kyler = players.get(3);

        //Shuffles Player Order
        gsm.setUpGame();

        //Test 1, First Player -> Second Player
        Player first = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(first,gsm.getCurrentPlayer());

        //Test 2 Second Player -> Third Player
        Player second = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(second,gsm.getCurrentPlayer());

        //Test 3 Third Player -> Fourth Player
        Player third = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(third,gsm.getCurrentPlayer());

        //Test 4 Fourth Player -> First Player
        Player fourth = gsm.getCurrentPlayer();
        gsm.nextPlayer();
        assertNotEquals(fourth,gsm.getCurrentPlayer());

        //Test 5, First Player IS the First Player (Looped through all players)
        assertEquals(first,gsm.getCurrentPlayer());
    }
}