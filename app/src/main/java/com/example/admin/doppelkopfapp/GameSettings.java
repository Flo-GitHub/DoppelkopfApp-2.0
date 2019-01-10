package com.example.admin.doppelkopfapp;

import java.io.Serializable;

public class GameSettings implements Serializable{

    private int centPerPoint;
    private boolean addPoints;
    private boolean bock;
    private boolean doubleBock;
    private boolean soloBockCalculation;

    public GameSettings(int centPerPoint, boolean bock, boolean doubleBock, boolean soloBockCalculation, boolean addPoints) {
        this.centPerPoint = centPerPoint;
        this.bock = bock;
        this.doubleBock = doubleBock;
        this.soloBockCalculation = soloBockCalculation;
        this.addPoints = addPoints;
    }

    public int getCentPerPoint() {
        return centPerPoint;
    }

    public boolean isBock() {
        return bock;
    }

    public boolean isDoubleBock() {
        return doubleBock;
    }

    public boolean isAddPoints() {
        return addPoints;
    }

    public boolean isSoloBockCalculation() {
        return soloBockCalculation;
    }

    public GameSettings cloneSettings() {
        return new GameSettings(this.centPerPoint, this.bock, this.doubleBock, this.soloBockCalculation, this.addPoints);
    }
}