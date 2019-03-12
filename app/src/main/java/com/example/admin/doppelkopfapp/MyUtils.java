package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.view.View;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by delv on 8/1/2017.
 */

public class MyUtils {

    public static View findViewByName(Activity context, String name) {
        return context.findViewById(context.getResources().getIdentifier(name, "id", context.getPackageName()));
    }

    public static String getDate() {
        DateFormat format = DateFormat.getDateInstance();
        Date date = new Date();
        return format.format(date);
    }

    public static String getPlayersAsString(List<Player> players) {
        StringBuilder builder = new StringBuilder();
        for(Player p : players) {
            builder.append(p.getName() + ", ");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length()-2);
        }
        return builder.toString();
    }
    /*todo update when pulling db extension from github
    public static void createPartyDataSource(GameDataSource dataSource) {
        Party party = sampleParty();
        party.setDatabaseId(dataSource.createParty(party));
        for(GameManager game : party.getGames()) {
            dataSource.createGame(game, party.getDatabaseId());
        }
        dataSource.createSettings(party.getSettings(), party.getDatabaseId());
        for(Player p : party.getPlayers()) {
            dataSource.createPlayer(p, party.getDatabaseId());
        }
    }*/

    public static GameSettings defaultSettings() {
        return new GameSettings(2, true);
    }

    public static GameManager sampleGameManager(Party party){
        return new GameManager(party, new long[]{1, 2, 3, 4, 5});
    }

    public static Party sampleParty() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Player1"));
        players.add(new Player(2, "Player2"));
        players.add(new Player(3, "Player3"));
        players.add(new Player(4, "Player4"));
        players.add(new Player(5, "Player5555"));

        Party party = new Party("This is the coolest group in the world", players, "Jan 14, 2018");
        for(int i = 0; i < 3; i++) {
            GameManager game = sampleGameManager(party);
            game.setDatabaseId(i + party.getDatabaseId() * 1000);
            party.addGame(game);
        }
        return party;
    }

    public static PartyManager samplePartyManager() {
        PartyManager partyManager = new PartyManager();
        for(int i = 0; i < 3; i++) {
            Party party = sampleParty();
            party.setDatabaseId(i);
            partyManager.addParty(party);
        }
        return partyManager;
    }

}
