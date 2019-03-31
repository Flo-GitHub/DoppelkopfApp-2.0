package com.example.admin.doppelkopfapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameManager implements Serializable, Comparable {

    private Party party;
    private long[] playersDataBaseIds;
    private int dealerIndex = 0;
    private int bocks[];
    private long databaseId = -1;
    private List<GameRound> rounds;
    private long lastDate;

    public GameManager(Party party, long[] playerDataBaseIds) {
        this.party = party;
        this.playersDataBaseIds = playerDataBaseIds;
        lastDate = MyUtils.getDate();
        rounds = new ArrayList<>();
        bocks = new int[party.getSettings().getMaxBocks()];
        for(int i = 0; i < party.getSettings().getMaxBocks(); i++){
            bocks[i] = 0;
        }
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
            throw new IndexOutOfBoundsException(MainActivity.getContext().getString(R.string.error_more_than_6_players));
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
            throw new IndexOutOfBoundsException(MainActivity.getContext().getString(R.string.error_minimum_4_players));

        long[] tempPlayers = playersDataBaseIds;
        playersDataBaseIds = new long[playersDataBaseIds.length-1];

        int indexToAdd = 0;
        for( long id : tempPlayers )
            if( id != databaseId ) {
                playersDataBaseIds[indexToAdd] = id;
                indexToAdd++;
            }

        while(dealerIndex >= getPlayersDataBaseIds().length) {
            dealerIndex--;
        }
    }

    /**
     * @return actual number of current bocks used
     */
    public int nextRound(GameRound round, boolean repeatRound) {
        //change playerPoints for solo
        Map<Long, Integer> playerPoints = round.getPlayerPoints();
        boolean solo = updateSoloPlayerPoints(playerPoints);

        if (!isValidRound(playerPoints))
            throw new IllegalArgumentException(MainActivity.getContext().getString(R.string.error_winners));

        int factorToIncrease = 1;
        int actualBocks = 0;
        if(!solo || party.getSettings().isSoloBockCalculation()) {
            for(int i = this.bocks.length-1; i >= 0; i--)  {
                if(this.bocks[i] > 0 && party.getSettings().getMaxBocks() >= i) {
                    actualBocks = i+1;
                    factorToIncrease = (int)Math.pow(2, i+1);
                    this.bocks[i]--;
                    break;
                }
            }
        }

        for (long key : playerPoints.keySet()) {
            playerPoints.put(key, playerPoints.get(key)*factorToIncrease);
        }

        if (!repeatRound)
            nextGiverIndex();

        addBocks(round.getNewBocks());
        return actualBocks;
    }

    /**
     * @param map
     * @return if round is solo
     */
    private boolean updateSoloPlayerPoints(Map<Long, Integer> map) {
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
        return winners == 1 || winners == 3;
    }

    private void addBocks(int n) {
        for(int i = 0; i < n; i++) {
            if( party.getSettings().getMaxBocks()==2) {
                int changed = 0; //7
                for( int a = 0; a < playersDataBaseIds.length; a++ ) {
                    if( bocks[0] == 0 )
                        break;
                    bocks[1]++;
                    bocks[0]--;
                    changed++;
                }
                bocks[0] += playersDataBaseIds.length - changed;
            } else if( party.getSettings().getMaxBocks()==1 ) {
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
        return playersDataBaseIds[dealerIndex];
    }

    /*
    This method is only used to find other inactive player in a game of six playersDataBaseIds.
     */
    private int getAcrossFromGiverIndex() {
        if( playersDataBaseIds.length != 6 )
            throw new IllegalArgumentException("This method is only used to find the second" +
                    " inactive player when playing with 6 playersDataBaseIds");
        int playerAcrossIndex = dealerIndex + 3;
        if( playerAcrossIndex > 5 ) playerAcrossIndex -= playersDataBaseIds.length;
            return playerAcrossIndex;
    }

    public long[] getActivePlayers() {

        if( playersDataBaseIds.length == 4 )
            return playersDataBaseIds;
        else {
            long[] activePlayers = new long[4];
            int activePlayerIndex = 0;

            for(int i = dealerIndex; i < playersDataBaseIds.length; i++ ) {
                if( playersDataBaseIds.length == 5 && dealerIndex != i ||
                        playersDataBaseIds.length == 6 && dealerIndex != i && getAcrossFromGiverIndex() != i ) {
                    activePlayers[activePlayerIndex] = playersDataBaseIds[i];
                    activePlayerIndex++;
                }
            }
            for(int i = 0; i < dealerIndex; i++ ) {
                if( playersDataBaseIds.length == 5 ||
                        playersDataBaseIds.length == 6 && getAcrossFromGiverIndex() != i ) {
                    activePlayers[activePlayerIndex] = playersDataBaseIds[i];
                    activePlayerIndex++;
                }
            }
            return activePlayers;
        }
    }

    public void nextGiverIndex() {
        dealerIndex += 1;
        if (dealerIndex > playersDataBaseIds.length-1) {
            dealerIndex -= playersDataBaseIds.length;
       }
    }

    public GameManager cloneGameManager() {
        GameManager gameManager = new GameManager(party, playersDataBaseIds);
        gameManager.setBocks(this.bocks);
        gameManager.setDatabaseId(this.databaseId);
        gameManager.setDealerIndex(this.dealerIndex);
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

    public void addRound(GameRound round, boolean repeat) {
        int actualBocks = nextRound(round, repeat);
        round.setCurrentBocks(actualBocks);
        this.rounds.add(round);
        lastDate = MyUtils.getDate();
        party.setLastDate(MyUtils.getDate());
    }

    public void addRound(GameRound round) {
        addRound(round, false);
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
            throw new NullPointerException(MainActivity.getContext().getString(R.string.error_db_id_not_set));
        return databaseId;
    }

    public void resetBocks(int newMaxBocks) {
        int[] bocks = new int[newMaxBocks];

        if(newMaxBocks > 0) {
            for(int i = 0; i < newMaxBocks-1; i++) {
                bocks[i] = getBockSafe(i);
            }
            for(int i = newMaxBocks-1; i < this.bocks.length; i++) {
                bocks[newMaxBocks-1] += getBockSafe(i) * (int)Math.pow(2, ( i-(newMaxBocks-1) ));
            }
        }
        this.bocks = bocks;
    }

    public int getBockSafe(int index){
        try {
            return bocks[index];
        }catch (Exception e) {
            return 0;
        }
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public long[] getPlayersDataBaseIds() {
        return playersDataBaseIds;
    }

    public int getDealerIndex() {
        return dealerIndex;
    }

    public int[] getBocks() {
        return bocks;
    }

    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    public void setBocks(int[] bocks) {
        this.bocks = bocks;
    }

    public void setRounds(List<GameRound> rounds) {
        this.rounds = rounds;
    }

    public List<GameRound> getRounds() {
        return rounds;
    }

    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public Bitmap getImage() {
        return party.getImage();
    }

    public GameRound getLastRound() {
        if(rounds.size() > 0) {
            return rounds.get(rounds.size()-1);
        }
        throw new IllegalArgumentException("No rounds available for getlastRound");
    }

    @Override
    public int compareTo(Object o) {
        GameManager other = (GameManager) o;
        return MyUtils.compareDates(this.getLastDate(), other.getLastDate());
    }
}
