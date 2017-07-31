package com.example.admin.doppelkopfapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.admin.doppelkopfapp.GameDBHelper.*;

public class GameManagerDataSource {

    private static final String LOG = "GmDataSource";

    private SQLiteDatabase database;
    private GameDBHelper dbHelper;


    public GameManagerDataSource(Context context) {
        dbHelper = new GameDBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
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


    public boolean hasGame() {
        try {
            GameManager gameManager = getNewestGame();
            if( gameManager != null ) {
                return true;
            }
        } catch (Exception e){
            Log.d(LOG, "No game in database");
        }
        return false;
    }

    public long createGame(GameManager game) {
        ContentValues values = gameValues(game);
        long gameId = database.insert(TABLE_GAME, null, values); //insert game

        for( Player p : game.getPlayers() ) {
            createPlayer(p, gameId); //insert players
        }

        createSettings(game.getSettings(), gameId); //insert settings

        return gameId;
    }

    public void updateGame(GameManager game) {
        ContentValues values = gameValues(game);
        database.update(TABLE_GAME, values, COLUMN_ID + "=" + game.getDatabaseId(), null);

        for( Player p : game.getPlayers() )
            updatePlayer(p, game.getDatabaseId());

        updateSettings(game.getSettings(), game.getDatabaseId() );
    }

    public GameManager getNewestGame() {
        String selectQuery = "SELECT * FROM " +  TABLE_GAME + " WHERE + " + COLUMN_ID +
                "= (SELECT MAX(" + COLUMN_ID + ")  FROM " + TABLE_GAME + ")";

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToLast();

        GameManager gameManager = new GameManager(
                getAllPlayers(getNextGameId()-1),
                getSettings(getNextGameId()-1));

        gameManager.setDatabaseId( getNextGameId()-1 );
        int bocks = c.getInt( c.getColumnIndex(COLUMN_BOCKS) );
        int doubleBocks = c.getInt( c.getColumnIndex(COLUMN_DOUBLE_BOCKS) );
        gameManager.setBocks( bocks );
        gameManager.setDoubleBocks( doubleBocks );
        gameManager.setGiverIndex( c.getInt( c.getColumnIndex(COLUMN_GIVER_INDEX) ) );

        c.close();
        return gameManager;
    }

    public GameManager getGame(long gameID) {
        String selectQuery = "SELECT * FROM " + TABLE_GAME + " WHERE " + COLUMN_ID + " = " + gameID;

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameManager gameManager = new GameManager(
                getAllPlayers(gameID),
                getSettings( gameID ));
        gameManager.setBocks( c.getInt( c.getColumnIndex(COLUMN_BOCKS) )  );
        gameManager.setDoubleBocks( c.getInt( c.getColumnIndex(COLUMN_DOUBLE_BOCKS) ) );
        gameManager.setGiverIndex( c.getInt( c.getColumnIndex(COLUMN_GIVER_INDEX) ) );

        gameManager.setDatabaseId(gameID);

        c.close();
        return gameManager;
    }


    public long createPlayer(Player player, long gameId) {
        ContentValues values = playerValues(player, gameId);
        long playerId = database.insert(TABLE_PLAYERS, null, values);

        return playerId;
    }

    public void updatePlayer(Player player, long gameId) {
        ContentValues values = playerValues(player, gameId);

        database.update(TABLE_PLAYERS, values, COLUMN_ID + " = " + player.getDataBaseId(), null);
    }

    public long createSettings(GameSettings settings, long gameId) {
        ContentValues values = settingsValues(settings, gameId);
        long settingsId = database.insert(TABLE_SETTINGS, null, values);

        return settingsId;
    }

    public void updateSettings(GameSettings settings, long gameId) {
        ContentValues values = settingsValues(settings, gameId);

        database.update(TABLE_SETTINGS, values, COLUMN_GAME + " = " + gameId, null);
    }



    public Player[] getAllPlayers(long gameID) {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_GAME + " = " + gameID;

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

    public GameSettings getSettings(long gameId) {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_GAME + " = " + gameId;

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameSettings gameSettings = new GameSettings(
                c.getInt( c.getColumnIndex(COLUMN_CENT_PER_POINT) ),
                c.getInt( c.getColumnIndex(COLUMN_IS_BOCK) ) == 1 ? true : false,
                c.getInt( c.getColumnIndex(COLUMN_IS_DOUBLE_BOCK) ) == 1 ? true : false,
                c.getInt( c.getColumnIndex(COLUMN_IS_SOLO_BOCK_CALCULATION) ) == 1 ? true : false
        );
        c.close();
        return gameSettings;
    }


    //contentValues
    private ContentValues settingsValues(GameSettings settings, long gameId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_CENT_PER_POINT, settings.getCentPerPoint());
        values.put(COLUMN_IS_BOCK, settings.isBock() ? 1 : 0 );
        values.put(COLUMN_IS_DOUBLE_BOCK, settings.isDoubleBock() ? 1 : 0);
        values.put(COLUMN_IS_SOLO_BOCK_CALCULATION, settings.isSoloBockCalculation() ? 1 : 0);
        return values;
    }

    private ContentValues playerValues(Player player, long gameId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, gameId);
        values.put(COLUMN_NAME, player.getName());
        values.put(COLUMN_POINTS, player.getPoints());
        values.put(COLUMN_POINTS_LOST, player.getPointsLost());
        return values;
    }

    private ContentValues gameValues(GameManager game) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOCKS, game.getBocks());
        values.put(COLUMN_DOUBLE_BOCKS, game.getDoubleBocks());
        values.put(COLUMN_GIVER_INDEX, game.getGiverIndex());
        return values;
    }


}
