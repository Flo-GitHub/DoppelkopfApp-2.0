package com.example.admin.doppelkopfapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_BOCKS;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_CENT_PER_POINT;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_DOUBLE_BOCKS;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_GAME;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_GIVER_INDEX;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_ID;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_IS_ADD_POINTS;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_IS_BOCK;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_IS_DOUBLE_BOCK;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_IS_SOLO_BOCK_CALCULATION;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_NAME;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_PARTY;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_PLAYER;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_POINTS;
import static com.example.admin.doppelkopfapp.GameDBHelper.COLUMN_POINTS_LOST;
import static com.example.admin.doppelkopfapp.GameDBHelper.TABLE_GAME;
import static com.example.admin.doppelkopfapp.GameDBHelper.TABLE_PARTY;
import static com.example.admin.doppelkopfapp.GameDBHelper.TABLE_PLAYERS;
import static com.example.admin.doppelkopfapp.GameDBHelper.TABLE_POINTS;
import static com.example.admin.doppelkopfapp.GameDBHelper.TABLE_SETTINGS;

public class GameDataSource {

    private static final String LOG = "GmDataSource";

    private SQLiteDatabase database;
    private GameDBHelper dbHelper;


    public GameDataSource(Context context) {
        dbHelper = new GameDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createParty(Party party) {
        ContentValues values = partyValues(party);
        long partyId = database.insert(TABLE_PARTY, null, values);
        return partyId;
    }

    public void updateParty(Party party) {
        ContentValues values = partyValues(party);
        long partyId = database.update(TABLE_PARTY,  values,
                COLUMN_ID + " = " + party.getDatabaseId(), null);

        updateGame(party.getCurrentGame(), partyId);

        for( Player p : party.getPlayersByDBId(party.getCurrentGame().getPlayersDataBaseIds())) {
            updatePlayer(p, party.getCurrentGame().getDatabaseId()); //insert players
        }

        updateSettings(party.getSettings(), partyId); //insert settings
    }


    public List<Party> getAllParties() {
        String selectQuery = "SELECT * FROM " + TABLE_PARTY;

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        List<Party> parties = new ArrayList<>();

        if(c.moveToFirst()) {
            do {
                long partyID = c.getLong(c.getColumnIndex(COLUMN_ID));
                Party party = new Party(
                        c.getString(c.getColumnIndex(COLUMN_NAME)),
                        Arrays.asList(getAllPlayersInParty(partyID)),
                        getSettings(partyID)
                );
                party.addGames(getAllGamesInParty(party));
                parties.add(party);

            } while(c.moveToNext());
        }

        c.close();
        return parties;
    }

    public long getNextPlayerId() {
        return getNextId(TABLE_PLAYERS);
    }

    public long getNextGameId() {
        return getNextId(TABLE_GAME);
    }

    private long getNextId( String tableName ) {
        Cursor c = null;
        long seq = 0;
        try {
            String sql = "select seq from sqlite_sequence where name=?";
            c = database.rawQuery(sql, new String[] {tableName});
            if (c.moveToFirst()) {
                seq = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return seq + 1;
    }

   /* public boolean hasGame() {
        try {
            GameManager gameManager = getNewestGame();
            if( gameManager != null ) {
                return true;
            }
        } catch (Exception e){
            Log.d(LOG, "No game in database");
        }
        return false;
    }*/


    public long createGame(GameManager game, long partyId) {
        ContentValues values = gameValues(game, partyId);
        long gameId = database.insert(TABLE_GAME, null, values); //insert game

        return gameId;
    }

    public void updateGame(GameManager game, long partyId) {
        ContentValues values = gameValues(game, partyId);
        database.update(TABLE_GAME, values, COLUMN_ID + "=" + game.getDatabaseId(), null);
    }


    public List<GameManager> getAllGamesInParty(Party party) {
        String selectQuery = "SELECT * FROM " + TABLE_GAME + " WHERE " + COLUMN_PARTY + " = " + party.getDatabaseId();

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        List<GameManager> games = new ArrayList<>();

        if(c.moveToFirst()) {
            do {
                long gameID = c.getLong(c.getColumnIndex(COLUMN_ID));

                GameManager game = new GameManager(party, getAllPlayersInGame(gameID));
                game.setBocks( c.getInt( c.getColumnIndex(COLUMN_BOCKS) )  );
                game.setDoubleBocks( c.getInt( c.getColumnIndex(COLUMN_DOUBLE_BOCKS) ) );
                game.setGiverIndex( c.getInt( c.getColumnIndex(COLUMN_GIVER_INDEX) ) );
                game.setDatabaseId(gameID);

                games.add(game);
            } while(c.moveToNext());
        }

        c.close();
        return games;
    }


    public long createPlayer(Player player, long gameId) {
        ContentValues values = playerValues(player, gameId);
        long playerId = database.insert(TABLE_PLAYERS, null, values);
        createPoints(player, gameId);
        return playerId;
    }

    public void updatePlayer(Player player, long gameId) {
        ContentValues values = playerValues(player, gameId);

        database.update(TABLE_PLAYERS, values, COLUMN_ID + " = " + player.getDataBaseId(), null);
        updatePoints(player, gameId);
    }

    public void deletePlayer(Player player) {
        database.delete(TABLE_PLAYERS, COLUMN_ID + "=" + player.getDataBaseId(), null);
    }

    public void createPoints(Player player, long gameId) {
       ContentValues values = pointsValues(player, gameId);
       database.insert(TABLE_POINTS, null, values);
    }


    public void updatePoints(Player player, long gameId) {
        ContentValues value = pointsValues(player, gameId);
        database.update(TABLE_POINTS, value, COLUMN_PLAYER + " = " + player.getDataBaseId() +
                " AND " + COLUMN_GAME + " = " + gameId, null);

    }

    public long createSettings(GameSettings settings, long partyId) {
        ContentValues values = settingsValues(settings, partyId);
        long settingsId = database.insert(TABLE_SETTINGS, null, values);

        return settingsId;
    }

    public void updateSettings(GameSettings settings, long partyId) {
        ContentValues values = settingsValues(settings, partyId);

        database.update(TABLE_SETTINGS, values, COLUMN_PARTY + " = " + partyId, null);
    }


    private HashMap<Long, Integer> getColumn(String points_column, long playerId) {

        HashMap<Long, Integer> map = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_PLAYER + " = " + playerId;
        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                map.put(c.getLong(c.getColumnIndex(COLUMN_GAME)),
                        c.getInt(c.getColumnIndex(points_column)));
            } while(c.moveToNext());
        }

        c.close();
        return map;
    }

    public HashMap<Long, Integer> getPoints(long playerId) {
        return getColumn(COLUMN_POINTS, playerId);
    }

    public HashMap<Long, Integer> getPointsLost(long playerId) {
        return getColumn(COLUMN_POINTS_LOST, playerId);
    }

    //todo refactor
    private Player[] getAllPlayersInParty(long partyId) {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PARTY + " = " + partyId;

        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                Player player = new Player(c.getInt(c.getColumnIndex(COLUMN_ID)),c.getString(c.getColumnIndex(COLUMN_NAME)));
                player.setPoints(getPoints(c.getInt(c.getColumnIndex(COLUMN_ID))));
                player.setPointsLost(getPointsLost(c.getInt(c.getColumnIndex(COLUMN_ID))));
                players.add(player);
            } while(c.moveToNext());
        }

        c.close();
        return players.toArray(new Player[players.size()]);
    }

    private long[] getAllPlayersInGame(long gameId) {
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_GAME + " = " + gameId;
        Cursor c = database.rawQuery(selectQuery, null);

        long[] players = new long[c.getCount()];
        int i = 0;

        if(c.moveToFirst()) {
            do {
                players[i] = c.getLong(c.getColumnIndex(COLUMN_ID) );
                i++;
            } while(c.moveToNext());
        }

        c.close();
        return players;
    }


   //all good below here

    public GameSettings getSettings(long partyId) {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_PARTY + " = " + partyId;

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameSettings gameSettings = new GameSettings(
                c.getInt( c.getColumnIndex(COLUMN_CENT_PER_POINT) ),
                c.getInt(c.getColumnIndex(COLUMN_IS_BOCK)) == 1,
                c.getInt(c.getColumnIndex(COLUMN_IS_DOUBLE_BOCK)) == 1,
                c.getInt(c.getColumnIndex(COLUMN_IS_SOLO_BOCK_CALCULATION)) == 1,
                c.getInt(c.getColumnIndex(COLUMN_IS_ADD_POINTS)) == 1
        );
        c.close();
        return gameSettings;
    }


    //contentValues

    private ContentValues settingsValues(GameSettings settings, long partyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, partyId);
        values.put(COLUMN_CENT_PER_POINT, settings.getCentPerPoint());
        values.put(COLUMN_IS_BOCK, settings.isBock() ? 1 : 0 );
        values.put(COLUMN_IS_DOUBLE_BOCK, settings.isDoubleBock() ? 1 : 0);
        values.put(COLUMN_IS_SOLO_BOCK_CALCULATION, settings.isSoloBockCalculation() ? 1 : 0);
        values.put(COLUMN_IS_ADD_POINTS, settings.isAddPoints() ? 1 : 0);
        return values;
    }

    private ContentValues playerValues(Player player, long partyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, partyId);
        values.put(COLUMN_NAME, player.getName());
        return values;
    }

    private ContentValues gameValues(GameManager game, long partyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, partyId);
        values.put(COLUMN_BOCKS, game.getBocks());
        values.put(COLUMN_DOUBLE_BOCKS, game.getDoubleBocks());
        values.put(COLUMN_GIVER_INDEX, game.getGiverIndex());
        return values;
    }

    private ContentValues pointsValues(Player player, long gameId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, player.getPoints().get(gameId));
        values.put(COLUMN_POINTS_LOST, player.getPointsLost().get(gameId));
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_PLAYER, player.getDataBaseId());
        return values;
    }

    private ContentValues partyValues(Party party){
         ContentValues values = new ContentValues();
         values.put(COLUMN_NAME, party.getName());
         return values;
    }


}
