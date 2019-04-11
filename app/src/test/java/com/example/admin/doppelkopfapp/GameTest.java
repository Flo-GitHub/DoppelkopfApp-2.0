package com.example.admin.doppelkopfapp;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

public class GameTest {
    @Test
    public void testResetBock() {
        Party party = sampleParty();
        party.getCurrentGame().setBocks(new int[]{34});
        party.getSettings().setMaxBocks(2);
        party.getCurrentGame().resetBocks(party.getSettings().getMaxBocks());
        assertThat(party.getCurrentGame().getBockSafe(0), is(4));
        assertThat(party.getCurrentGame().getBockSafe(1), is(15));
    }

    @Test
    public void testRemoveBocks() {
        Party party = sampleParty();
        party.getCurrentGame().setBocks(new int[]{4, 14});//9 9   4 9   9 4  (1, 8)
        party.getSettings().setMaxBocks(2);
        party.getCurrentGame().removeNewBocks(3);
        assertThat(party.getCurrentGame().getBockSafe(0), is(9));
        assertThat(party.getCurrentGame().getBockSafe(1), is(4));
    }

    private static Party sampleParty() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        players.add(new Player("Player3"));
        players.add(new Player("Player4"));
        players.add(new Player("Player5555"));

        Party party = new Party("This is the coolest group in the world", players, 566654);
        party.setDatabaseId(454);
        party.setSettings(defaultSettings());

        GameManager game = sampleGameManager(party);
        game.setDatabaseId(23);
        party.addGame(game);
        party.setCurrentGame(23);

        return party;
    }

    private static GameManager sampleGameManager(Party party){
        return new GameManager(party, new long[]{1, 2, 3, 4, 5});
    }

    private static GameSettings defaultSettings() {
        return new GameSettings(2, true);
    }

}
