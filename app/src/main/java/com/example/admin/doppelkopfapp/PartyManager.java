package com.example.admin.doppelkopfapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {

    private List<Party> parties;
    private GameDataSource dataSource;

    public PartyManager(Context context) {
        dataSource = new GameDataSource(context);
        dataSource.open();
        //test
        parties = dataSource.getAllParties();
    }

    public void addParty(Party party) {
        parties.add(party);
        dataSource.createParty(party);
    }

    public void closeDataBase() {
        dataSource.close();
    }

    public List<Party> getParties() {
        return parties;
    }
}
