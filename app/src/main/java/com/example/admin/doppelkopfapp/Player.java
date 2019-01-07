package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Player implements Serializable, Comparable{

    private String name;
    private Map<Long, Integer> points;
    private Map<Long, Integer> pointsLost;
    private final long dataBaseId;

    public Player( long dataBaseId, String name ) {
        this.dataBaseId = dataBaseId;
        this.name = name;

        points = new HashMap<>();
        pointsLost = new HashMap<>();
    }

    public void addPoints( long gameId, int pointsToAdd ) {
        addValue(gameId, pointsToAdd, this.points);

        if( pointsToAdd < 0 )
            addValue(gameId, Math.abs(pointsToAdd), this.pointsLost);
    }

    private static void addValue(long key, int pointsToAdd, Map<Long, Integer> map) {
        int old = map.containsKey(key) ? map.get(key) : 0;
        map.put(key, old + pointsToAdd);
    }

    public long getDataBaseId() {
        return dataBaseId;
    }

    public Map<Long, Integer> getPoints() {
        return points;
    }

    public Map<Long, Integer> getPointsLost() { return pointsLost; }

    public String getName() {
        return name;
    }

    public void setPoints(Map<Long, Integer> points) {
        this.points = points;
    }

    public void setPointsLost(Map<Long, Integer> pointsLost) {
        this.pointsLost = pointsLost;
    }

    public Player clonePlayer() {
        Player player = new Player(this.dataBaseId, this.name);
        player.setPoints(this.points); //todo may have to update clone method
        player.setPointsLost(this.pointsLost);
        return player;
    }

    @Override
    public boolean equals( Object o ) {
        if (((Player) o).getDataBaseId() == dataBaseId)
            return true;
        return false;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Player p = (Player) o;
        return new Long(this.dataBaseId).compareTo( ((Player) o).dataBaseId);
    }
}
