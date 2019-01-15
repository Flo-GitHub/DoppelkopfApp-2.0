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
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Player1"));
        players.add(new Player(2, "Player2"));
        players.add(new Player(3, "Player3"));
        players.add(new Player(4, "Player4"));
        GameSettings settings = new GameSettings(1, false, false, false, false);
        Party party = new Party("This is the coolest group in the world", players, settings, "Jan 14, 2018");
        dataSource.createParty(party);
        GameManager game = new GameManager(party, new long[]{1, 2, 3, 4});
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
