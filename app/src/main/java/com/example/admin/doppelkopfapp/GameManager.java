package com.example.admin.doppelkopfapp;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager implements Serializable {

    private Party party;
    private GameSettings settings;
    private long[] playersDataBaseIds;
    private int giverIndex = 0;
    private int bocks[];
    private long databaseId = -1;
    private ArrayList<GameRound> rounds;
    private String lastDate;

    public GameManager(Party party, GameSettings settings, long[] playerDataBaseIds) {
        this.party = party;
        this.playersDataBaseIds = playerDataBaseIds;
        this.settings = settings;
        rounds = new ArrayList<>();
        bocks = new int[settings.getMaxBocks()];
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

    public void nextRound(Map<Long, Integer> playerPoints, int newBocks, boolean repeatRound) {
        //change playerPoints for solo
        updateSoloPlayerPoints(playerPoints);
        if (!isValidRound(playerPoints))
            throw new IllegalArgumentException("Sum of the points not equal 0.");
        else if (playerPoints.size() != 4)
            throw new IllegalArgumentException("Length of points should be 4, but was " + playerPoints.size());

        int factorToIncrease = 1;
        //todo solo bock calculation
         for(int i = this.bocks.length-1; i <= 0; i--)  {
             if(this.bocks[i] > 0) {
                 factorToIncrease = (int)Math.pow(2, this.bocks[i]) ;
                 break;
             }
         }

        Log.e("Gamemangaer", "FACTOR TO INCREASE " + factorToIncrease);

        for (long key : playerPoints.keySet()) {
            party.getPlayerByDBId(key).addPoints(playerPoints.get(key)*factorToIncrease);
        }

        if (!repeatRound)
            nextGiverIndex();

        addBocks(newBocks);
    }

    private Map<Long, Integer> updateSoloPlayerPoints(Map<Long, Integer> map) {
        int winners = 0;
        for(Long key : map.keySet()) {
            if (map.get(key)>0)
                winners++;
        }

        for(Long key : map.keySet()) {
            if( (winners == 1 && map.get(key)>0) || (winners == 3 && map.get(key)<0) ){
                map.put(key, map.get(key) * 3);
            }
        }
        return map;
    }

    private void addBocks(int n) {
        for(int i = 0; i < n; i++) {
            if( settings.getMaxBocks()>=2 && bocks[0] >= 1 ) {
                int temp = bocks[0];
                for( int a = 0; a < playersDataBaseIds.length; a++ ) {
                    if( bocks[0] == 0 )
                        break;
                    bocks[1]++;
                    bocks[0]--;
                }
                bocks[0] += playersDataBaseIds.length - temp;
            } else if( settings.getMaxBocks()==1 ) {
                bocks[0] += playersDataBaseIds.length;
            }
        }
    }

    private boolean isValidRound(Map<Long, Integer> points) {
        int sum = 0;
        for(int i : points.values())
            sum += i;
        return sum == 0;
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
        gameManager.setGiverIndex(this.giverIndex);
        return gameManager;
    }


    //----
    public int getCurrentBocks(){
        for(int i = bocks.length-1; i >= 0; i--) {
            if(bocks[i]> 0){
                return i+1;
            }
        }
        return 0;
    }

    public void addRound(GameRound round) {
        this.rounds.add(round);//todo add repeat round
        nextRound(round.getPlayerPoints(), round.getNewBocks(), false);
    }

    public String getPlayersAsString() {
        Player[] players = party.getPlayersByDBId(playersDataBaseIds);
        return MyUtils.getPlayersAsString(Arrays.asList(players));
    }

    public String[] getPlayersAsStrings() {
        Player[] players = party.getPlayersByDBId(playersDataBaseIds);
        String[] names = new String[players.length];
        for(int i = 0; i < players.length; i++) {
            names[i] = players[i].getName();
        }
        return names;
    }

    public List<Player> getPlayers() {
        return Arrays.asList(party.getPlayersByDBId(getPlayersDataBaseIds()));
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

    public int[] getBocks() {
        return bocks;
    }

    public void setGiverIndex(int giverIndex) {
        this.giverIndex = giverIndex;
    }

    public void setBocks(int[] bocks) {
        this.bocks = bocks;
    }

    public void setRounds(ArrayList<GameRound> rounds) {
        this.rounds = rounds;
    }

    public ArrayList<GameRound> getRounds() {
        return rounds;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }
}
