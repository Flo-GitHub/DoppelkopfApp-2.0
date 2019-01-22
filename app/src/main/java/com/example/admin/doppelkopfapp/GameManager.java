package com.example.admin.doppelkopfapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class GameManager implements Serializable {

    private Party party;
    private GameSettings settings;
    private long[] playersDataBaseIds;
    private int giverIndex = 0;
    private int bocks = 0,
                doubleBocks = 0;
    private long databaseId = -1;
    private ArrayList<GameRound> rounds;
    private String lastDate = "";
    private String firstDate = "";

    public GameManager(Party party, GameSettings settings, long[] playerDataBaseIds) {
        this.party = party;
        this.playersDataBaseIds = playerDataBaseIds;
        this.settings = settings;
        rounds = new ArrayList<>();
    }

    public void skipRound() {
        nextGiverIndex();
    }

    /**
     *
     * @param newPlayerId
     * @param indexToAdd
     * @return boolean if the add button should be disabled due to six playersDataBaseIds already playing
     */
    public boolean addPlayer( long newPlayerId, int indexToAdd ) {
        //for the case something goes wrong or for the manual adding of playersDataBaseIds
        if( playersDataBaseIds.length >= 6 )
            throw new IndexOutOfBoundsException("Can't add more than six playersDataBaseIds to game.");
        long[] tempPlayers = playersDataBaseIds;
        playersDataBaseIds = new long[playersDataBaseIds.length+1];

        boolean newPlayerInserted = false;
        for( int i = 0; i < playersDataBaseIds.length; i++ ) {
            if(i == indexToAdd) {
                playersDataBaseIds[i] = newPlayerId;
                newPlayerInserted = true;
            }
            else
                playersDataBaseIds[i] = tempPlayers[newPlayerInserted ? i-1 : i];
        }
        return playersDataBaseIds.length >= 6;
    }

    public void removePlayer( long databaseId ) {
        if( playersDataBaseIds.length <= 4 )
            throw new IndexOutOfBoundsException("Can't remove the player because at least 4 playersDataBaseIds are needed");

        long[] tempPlayers = playersDataBaseIds;
        playersDataBaseIds = new long[playersDataBaseIds.length-1];

        int indexToAdd = 0;
        for( long id : tempPlayers )
            if( id != databaseId ) {
                playersDataBaseIds[indexToAdd] = id;
                indexToAdd++;
            }

        while(giverIndex >= getPlayersDataBaseIds().length) {
            giverIndex--;
        }
    }

    public void nextRound(int[] points, int bocks, boolean repeatRound) {
        if (!isValidRound(points))
            throw new IllegalArgumentException("Sum of the points not equal 0.");
        else if (points.length != 4)
            throw new IllegalArgumentException("Length of points should be 4, but was " + points.length);

        int factorToIncrease;
        if (settings.isSoloBockCalculation() && isSolo(points))
            factorToIncrease = 1;
        else if (settings.isDoubleBock() && doubleBocks > 0) {
            factorToIncrease = 4;
            doubleBocks--;
        } else if (settings.isBock() && this.bocks > 0) {
            factorToIncrease = 2;
            this.bocks--;
        } else
            factorToIncrease = 1;

        for (int i = 0; i < points.length; i++)
            points[i] *= factorToIncrease;

        party.getPlayersByDBId(getActivePlayers());
        for (int i = 0; i < 4; i++) {
            party.getPlayerByDBId(getActivePlayers()[i]).addPoints(points[i]);
        }

        if (!repeatRound)
            nextGiverIndex();

        addBocks(bocks);
    }

    private void addBocks(int n) {
        for( int i = 0; i < n; i++ ) {
            if( settings.isDoubleBock() && bocks >= 1 ) {
                int tempBocks = bocks;
                for( int a = 0; a < playersDataBaseIds.length; a++ ) {
                    if( bocks == 0 )
                        break;
                    doubleBocks++;
                    bocks--;
                }
                bocks += playersDataBaseIds.length - tempBocks;
            } else if( settings.isBock() ) {
                bocks += playersDataBaseIds.length;
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

    public long getGiver() {
        return playersDataBaseIds[giverIndex];
    }

    /*
    This method is only used to find other inactive player in a game of six playersDataBaseIds.
     */
    private int getAcrossFromGiverIndex() {
        if( playersDataBaseIds.length != 6 )
            throw new IllegalArgumentException("This method is only used to find the second" +
                    " inactive player when playing with 6 playersDataBaseIds");
        int playerAcrossIndex = giverIndex + 3;
        if( playerAcrossIndex > 5 ) playerAcrossIndex -= playersDataBaseIds.length;
            return playerAcrossIndex;
    }

    public long[] getActivePlayers() {

        if( playersDataBaseIds.length == 4 )
            return playersDataBaseIds;
        else {
            long[] activePlayers = new long[4];
            int activePlayerIndex = 0;

            for(int i = giverIndex; i < playersDataBaseIds.length; i++ ) {
                if( playersDataBaseIds.length == 5 && giverIndex != i ||
                        playersDataBaseIds.length == 6 && giverIndex != i && getAcrossFromGiverIndex() != i ) {
                    activePlayers[activePlayerIndex] = playersDataBaseIds[i];
                    activePlayerIndex++;
                }
            }
            for( int i = 0; i < giverIndex; i++ ) {
                if( playersDataBaseIds.length == 5 && giverIndex != i ||
                        playersDataBaseIds.length == 6 && giverIndex != i && getAcrossFromGiverIndex() != i ) {
                    activePlayers[activePlayerIndex] = playersDataBaseIds[i];
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
        if (giverIndex > playersDataBaseIds.length-1) {
            giverIndex -= playersDataBaseIds.length;
       }
    }

    public GameManager cloneGameManager() {
        GameSettings settings = this.settings.cloneSettings();
        GameManager gameManager = new GameManager(party, settings, playersDataBaseIds);
        gameManager.setBocks(this.bocks);
        gameManager.setDatabaseId(this.databaseId);
        gameManager.setDoubleBocks(this.doubleBocks);
        gameManager.setGiverIndex(this.giverIndex);
        return gameManager;
    }


    //----
    public String getPlayersAsString() {
        Player[] players = party.getPlayersByDBId(playersDataBaseIds);
        return MyUtils.getPlayersAsString(Arrays.asList(players));
    }

    public long getDatabaseId() {
        if( databaseId == -1 )
            throw new NullPointerException("DatabaseId has not been set yet");
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public long[] getPlayersDataBaseIds() {
        return playersDataBaseIds;
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

    public void setGiverIndex(int giverIndex) {
        this.giverIndex = giverIndex;
    }

    public void setBocks(int bocks) {
        this.bocks = bocks;
    }

    public void setDoubleBocks(int doubleBocks) {
        this.doubleBocks = doubleBocks;
    }

    public void setRounds(ArrayList<GameRound> rounds) {
        this.rounds = rounds;
    }

    public ArrayList<GameRound> getRounds() {
        return rounds;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }
}
