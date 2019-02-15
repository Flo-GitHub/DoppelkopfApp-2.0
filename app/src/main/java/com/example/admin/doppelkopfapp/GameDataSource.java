package com.example.admin.doppelkopfapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.admin.doppelkopfapp.GameDBHelper.*;


public class GameDataSource {

/*    private static final String LOG = "GmDataSource";

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
        return database.insert(TABLE_PARTY, null, values);
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
                Player player = new Player(c.getInt(c.getColumnIndex(COLUMN_ID)),c.getString(c.getColumnIndex(COLUMN_NAME)));
                player.setPoints(c.getInt(c.getColumnIndex(COLUMN_POINTS)));
                player.setPointsLost(c.getInt(c.getColumnIndex(COLUMN_POINTS_LOST)));
                players.add(player);
            } while(c.moveToNext());
        }

        c.close();
        return players.toArray(new Player[players.size()]);
    }

    public long[] getAllPlayersInGame(long gameId) {
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

    public void deletePlayer(Player player) {
        database.delete(TABLE_PLAYERS, COLUMN_ID + "=" + player.getDataBaseId(), null);
    }
    //PLAYER END


    //GAME START
    public long createGame(GameManager game, long partyId) {
        ContentValues values = gameValues(game, partyId);
        long gameId = database.insert(TABLE_GAME, null, values);
        return gameId;
    }

    public void updateGame(Party party, GameManager game, long partyId) {
        ContentValues values = gameValues(game, partyId);
        database.update(TABLE_GAME, values, COLUMN_ID + "=" + game.getDatabaseId(), null);

        Player[] players = party.getPlayersByDBId(game.getPlayersDataBaseIds());
        for(Player p : players) {
            updatePlayer(p, game.getDatabaseId());
        }
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

                GameManager game = new GameManager(party, getSettings(gameID), getAllPlayersInGame(gameID));
                game.setBocks(c.getInt(c.getColumnIndex(COLUMN_BOCKS)));
                game.setDoubleBocks(c.getInt(c.getColumnIndex(COLUMN_DOUBLE_BOCKS)));
                game.setGiverIndex(c.getInt(c.getColumnIndex(COLUMN_GIVER_INDEX)));
                game.setLastDate(c.getString(c.getColumnIndex(COLUMN_LAST_DATE)));
                game.setDatabaseId(gameID);

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
    }
    //GAME - END


    //SETTINGS - START
    public long createSettings(GameSettings settings, long gameId) {
        ContentValues values = settingsValues(settings, gameId);
        long settingsId = database.insert(TABLE_SETTINGS, null, values);

        return settingsId;
    }

    public void updateSettings(GameSettings settings, long gameId) {
        ContentValues values = settingsValues(settings, gameId);

        database.update(TABLE_SETTINGS, values, COLUMN_PARTY + " = " + gameId, null);
    }

    public GameSettings getSettings(long gameId) {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_GAME + " = " + gameId;

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

    public void deleteSettings(long partyId) {
        database.delete(TABLE_SETTINGS, COLUMN_PARTY + "=" + partyId, null);
    }
    //SETTINGS - END


    //ROUND - START
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
                rounds.add(new GameRound(id));
            } while (c.moveToNext());
        }
        c.close();
        return rounds;
    }
    //ROUND - END


    //PLAYER ROUND - START
    private void deletePlayerRounds(GameRound round) {
        database.delete(TABLE_PLAYER_ROUND, COLUMN_ROUND + "=" + round.getDataBaseId(), null);
    }

    private HashMap<Long, Integer> getPlayerPoints(long roundId) {
        HashMap<Long, Integer> playerPoints = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_PLAYER_ROUND + " WHERE " +
                COLUMN_ROUND + " = " + roundId;
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
         values.put(COLUMN_LAST_DATE, party.getLastDate());
         return values;
    }

    //todo
    private ContentValues playerValues(Player player, long partyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, partyId);
        values.put(COLUMN_NAME, player.getName());
        values.put(COLUMN_POINTS, player.getPoints());
        values.put(COLUMN_POINTS_LOST, player.getPointsLost());
        return values;
    }


    private ContentValues gameValues(GameManager game, long gameId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTY, gameId);
        values.put(COLUMN_BOCKS, game.getBocks());
        values.put(COLUMN_DOUBLE_BOCKS, game.getDoubleBocks());
        values.put(COLUMN_GIVER_INDEX, game.getGiverIndex());
        values.put(COLUMN_LAST_DATE, game.getLastDate());
        return values;
    }

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
//TODO TODO TODO TODO TODO
    private ContentValues roundValues(long gameId, GameRound round){
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_DATE, round.getDate());
        values.put(COLUMN_NEW_BOCKS, round.getNewBocks());
        values.put(COLUMN_CURRENT_ROUND_BOCKS, round.getCurrentRoundBock());
        values.put(COLUMN_RE_ANNOUNCEMENT, round.getReAnnouncement());
        values.put(COLUMN_RE_BONUS, round.getReBonusPoints());
        values.put(COLUMN_CON_ANNOUNCEMENT, round.getConAnnouncement());
        values.put(COLUMN_CON_BONUS, round.getConBonusPoints());
        return values;
    }

    private ContentValues playerRoundValues(long roundId, long playerId, int points) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUND, roundId);
        values.put(COLUMN_PLAYER, playerId);
        values.put(COLUMN_POINTS, points);
        return values;
    }

*/

}
