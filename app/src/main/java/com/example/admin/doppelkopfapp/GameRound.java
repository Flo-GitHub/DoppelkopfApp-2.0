package com.example.admin.doppelkopfapp;

import java.util.HashMap;

public class GameRound {

    private boolean isBock;
    private HashMap<Long, Integer> playerPoints;
    private long dataBaseId;
    //<PLAYERID, POINTS>


    public GameRound(long dataBaseId, boolean isBock, HashMap<Long, Integer> playerPoints) {
       this.isBock = isBock;
       this.playerPoints = playerPoints;
    }

    public boolean isBock() {
        return isBock;
    }

    public HashMap<Long, Integer> getPlayerPoints() {
        return playerPoints;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }
}
