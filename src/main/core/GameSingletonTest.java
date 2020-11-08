package main.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameSingletonTest {

    private GameSingleton gsm;

    List<Player> players;

    @Before
    public void setUp(){
        players = new ArrayList<>();
        players.add(new Player("Ethan", RiskColour.RED));
        players.add(new Player("Anthony", RiskColour.BLUE));
        players.add(new Player("Tony", RiskColour.YELLOW));
        gsm = GameSingleton.getGameInstance(players);
    }

    /**
     * Test getNumActivePlayer():
     *
     * Check how many players not yet eliminated in the game.
     */
    @Test
    public void getNumActivePlayerStatus() {
        gsm.setUpGame();
        assertEquals(3, gsm.getNumActivePlayer());
    }

    /**
     * Test getMaxBattleDie()
     */
    @Test
    public void getMaxBattleDieStatus(){
        assertEquals(2, gsm.getMaxBattleDie(3,true));
    }

    /**
     * Test getColour() in Player class
     *
     * NOTE: In order to test getColour from the Player class, you
     * have to run getColoursTest() only. Running the entire
     * GameSingletonTest.java won't work.
     */
    @Test
    public void getColoursTest(){
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        gsm.setUpGame();
        assertEquals(RiskColour.RED, ethan.getColour());
        assertEquals(RiskColour.BLUE, anthony.getColour());
        assertEquals(RiskColour.YELLOW, tony.getColour());
    }

    /**
     * Test getName() in Player class
     *
     * NOTE: In order to test getName from the Player class, you
     * have to run getNameTest() only. Running the entire
     * GameSingletonTest.java won't work.
     */
    @Test
    public void getNameTest(){
        //NOTE: Test passes if order of players is not shuffled in setUpGame()
        Player ethan = players.get(0);
        Player anthony = players.get(1);
        Player tony = players.get(2);
        gsm.setUpGame();
        assertEquals("Ethan", ethan.getName());
        assertEquals("Anthony", anthony.getName());
        assertEquals("Tony", tony.getName());
    }

    /**
     * Test fortifyPosition() in GameSingleton class
     *
     * NOTE: In order to test fortifyPosition(), we need to check
     * whether the method works through access to the number of units
     * in the Territory class.
     * Therefore, testFortifyPosition() must be run separately to
     * succeed. Running the entire GameSingletonTest.java won't work.
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

        assertEquals(ethan,t1.getOwner());
        assertEquals(ethan,t2.getOwner());
        assertEquals(2,t1.getUnits());
        assertEquals(7,t2.getUnits());
    }
}