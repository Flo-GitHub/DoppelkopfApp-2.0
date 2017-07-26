package com.example.admin.doppelkopfapp;

import java.io.Serializable;

/**
 * Created by Admin on 06.06.2017.
 */

public class Player implements Serializable{

    private String name;
    private int points = 0;
    private int pointsLost = 0;
    private final long dataBaseId;

    public Player( long dataBaseId, String name ) {
        this.dataBaseId = dataBaseId;
        this.name = name;
    }

    public void addPoints( int pointsToAdd ) {
        this.points += pointsToAdd;

        if( pointsToAdd < 0 )
            pointsLost += Math.abs(pointsToAdd);
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

    public String getDisplayName( int centPerPoint ) {
        return String.format( "%s: %dP (%f€)", name, points, pointsLost * centPerPoint / 100 );
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPointsLost(int pointsLost) {
        this.pointsLost = pointsLost;
    }
}
