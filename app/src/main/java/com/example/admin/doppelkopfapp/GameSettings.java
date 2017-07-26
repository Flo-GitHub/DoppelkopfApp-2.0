package com.example.admin.doppelkopfapp;

import java.io.Serializable;

/**
 * Created by Admin on 12/07/2017.
 */

public class GameSettings implements Serializable{

    private int centPerPoint;
    private boolean bock;
    private boolean doubleBock;
    private boolean soloBockCalculation;
    private final long dataBaseId;

    public GameSettings(long dataBaseId, int centPerPoint, boolean bock, boolean doubleBock, boolean soloBockCalculation) {
        this.dataBaseId = dataBaseId;
        this.centPerPoint = centPerPoint;
        this.bock = bock;
        this.doubleBock = doubleBock;
        this.soloBockCalculation = soloBockCalculation;
    }

    public int getCentPerPoint() {
        return centPerPoint;
    }

    public void setCentPerPoint(int centPerPoint) {
        this.centPerPoint = centPerPoint;
    }

    public boolean isBock() {
        return bock;
    }

    public void setBock(boolean bock) {
        this.bock = bock;
    }

    public boolean isDoubleBock() {
        return doubleBock;
    }

    public void setDoubleBock(boolean doubleBock) {
        this.doubleBock = doubleBock;
    }

    public boolean isSoloBockCalculation() {
        return soloBockCalculation;
    }

    public void setSoloBockCalculation(boolean soloBockCalculation) {
        this.soloBockCalculation = soloBockCalculation;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }

}
