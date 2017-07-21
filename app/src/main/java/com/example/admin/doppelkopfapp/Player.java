package com.example.admin.doppelkopfapp;

/**
 * Created by Admin on 06.06.2017.
 */

public class Player {

    private String name;
    private int points = 0;
    private int pointsLost = 0;

    public Player( String name ) {
        this.name = name;
    }

    public void addPoints( int pointsToAdd ) {
        this.points += pointsToAdd;

        if( pointsToAdd < 0 )
            pointsLost += Math.abs(pointsToAdd);
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
