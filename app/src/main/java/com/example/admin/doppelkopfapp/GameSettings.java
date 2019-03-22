package com.example.admin.doppelkopfapp;

import java.io.Serializable;

public class GameSettings implements Serializable{

    private int centPerPoint;
    private boolean addPoints;
    private int maxBocks;
    private boolean soloBockCalculation;

    public GameSettings(int maxBocks, boolean soloBockCalculation) {
        this.centPerPoint = 0;
        this.maxBocks = maxBocks;
        this.soloBockCalculation = soloBockCalculation;
        this.addPoints = false;
    }

    public void setAddPoints(boolean addPoints) {
        this.addPoints = addPoints;
    }

    public void setCentPerPoint(int centPerPoint) {
        this.centPerPoint = centPerPoint;
    }

    public void setMaxBocks(int maxBocks) {
        this.maxBocks = maxBocks;
    }

    public void setSoloBockCalculation(boolean soloBockCalculation) {
        this.soloBockCalculation = soloBockCalculation;
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
}
