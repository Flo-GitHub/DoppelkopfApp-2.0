package com.example.admin.doppelkopfapp;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Player implements Serializable, Comparable{

    private String name;
    public long dataBaseId = -1;

    public Player( String name ) {
        this.name = name;
    }

    public long getDataBaseId() {
        return dataBaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataBaseId(long dataBaseId) {
        this.dataBaseId = dataBaseId;
    }

    @Override
    public boolean equals( Object o ) {
        return ((Player) o).getDataBaseId() == dataBaseId;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Player p = (Player) o;
        return Long.valueOf(this.dataBaseId).compareTo( ((Player) o).dataBaseId);
    }
}
