package com.example.admin.doppelkopfapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Party implements Serializable {

    public static final int PLAYER_LIMIT = 15;

    private List<GameManager> games;
    private List<Player> players;
    private String name;
    private String lastDate;
    private long currentGame = -1;
    private long databaseId;

    public Party(String name, List<Player> players, String lastDate) {
        this.name = name;
        this.players = players;
        this.lastDate = lastDate;
    }

    public void addGames(List<GameManager> games) {
        this.games = games;
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
         throw new IllegalArgumentException("PlayerDataBaseId doesn't exist");
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


    public String getPlayersAsString(){
        return MyUtils.getPlayersAsString(players);
    }

    public Player[] getCurrentActivePlayers(){
        long[] playerIds = getCurrentGame().getPlayersDataBaseIds();
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

    public String getLastDate() {
        return lastDate;
    }

    public List<GameManager> getGames() {
        return games;
    }

    public void setGames(List<GameManager> games) {
        this.games = games;
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
}
