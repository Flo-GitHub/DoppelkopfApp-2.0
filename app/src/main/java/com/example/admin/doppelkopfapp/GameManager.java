package com.example.admin.doppelkopfapp;

import java.io.Serializable;

/**
 * Central class that has got all the data from the app (Players, Settings, Bocks).
 * Already tested really well.
 */

public class GameManager implements Serializable {

    private final int BOCKS_TO_ADD;

    private Player[] players;
    private int giverIndex = 0;
    private int bocks = 0,
                doubleBocks = 0;
    private GameSettings settings;
    private long databaseId = -1;

    public GameManager( Player[] players, GameSettings settings ) {
        this.players = players;
        this.settings = settings;

        BOCKS_TO_ADD = players.length;
    }


    public void skipRound() {
        nextGiverIndex();
    }

    public void nextRound(int[] points, int bocks, boolean repeatRound) {
        if( !isValidRound(points) )
            throw new IllegalArgumentException("Sum of the points not equal 0." );
        else if( points.length != 4 )
            throw new IllegalArgumentException("Length of points should be 4, but was "+points.length);

        int factorToIncrease;
        if( !settings.isSoloBockCalculation() && isSolo(points) )
            factorToIncrease = 1;
        else if( settings.isDoubleBock() && doubleBocks > 0 ) {
            factorToIncrease = 4;
            doubleBocks--;
        } else if( settings.isBock() && this.bocks > 0 ) {
            factorToIncrease = 2;
            this.bocks--;
        } else
            factorToIncrease = 1;

        for( int i = 0; i < points.length; i++ )
            points[i] *= factorToIncrease;

        for( int i = 0; i < 4; i++ )
            getActivePlayers()[i].addPoints(points[i]);

        if( !repeatRound )
            nextGiverIndex();


        addBocks(bocks);
    }

    private void addBocks(int n) {
        for( int i = 0; i < n; i++ ) {
            if( settings.isDoubleBock() && bocks >= 1 ) {
                int tempBocks = bocks;
                for( int a = 0; a < BOCKS_TO_ADD; a++ ) {
                    if( bocks == 0 )
                        break;
                    doubleBocks++;
                    bocks--;
                }
                bocks += BOCKS_TO_ADD - tempBocks;
            } else if( settings.isBock() ) {
                bocks += BOCKS_TO_ADD;
            }
        }
    }

    private boolean isValidRound(int[] points) {
        int sum = 0;
        for( int i : points)
            sum += i;
        return sum == 0;
    }

    private boolean isSolo(int[] points) {
        int absValue = Math.abs(points[0]);
        for( int i = 1; i < points.length; i++ )
            if( Math.abs(points[i]) != absValue )
                return true;
        return false;
    }

    public Player getGiver() {
        return players[giverIndex];
    }

    private int getAcrossFromGiverIndex() {
        if( players.length != 6 )
            throw new IllegalArgumentException("This method is only used to find the second" +
                    " inactive player when playing with 6 players");
        int playerAcrossIndex = giverIndex + 3;
        if( playerAcrossIndex > 5 ) playerAcrossIndex -= players.length;
            return playerAcrossIndex;
    }

    public Player[] getActivePlayers() {
        if( players.length == 4 )
            return players;
        else {
            Player[] activePlayers = new Player[4];
            int activePlayerIndex = 0;

            for( int i = 0; i < players.length; i++ ) {
                if( players.length == 5 && giverIndex != i ||
                        players.length == 6 && giverIndex != i && getAcrossFromGiverIndex() != i ) {
                    activePlayers[activePlayerIndex] = players[i];
                    activePlayerIndex++;
                }
            }
            return activePlayers;
        }
    }

    public int getMoney( Player player ) {
        return settings.getCentPerPoint() * player.getPointsLost();
    }

    public void nextGiverIndex() {
        giverIndex += 1;
        if (giverIndex > players.length-1) {
            giverIndex -= players.length;
       }
    }


    //getter
    public long getDatabaseId() {
        if( databaseId == -1 )
            throw new NullPointerException("DatabaseId have not been initialized. ");
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getGiverIndex() {
        return giverIndex;
    }

    public int getBocks() {
        return bocks;
    }

    public int getDoubleBocks() {
        return doubleBocks;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setGiverIndex(int giverIndex) {
        this.giverIndex = giverIndex;
    }

    public void setBocks(int bocks) {
        this.bocks = bocks;
    }

    public void setDoubleBocks(int doubleBocks) {
        this.doubleBocks = doubleBocks;
    }

}
