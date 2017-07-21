package com.example.admin.doppelkopfapp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 12/07/2017.
 */

public class GameManagerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionNextRoundSumNotZero() {
        Player[] players = new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4")};
        GameManager gameManager = new GameManager(players, null);

        gameManager.nextRound( new int[]{2, 23, 4, -32}, 2, false);
    }

    @Test
    public void activePlayersLengthIs4() {
        Player[] players4 =  new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4")};
        GameManager gameManager4 = new GameManager(players4, null);

        Player[] players5 =  new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4"), new Player("5")};
        GameManager gameManager5 = new GameManager(players5, null);

        Player[] players6 =  new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4"), new Player("5"), new Player("6")};
        GameManager gameManager6 = new GameManager(players6, null);

        assertThat(gameManager4.getActivePlayers().length, is(4));
        assertThat(gameManager5.getActivePlayers().length, is(4));
        assertThat(gameManager6.getActivePlayers().length, is(4));
    }


    @Test
    public void nextRoundGiverTwice() {
        Player[] players =  new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4")};
        GameSettings settings = new GameSettings(0, false, false, false);
        GameManager gameManager = new GameManager(players, settings);

        for( int i = 0; i < 2; i++ )
            gameManager.nextRound(new int[]{-1, -1, 1, 1}, 0, false);

        assertThat( gameManager.getGiver(), is( equalTo(players[2]) ) );
        assertThat( gameManager.getGiverIndex(), is(2) );
    }


    @Test
    public void nextRoundGiverEightTimes() {
        Player[] players = new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4"), new Player("5"), new Player("6")};
        GameSettings settings = new GameSettings(0, false, false, false);
        GameManager gameManager = new GameManager(players, settings);
        for( int i = 0; i < 8; i++ ) {
            gameManager.nextRound(new int[]{1, -1, 1, -1}, 0, false);
        }
        assertThat( gameManager.getGiver(), is( equalTo( players[2] ) ) );
        assertThat( gameManager.getGiverIndex(), is(2) );
    }

    @Test
    public void getActivePlayersFive() {
        Player[] players = new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4"), new Player("5"), new Player("6")};
        GameSettings settings = new GameSettings(15, false, false, false);
        GameManager gameManager = new GameManager(players, settings);

        gameManager.nextRound(new int[]{1, -1, 1, -1}, 0, false);

        assertThat( gameManager.getActivePlayers().length, is(4) );
    }

    @Test
    public void getMoney() {
        Player[] players =  new Player[]{new Player("1"), new Player("2")};
        GameSettings settings = new GameSettings(15, false, false, false);
        GameManager gameManager = new GameManager(players, settings);

        players[0].addPoints(-20);
        players[0].addPoints(1);
        players[1].addPoints(100);
        players[1].addPoints(1);

        assertThat(gameManager.getMoney(players[0]), is(300));
        assertThat(gameManager.getMoney(players[1]), is(0));
    }

    @Test
    public void testPointsBock() {
        Player[] players =  new Player[]{new Player("1"), new Player("2"), new Player("3"), new Player("4")};
        GameSettings settings = new GameSettings(0, true, false, false);
        GameManager gameManager = new GameManager(players, settings);

        //12 bocks
        gameManager.nextRound(new int[]{1, 1, -1, -1}, 2, false); // 1
        gameManager.nextRound(new int[]{1, 1, -1, -1}, 1, false); // 2
        for( int i = 0; i < 10; i++)
            gameManager.nextRound(new int[]{1, 1, -1, -1}, 1, false); // 10 x 2

        assertThat(gameManager.getPlayers()[0].getPoints(), is(23));
        assertThat(gameManager.getPlayers()[2].getPoints(), is(-23));
        assertThat(gameManager.getPlayers()[3].getPointsLost(), is(23));
        assertThat(gameManager.getBocks(), is(1));
    }

    @Test
    public void testPointsDoubleBock() {
        //// TODO: 19/07/2017
    }

    @Test
    public void testPointsSoloBockCalculation() {
        // // TODO: 19/07/2017
    }


}
