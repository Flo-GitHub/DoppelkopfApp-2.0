package com.example.admin.doppelkopfapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {

    List<Party> parties;
    GameDataSource dataSource;

    public PartyManager(Context context) {
        dataSource = new GameDataSource(context);
        //test
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Player1"));
        players.add(new Player(2, "Player2"));
        GameSettings settings = new GameSettings(1, false, false, false, false);
        dataSource.createParty(new Party("Group Name", players, settings));
        //end test
        parties = dataSource.getAllParties();
    }

    public void addParty(Party party) {
        parties.add(party);
        dataSource.createParty(party);
    }

    public List<Party> getParties() {
        return parties;
    }
}
