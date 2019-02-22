package com.example.admin.doppelkopfapp;

import java.io.Serializable;

public class GameSettings implements Serializable{

    private int centPerPoint;
    private boolean addPoints;
    private int maxBocks;
    private boolean soloBockCalculation;

    public GameSettings(int centPerPoint, int maxBocks, boolean soloBockCalculation, boolean addPoints) {
        this.centPerPoint = centPerPoint;
        this.maxBocks = maxBocks;
        this.soloBockCalculation = soloBockCalculation;
        this.addPoints = addPoints;
    }

    public int getCentPerPoint() {
        return centPerPoint;
    }

    public int getMaxBocks() {
        return maxBocks;
    }

    public boolean isAddPoints() {
        return addPoints;
    }

    public boolean isSoloBockCalculation() {
        return soloBockCalculation;
    }

    public GameSettings cloneSettings() {
        return new GameSettings(this.centPerPoint, this.maxBocks, this.soloBockCalculation, this.addPoints);
    }
}
