package com.example.admin.doppelkopfapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameRound implements Serializable {

    private int currentBocks = 0;
    private int newBocks = 0;
    private Map<Long, Integer> playerPoints;
    private long dataBaseId = -1;


    public GameRound(Map<Long, Integer> playerPoints) {
        this.playerPoints = playerPoints;
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

    public void setDataBaseId(long dataBaseId) {
        this.dataBaseId = dataBaseId;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }
}