package com.example.admin.doppelkopfapp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Admin on 12/07/2017.
 */

public class GameManagerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionNextRoundSumNotZero() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameManager gameManager = new GameManager(0, players, null);

        gameManager.nextRound( new int[]{2, 23, 4, -32}, 2, false);
    }

    @Test
    public void activePlayersLengthIs4() {
        Player[] players4 =  new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameManager gameManager4 = new GameManager(0, players4, null);

        Player[] players5 =  new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4"), new Player(0, "6")};
        GameManager gameManager5 = new GameManager(0, players5, null);

        Player[] players6 =  new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4"), new Player(0, "6"), new Player(0, "6")};
        GameManager gameManager6 = new GameManager(0, players6, null);

        assertThat(gameManager4.getActivePlayers().length, is(4));
        assertThat(gameManager5.getActivePlayers().length, is(4));
        assertThat(gameManager6.getActivePlayers().length, is(4));
    }


    @Test
    public void nextRoundGiverTwice() {
        Player[] players =  new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameSettings settings = new GameSettings(0, 0, false, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        for( int i = 0; i < 2; i++ )
            gameManager.nextRound(new int[]{-1, -1, 1, 1}, 0, false);

        assertThat( gameManager.getGiver(), is( equalTo(players[2]) ) );
        assertThat( gameManager.getGiverIndex(), is(2) );
    }


    @Test
    public void nextRoundGiverEightTimes() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4"), new Player(0, "6"), new Player(0, "6")};
        GameSettings settings = new GameSettings(0, 0, false, false, false);
        GameManager gameManager = new GameManager(0, players, settings);
        for( int i = 0; i < 8; i++ ) {
            gameManager.nextRound(new int[]{1, -1, 1, -1}, 0, false);
        }
        assertThat( gameManager.getGiver().getName(), is( equalTo( players[2].getName() ) ) );
        assertThat( gameManager.getGiverIndex(), is(2) );
    }

    @Test
    public void getActivePlayersFive() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4"), new Player(0, "6"), new Player(0, "6")};
        GameSettings settings = new GameSettings(0, 15, false, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        gameManager.nextRound(new int[]{1, -1, 1, -1}, 0, false);

        assertThat( gameManager.getActivePlayers().length, is(4) );
    }

    @Test
    public void getMoney() {
        Player[] players =  new Player[]{new Player(0, "1"), new Player(0, "2")};
        GameSettings settings = new GameSettings(0, 15, false, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        players[0].addPoints(-20);
        players[0].addPoints(1);
        players[1].addPoints(100);
        players[1].addPoints(1);

        assertThat(gameManager.getMoney(players[0]), is(300));
        assertThat(gameManager.getMoney(players[1]), is(0));
    }

    @Test
    public void testPointsBock() {
        Player[] players =  new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameSettings settings = new GameSettings(0, 0, true, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        //12 bocks
        gameManager.nextRound(new int[]{1, 1, -1, -1}, 2, true); // 1
        gameManager.nextRound(new int[]{1, 1, -1, -1}, 1, true); // 2
        for( int i = 0; i < 10; i++)
            gameManager.nextRound(new int[]{1, 1, -1, -1}, 0, true); // 10 x 2

        assertThat(gameManager.getPlayers()[0].getPoints(), is(23));
        assertThat(gameManager.getPlayers()[2].getPoints(), is(-23));
        assertThat(gameManager.getPlayers()[3].getPointsLost(), is(23));
        assertThat(gameManager.getBocks(), is(1));
    }

    @Test
    public void testPointsDoubleBock() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4") };
        GameSettings settings = new GameSettings(0, 10, true, true, false);
        GameManager gameManager = new GameManager(0, players, settings);

        for( int i = 0; i < 2; i++ )
            gameManager.nextRound(new int[]{1, 1, -1, -1}, 1, false);
        for( int i = 0; i < 3; i++ )
            gameManager.nextRound(new int[]{2, 2, -2, -2}, 0, false);

        assertThat(gameManager.getBocks(), is(1));
        assertThat(gameManager.getDoubleBocks(), is(0));
        assertThat(gameManager.getPlayers()[0].getPoints(), is(27));
    }

    @Test
    public void testPointsSoloBockCalculationEnabled() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameSettings settings = new GameSettings(0, 10, true, false, true);
        GameManager gameManager = new GameManager(0, players, settings);

        gameManager.nextRound(new int[]{3, -1, -1, -1}, 1, false);
        for( int i = 0; i < 4; i++ )
            gameManager.nextRound(new int[]{3, -1, -1, -1}, 0, false);

        assertThat(gameManager.getBocks(), is(0));
        assertThat(gameManager.getPlayers()[0].getPoints(), is(27) );
        assertThat(gameManager.getPlayers()[1].getPoints(), is(-9) );
        assertThat(gameManager.getPlayers()[2].getPointsLost(), is(9));
    }


    @Test
    public void testPointsSoloBockCalculationDisabled() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4")};
        GameSettings settings = new GameSettings(0, 10, true, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        gameManager.nextRound(new int[]{3, -1, -1, -1}, 1, false);
        for( int i = 0; i < 3; i++ )
            gameManager.nextRound(new int[]{3, -1, -1, -1}, 0, false);
        gameManager.nextRound(new int[]{3, 3, -3, -3}, 0, false);

        assertThat(gameManager.getBocks(), is(3));
        assertThat(gameManager.getPlayers()[0].getPoints(), is(18) );
        assertThat(gameManager.getPlayers()[1].getPoints(), is(2));
        assertThat(gameManager.getPlayers()[3].getPoints(), is(-10) );
    }

    @Test
    public void skipRound() {
        Player[] players = new Player[]{new Player(0, "1"), new Player(0, "2"), new Player(0, "3"), new Player(0, "4"), new Player(0, "6"), new Player(0, "6")};
        GameSettings settings = new GameSettings(0, 0, false, false, false);
        GameManager gameManager = new GameManager(0, players, settings);

        gameManager.nextRound(new int[]{1, 1, -1, -1}, 0, false);
        for( int i = 0; i < 4; i++ )
            gameManager.skipRound();

        assertThat(gameManager.getGiverIndex(), is(5));
    }
}