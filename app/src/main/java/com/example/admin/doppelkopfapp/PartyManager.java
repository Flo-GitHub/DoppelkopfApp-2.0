package com.example.admin.doppelkopfapp;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PartyManager implements Serializable {

    private List<Party> parties;
    private long currentParty = 0;

    public PartyManager(List<Party> parties) {
        this.parties = parties;
    }

    public PartyManager(){
        parties = new ArrayList<>();
    }

    public void addParty(Party party) {
        parties.add(party);
    }

    public List<Party> getParties() {
        return parties;
    }

    public Party getCurrentParty(){
        if(currentParty == -1)
            throw new RuntimeException("CurrentParty called but not initialized.");
        for(Party party : parties)
            if(party.getDatabaseId() == currentParty)
                return party;
        throw new RuntimeException("CurrentParty in Party not found.");
    }

    public void setCurrentParty(long id){
        this.currentParty = id;
    }
}
