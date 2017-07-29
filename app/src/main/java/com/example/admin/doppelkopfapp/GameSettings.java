package com.example.admin.doppelkopfapp;

import java.io.Serializable;

/**
 * Class that only contains the settings.
 */

public class GameSettings implements Serializable{

    private int centPerPoint;
    private boolean bock;
    private boolean doubleBock;
    private boolean soloBockCalculation;

    public GameSettings(int centPerPoint, boolean bock, boolean doubleBock, boolean soloBockCalculation) {
        this.centPerPoint = centPerPoint;
        this.bock = bock;
        this.doubleBock = doubleBock;
        this.soloBockCalculation = soloBockCalculation;
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

    public boolean isSoloBockCalculation() {
        return soloBockCalculation;
    }

}
