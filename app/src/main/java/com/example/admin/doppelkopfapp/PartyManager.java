package com.example.admin.doppelkopfapp;

import android.content.Context;

import java.util.List;

public class PartyManager {

    List<Party> parties;
    GameDataSource dataSource;

    public PartyManager(Context context) {
        dataSource = new GameDataSource(context);
        parties = dataSource.getAllParties();
    }

    public void addParty(Party party) {
        parties.add(party);
    }

    public List<Party> getParties() {
        return parties;
    }
}
