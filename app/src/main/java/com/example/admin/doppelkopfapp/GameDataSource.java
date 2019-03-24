package com.example.admin.doppelkopfapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.admin.doppelkopfapp.GameDBHelper.*;


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


    //PARTY START
    public long createParty(Party party) {
        ContentValues values = partyValues(party);
        long id = database.insert(TABLE_PARTY, null, values);
        createSettings(MyUtils.defaultSettings(), id);
        for(Player p : party.getPlayers()) {
            long playerId = createPlayer(p, id);
            p.setDataBaseId(playerId);
            Log.e("Player", p.getDataBaseId() + "");
        }
        return id;
    }

    public void updateParty(Party party) {
        ContentValues values = partyValues(party);
        long partyId = database.update(TABLE_PARTY,  values,
                COLUMN_ID + " = " + party.getDatabaseId(), null);
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
                        c.getString(c.getColumnIndex(COLUMN_LAST_DATE))
                );
                party.setDatabaseId(partyID);
                byte[] imageBytes = c.getBlob(c.getColumnIndex(COLUMN_IMAGE));
                party.setImageBytes(imageBytes);
                party.setSettings(getSettings(partyID));
                party.setGames(getAllGamesInParty(party));

                parties.add(party);

            } while(c.moveToNext());
        }

        c.close();
        return parties;
    }

    public void deleteDeepParty(Party party) {
        database.delete(TABLE_PARTY, COLUMN_ID + "=" + party.getDatabaseId(), null);
        database.delete(TABLE_PLAYERS, COLUMN_PARTY + "=" + party.getDatabaseId(), null);
        deleteSettings(party.getDatabaseId());
        for(GameManager game : party.getGames()){
            deleteDeepGame(game);
        }
    }
    //PARTY END


    //PLAYER START
    public long createPlayer(Player player, long partyId) {
        ContentValues values = playerValues(player, partyId);
        return database.insert(TABLE_PLAYERS, null, values);
    }

    public void updatePlayer(Player player, long gameId) {
        ContentValues values = playerValues(player, gameId);
        database.update(TABLE_PLAYERS, values, COLUMN_ID + " = " + player.getDataBaseId(), null);
    }


    public Player[] getAllPlayersInParty(long partyId) {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PARTY + " = " + partyId;

        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                Player player = new Player(c.getString(c.getColumnIndex(COLUMN_NAME)));
                player.setDataBaseId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                players.add(player);
            } while(c.moveToNext());
        }

        c.close();
        return players.toArray(new Player[players.size()]);
    }

    public void deletePlayer(Player player) {
        database.delete(TABLE_PLAYERS, COLUMN_ID + "=" + player.getDataBaseId(), null);
    }
    //PLAYER END


    //GAME START
    public long createGame(GameManager game, long partyId) {
        game.setLastDate(MyUtils.getDate());
        ContentValues values = gameValues(game, partyId);
        long id = database.insert(TABLE_GAME, null, values);
        for(long p : game.getPlayersDataBaseIds()) {
            createGamePlayer(id, p);
        }
        return id;
    }

    public void updateGame(Party party, GameManager game) {
        ContentValues values = gameValues(game, party.getDatabaseId());
        database.update(TABLE_GAME, values, COLUMN_ID + "=" + game.getDatabaseId(), null);
    }

    public List<GameManager> getAllGamesInParty(Party party) {
        String selectQuery = "SELECT * FROM " + TABLE_GAME + " WHERE " + COLUMN_PARTY + " = " + party.getDatabaseId();

        Cursor c = database.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        List<GameManager> games = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                long gameID = c.getLong(c.getColumnIndex(COLUMN_ID));

                GameManager game = new GameManager(party, getAllPlayersInGame(gameID));
                game.setDatabaseId(gameID);

                int[] bocks = new int[2];
                bocks[0] = c.getInt(c.getColumnIndex(COLUMN_BOCKS));
                bocks[1] = c.getInt(c.getColumnIndex(COLUMN_DOUBLE_BOCKS));
                game.setBocks(bocks);

                game.setDealerIndex(c.getInt(c.getColumnIndex(COLUMN_DEALER_INDEX)));
                game.setLastDate(c.getString(c.getColumnIndex(COLUMN_LAST_DATE)));
                game.setRounds(getAllRoundsInGame(gameID));

                games.add(game);
            } while (c.moveToNext());
        }

        c.close();
        return games;
    }

    public void deleteDeepGame(GameManager game) {
        database.delete(TABLE_GAME, COLUMN_ID + "=" + game.getDatabaseId(), null);
        for(GameRound round : game.getRounds()) {
            deleteDeepRound(round);
        }
        deleteGamePlayers(game.getDatabaseId());
    }
    //GAME - END

    public void createGamePlayer(long gameId, long playerId) {
        ContentValues values = gamePlayerValues(gameId, playerId);
        database.insert(TABLE_GAME_PLAYERS, null, values);
    }

    public void deleteGamePlayer(long gameId, long playerId) {
        database.delete(TABLE_GAME_PLAYERS, COLUMN_GAME + "=" + gameId +
                " and " + COLUMN_PLAYER + "=" + playerId, null);
    }

    private void deleteGamePlayers(long gameId){
        database.delete(TABLE_GAME_PLAYERS, COLUMN_GAME + "=" + gameId, null);
    }

    public long[] getAllPlayersInGame(long gameId) {
        String selectQuery = "SELECT * FROM " + TABLE_GAME_PLAYERS + " WHERE " + COLUMN_GAME + " = " + gameId;
        Cursor c = database.rawQuery(selectQuery, null);

        long[] players = new long[c.getCount()];
        int i = 0;

        if(c.moveToFirst()) {
            do {
                players[i] = c.getLong(c.getColumnIndex(COLUMN_PLAYER) );
                i++;
            } while(c.moveToNext());
        }

        c.close();
        return players;
    }


    //SETTINGS - START
    public long createSettings(GameSettings settings, long partyId) {
        ContentValues values = settingsValues(settings, partyId);
        return database.insert(TABLE_SETTINGS, null, values);
    }

    public void updateSettings(GameSettings settings, long partyId) {
        ContentValues values = settingsValues(settings, partyId);

        database.update(TABLE_SETTINGS, values, COLUMN_PARTY + " = " + partyId, null);
    }

    public GameSettings getSettings(long partyId) {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_PARTY + " = " + partyId;

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameSettings gameSettings = new GameSettings(
                c.getInt(c.getColumnIndex(COLUMN_MAX_BOCKS)),
                c.getInt(c.getColumnIndex(COLUMN_IS_SOLO_BOCK_CALCULATION)) == 1
        );
        gameSettings.setCentPerPoint(c.getInt(c.getColumnIndex(COLUMN_CENT_PER_POINT)));
        gameSettings.setAddPoints(c.getInt(c.getColumnIndex(COLUMN_IS_ADD_POINTS)) == 1);
        c.close();

        return gameSettings;
    }

    public void deleteSettings(long partyId) {
        database.delete(TABLE_SETTINGS, COLUMN_PARTY + "=" + partyId, null);
    }
    //SETTINGS - END


    //ROUND - START
    public long createRound(GameRound round, long gameId) {
        ContentValues values = roundValues(gameId, round);
        long roundId = database.insert(TABLE_ROUND, null, values);
        round.setDataBaseId(roundId);
        for(long playerId : round.getPlayerPoints().keySet()) {
            createPlayerRound(roundId, playerId, round.getPlayerPoints().get(playerId));
        }
        return roundId;
    }

    public void updateRound(GameRound round, long gameId) {
        ContentValues values = roundValues(gameId, round);
        database.update(TABLE_ROUND, values, COLUMN_ID + "="  + round.getDataBaseId(), null);
        for(long playerId : round.getPlayerPoints().keySet()) {
            updatePlayerRound(round.getDataBaseId(), playerId, round.getPlayerPoints().get(playerId));
        }
    }

    public void deleteDeepRound(GameRound round) {
        database.delete(TABLE_ROUND, COLUMN_ID + "=" + round.getDataBaseId(), null);
        deletePlayerRounds(round);
    }

    public List<GameRound> getAllRoundsInGame(long gameId) {
        List<GameRound> rounds = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ROUND + " WHERE " +
                COLUMN_GAME + " = " + gameId;
        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndex(COLUMN_ID));
                getPlayerPoints(id);
                GameRound round = new GameRound(getPlayerPoints(id));
                round.setDataBaseId(id);
                round.setCurrentBocks(c.getInt(c.getColumnIndex(COLUMN_CURRENT_ROUND_BOCKS)));
                round.setNewBocks(c.getInt(c.getColumnIndex(COLUMN_NEW_BOCKS)));
                rounds.add(round);
            } while (c.moveToNext());
        }
        c.close();
        return rounds;
    }
    //ROUND - END


    //PLAYER ROUND - START
    private void createPlayerRound(long roundId, long playerId, int points) {
        ContentValues values = playerRoundValues(roundId, playerId, points);
        database.insert(TABLE_PLAYER_ROUND, null, values);
    }

    private void updatePlayerRound(long roundId, long playerId, int points) {
        ContentValues values = playerRoundValues(roundId, playerId, points);
        database.update(TABLE_PLAYER_ROUND, values, COLUMN_ROUND+ "=" + roundId +
                " and " + COLUMN_PLAYER + "=" + playerId, null);
    }

    private void deletePlayerRounds(GameRound round) {
        database.delete(TABLE_PLAYER_ROUND, COLUMN_ROUND + "=" + round.getDataBaseId(), null);
    }

    private HashMap<Long, Integer> getPlayerPoints(long roundId) {
        HashMap<Long, Integer> playerPoints = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_PLAYER_ROUND + " WHERE " +
                COLUMN_ROUND + "=" + roundId;
        Cursor c = database.rawQuery(selectQuery, null);
        if(c.moveToFirst()) {
            do {
                playerPoints.put(c.getLong(c.getColumnIndex(COLUMN_PLAYER)),
                        c.getInt(c.getColumnIndex(COLUMN_POINTS)));
            } while(c.moveToNext());
        }

        c.close();
        return playerPoints;
    }
    //PlAYER ROUND - END


    //CONTENTVALUES
    private ContentValues partyValues(Party party){
         ContentValues values = new ContentValues();
         values.put(COLUMN_NAME, party.getName());
         values.put(COLUMN_IMAGE, party.getImageBytes());
         values.put(COLUMN_LAST_DATE, party.getLastDate());
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
        values.put(COLUMN_BOCKS, game.getBockSafe(0));
        values.put(COLUMN_DOUBLE_BOCKS, game.getBockSafe(1));
        values.put(COLUMN_DEALER_INDEX, game.getDealerIndex());
        values.put(COLUMN_LAST_DATE, game.getLastDate());
        return values;
    }

    private ContentValues gamePlayerValues(long gameId, long playerId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_PLAYER, playerId);
        return values;
    }

    private ContentValues settingsValues(GameSettings settings, long partyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, partyId);
        values.put(COLUMN_CENT_PER_POINT, settings.getCentPerPoint());
        values.put(COLUMN_MAX_BOCKS, settings.getMaxBocks());
        values.put(COLUMN_IS_SOLO_BOCK_CALCULATION, settings.isSoloBockCalculation() ? 1 : 0);
        values.put(COLUMN_IS_ADD_POINTS, settings.isAddPoints() ? 1 : 0);
        return values;
    }

    private ContentValues roundValues(long gameId, GameRound round){
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_NEW_BOCKS, round.getNewBocks());
        values.put(COLUMN_CURRENT_ROUND_BOCKS, round.getCurrentBocks());
        return values;
    }

    private ContentValues playerRoundValues(long roundId, long playerId, int points) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUND, roundId);
        values.put(COLUMN_PLAYER, playerId);
        values.put(COLUMN_POINTS, points);
        return values;
    }

}
