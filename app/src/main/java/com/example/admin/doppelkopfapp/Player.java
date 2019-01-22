package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Player implements Serializable, Comparable{

    private String name;
    private int points;
    private int pointsLost;
    private final long dataBaseId;

    public Player( long dataBaseId, String name ) {
        this.dataBaseId = dataBaseId;
        this.name = name;

        points = 0;
        pointsLost = 0;
    }

    public void addPoints(int pointsToAdd ) {
        this.points += pointsToAdd;

        if( pointsToAdd < 0 )
            this.points += Math.abs(pointsToAdd);
    }

    public long getDataBaseId() {
        return dataBaseId;
    }

    public int getPoints() {
        return points;
    }

    public int getPointsLost() { return pointsLost; }

    public String getName() {
        return name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPointsLost(int pointsLost) {
        this.pointsLost = pointsLost;
    }

    public Player clonePlayer() {
        Player player = new Player(this.dataBaseId, this.name);
        player.setPoints(this.points);
        player.setPointsLost(this.pointsLost);
        return player;
    }

    @Override
    public boolean equals( Object o ) {
        return ((Player) o).getDataBaseId() == dataBaseId;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Player p = (Player) o;
        return new Long(this.dataBaseId).compareTo( ((Player) o).dataBaseId);
    }
}
