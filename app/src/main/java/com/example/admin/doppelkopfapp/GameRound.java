package com.example.admin.doppelkopfapp;

import java.util.HashMap;

public class GameRound {

    private int currentRoundBock; //1 = regular, 2 = double, 3 = triple
    private int newBocks;
    private String date;
    private HashMap<Long, Boolean> playerSides;
    private int rePoints;
    private int reBonusPoints = 0;
    private int conBonusPoints = 0;
    private int reAnnouncement = -1;
    private int conAnnouncement = -1;
    private long dataBaseId;


    public GameRound(long dataBaseId) {
        this.dataBaseId = dataBaseId;
    }

    public HashMap<Long, Boolean> getPlayerSides() {
        return playerSides;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReAnnouncement(int reAnnouncement) {
        this.reAnnouncement = reAnnouncement;
    }

    public int getReAnnouncement() {
        return reAnnouncement;
    }

    public void setReBonusPoints(int reBonusPoints) {
        this.reBonusPoints = reBonusPoints;
    }

    public int getReBonusPoints() {
        return reBonusPoints;
    }

    public void setConAnnouncement(int conAnnouncement) {
        this.conAnnouncement = conAnnouncement;
    }

    public int getConAnnouncement() {
        return conAnnouncement;
    }

    public void setConBonusPoints(int conBonusPoints) {
        this.conBonusPoints = conBonusPoints;
    }

    public int getConBonusPoints() {
        return conBonusPoints;
    }

    public int getRePoints() {
        return rePoints;
    }

    public void setRePoints(int rePoints) {
        this.rePoints = rePoints;
    }

    public int getNewBocks() {
        return newBocks;
    }

    public void setNewBocks(int newBocks) {
        this.newBocks = newBocks;
    }

    public int getCurrentRoundBock() {
        return currentRoundBock;
    }

    public void setCurrentRoundBock(int currentRoundBock) {
        this.currentRoundBock = currentRoundBock;
    }

}
