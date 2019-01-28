package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.view.View;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    //todo update when pulling db extension from github
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
    }

    public static Party sampleParty() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Player1"));
        players.add(new Player(2, "Player2"));
        players.add(new Player(3, "Player3"));
        players.add(new Player(4, "Player4"));
        GameSettings settings = new GameSettings(1, false, false, false, false);
        Party party = new Party("This is the coolest group in the world", players, settings, "Jan 14, 2018");
        GameManager manager = new GameManager(new long[]{1, 2, 3, 4});
        party.addGame(manager);
        return party;
    }

}
