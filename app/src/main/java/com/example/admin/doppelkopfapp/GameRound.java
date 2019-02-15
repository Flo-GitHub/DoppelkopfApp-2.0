package com.example.admin.doppelkopfapp;

import java.util.HashMap;
import java.util.Map;

public class GameRound {

    private int currentBocks = 0;
    private int newBocks = 0;
    private Map<Long, Integer> playerPoints;
    private long dataBaseId;


    public GameRound(long dataBaseId, Map<Long, Integer> playerPoints) {
        this.currentBocks = currentBocks;
        this.playerPoints = playerPoints;
        this.dataBaseId = dataBaseId;
    }

    public void setNewBocks(int newBocks) {
        this.newBocks = newBocks;
    }

    public void setCurrentBocks(int currentBocks) {
        this.currentBocks = currentBocks;
    }

    public int getNewBocks() {
        return newBocks;
    }

    public int getCurrentBocks() {
        return currentBocks;
    }

    public Map<Long, Integer> getPlayerPoints() {
        return playerPoints;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }
}