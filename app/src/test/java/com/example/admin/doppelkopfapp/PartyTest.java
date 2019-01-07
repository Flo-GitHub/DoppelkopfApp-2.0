package com.example.admin.doppelkopfapp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PartyTest {

    @Test
    public void testAddPlayer() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "P"));
        Party party = new Party(players, null, null);
        party.addPlayer(new Player(2, "willi"));
        assertThat(party.getPlayers().size(), is(2));
    }
}
