package com.example.admin.doppelkopfapp;

import java.util.List;

public class PartyManager {

    List<Party> parties;

    public PartyManager(List<Party> parties) {
        this.parties = parties;
    }

    public void addParty(Party party) {
        parties.add(party);
    }

    public List<Party> getParties() {
        return parties;
    }
}
