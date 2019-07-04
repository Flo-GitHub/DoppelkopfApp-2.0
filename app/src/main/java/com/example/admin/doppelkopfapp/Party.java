package com.example.admin.doppelkopfapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Party implements Serializable, Comparable {

    public static final int PLAYER_LIMIT = 15;

    private GameSettings settings;
    private List<GameManager> games;
    private List<Player> players;
    private String name;
    private long lastDate;
    private byte[] imageBytes;
    private long currentGame = -1;
    private long databaseId = -1;

    public Party(String name, List<Player> players, long lastDate) {
        this(name, players, MyUtils.defaultSettings(), lastDate);
    }

    public Party(String name, List<Player> players, GameSettings settings, long lastDate) {
        this.name = name;
        this.players = players;
        this.lastDate = lastDate;
        this.settings = settings;
        games = new ArrayList<>();
        sort();
    }

    public void sort() {
        Collections.sort(games);
    }

    public void addGame(GameManager game) {
        if(games == null) {
            games = new ArrayList<>();
        }
        this.games.add(game);
        sort();
    }

    public Player[] getPlayersByDBId(long[] playersDataBaseId) {
        List<Player> players = new ArrayList<>();
        for(long id : playersDataBaseId) {
           players.add(getPlayerByDBId(id));
        }
        return players.toArray(new Player[players.size()]);
    }

    public Player getPlayerByDBId(long playerDataBaseId) {
         for(Player p : players) {
             if(p.getDataBaseId() == playerDataBaseId) {
                 return p;
             }
         }
         throw new IllegalArgumentException("PlayerDataBaseId doesn't exist" + playerDataBaseId);
    }

    public boolean hasPlayerUse(Player p){
        for(GameManager game : getGames()){
            if(game.getPlayers().contains(p)){
                return true;
            }
        }
        return false;
    }

    /*
    @return true if player was successfully added to list
     */
    public boolean addPlayer(Player player) {
        if(players.size() >= PLAYER_LIMIT || players.contains(player)) {
            return false;
        }
        for(Player p : this.players) {
            if(p.getName().equals(player.getName())){
                return false;
            }
        }

        this.players.add(player);
        return true;
    }

    /*
    @return true if player was successfully removed from the list
     */
    public boolean removePlayer(Player player) {
        if(this.players.contains(player)) {
            this.players.remove(player);
            return true;
        }
        return false;
    }

    private byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
        return bos.toByteArray();
    }

    //overall, won, lost
    public int[] getPlayerStats(Player p, StatsFragment.Stats stats){
        int[] values = new int[3];
        for(int i = 0; i < values.length; i++){
            values[i] = 0;
        }
        for(GameManager game : getGames()){
            for(GameRound round : game.getRounds()){
                try{
                    int pts = round.getPlayerPoints().get(p.getDataBaseId());
                    switch(stats){
                        case POINTS:
                            if(pts > 0){
                                values[1] += pts;
                            } else{
                                values[2] -= pts;
                            }
                            values[0] += pts;
                            break;
                        case ROUNDS:
                            if(pts > 0){
                                values[1]++;
                                values[0]++;
                            } else if(pts < 0){
                                values[2]++;
                                values[0]--;
                            }
                            break;
                        case SOLO:
                            switch (isSolo(round.getPlayerPoints(), p.getDataBaseId())){
                                case WIN:
                                    values[0]++;
                                    values[1]++;
                                    break;
                                case LOSS:
                                    values[0]--;
                                    values[2]++;
                                    break;
                            }
                    }


                } catch (Exception ignore){}

            }
        }
        return values;
    }

    enum SoloType{
        WIN, LOSS, NO_SOLO
    }

    private SoloType isSolo(Map<Long, Integer> playerPoints, long player) {
        Set<Long> winners = new HashSet<>();
        Set<Long> losers = new HashSet<>();

        for(long id : playerPoints.keySet()) {
            if(playerPoints.get(id) > 0) {
                winners.add(id);
            } else if (playerPoints.get(id) < 0) {
                losers.add(id);
            }
        }

        if(winners.size() == 3 && losers.contains(player)) {
            return SoloType.LOSS;
        } else if(losers.size() == 3 && winners.contains(player)){
            return SoloType.WIN;
        }
        return SoloType.NO_SOLO;
    }


    public String getPlayersAsString(){
        return MyUtils.getPlayersAsString(players);
    }

    public Player[] getCurrentActivePlayers(){
        long[] playerIds = getCurrentGame().getActivePlayers();
        return getPlayersByDBId(playerIds);
    }

    public GameManager getCurrentGame(){
        if(currentGame == -1)
            throw new RuntimeException("CurrentGame in Party called but not initialized.");
        for(GameManager game : games)
            if(game.getDatabaseId() == currentGame)
                return game;
        throw new RuntimeException("CurrentGame in Party not found.");
    }

    public void setCurrentGame(long currentGame) {
        this.currentGame = currentGame;
    }

    public long getLastDate() {
        return lastDate;
    }

    public List<GameManager> getGames() {
        return games;
    }

    public void setGames(List<GameManager> games) {
        this.games = games;
        sort();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Bitmap image) {
        try {
            imageBytes = getImageBytes(image);
        } catch(Exception e) {
        }
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public Bitmap getImage() {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public int compareTo(Object o) {
        Party other = (Party)o;
        return MyUtils.compareDates(this.getLastDate(), other.getLastDate());
    }
}
