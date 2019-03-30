package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static long getDate() {
        return new Date().getTime();
    }

    private static boolean isToday(long date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal2.setTime(new Date(date));
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static String getFullDisplayDate(long s) {
        Date date = new Date(s);
        DateFormat fullFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        return fullFormat.format(date);
    }

    public static String getDisplayDate(long s){
        Date date = new Date(s);
        if(isToday(s)) {
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            return timeFormat.format(date);
        } else {
            DateFormat dateFormat = DateFormat.getDateInstance();
            return dateFormat.format(date);
        }
    }

    public static int compareDates(long first, long second) {
        Date firstDate = new Date(first);
        Date secondDate = new Date(second);
        return -firstDate.compareTo(secondDate);//inverse because of order in time
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
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        players.add(new Player("Player3"));
        players.add(new Player("Player4"));
        players.add(new Player("Player5555"));

        Party party = new Party("This is the coolest group in the world", players, 223423232);
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
