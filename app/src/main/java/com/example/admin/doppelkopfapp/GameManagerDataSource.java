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

/**
 * Created by Admin on 12/07/2017.
 */

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

    public int getPlayerCountInDb() {
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS;
        Cursor c = database.rawQuery(selectQuery, null);
        return c.getCount();
    }

    public int getGameCountInDb() {
        String selectQuery = "SELECT * FROM " + TABLE_GAME;
        Cursor c = database.rawQuery(selectQuery, null);
        return c.getCount();
    }

    public int getSettingsCountInDb() {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS;
        Cursor c = database.rawQuery(selectQuery, null);
        return c.getCount();
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
        createSettings(game.getSettings()); //insert settings

        ContentValues values = gameValues(game);
        long gameId = database.insert(TABLE_GAME, null, values); //insert game

        for( Player p : game.getPlayers() ) {
            createPlayer(p, gameId); //insert players
        }

        return gameId;
    }

    public void updateGame(GameManager game) {
        updateSettings(game.getSettings());

        ContentValues values = gameValues(game);
        database.update(TABLE_GAME, values, COLUMN_ID + "=" + game.getDatabaseId(), null);

        for( Player p : game.getPlayers() )
            updatePlayer(p, game.getDatabaseId());
    }

    public GameManager getNewestGame() {
        String selectQuery = "SELECT * FROM " + TABLE_GAME;

        Log.e(LOG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToLast();

        GameManager gameManager = new GameManager(
                c.getPosition(),
                getAllPlayers(c.getCount()-1),
                getSettings( c.getInt( c.getColumnIndex(COLUMN_ID) ) ) );
        gameManager.setBocks( c.getInt( c.getColumnIndex(COLUMN_BOCKS) )  );
        gameManager.setDoubleBocks( c.getInt( c.getColumnIndex(COLUMN_DOUBLE_BOCKS) ) );
        gameManager.setGiverIndex( c.getInt( c.getColumnIndex(COLUMN_GIVER_INDEX) ) );

        c.close();
        return gameManager;
    }

    public GameManager getGame(long gameID) {
        String selectQuery = "SELECT * FROM " + TABLE_GAME + " WHERE " + COLUMN_ID + " = " + gameID;

        Log.e(LOG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameManager gameManager = new GameManager(
                c.getPosition(),
                getAllPlayers(gameID),
                getSettings( c.getInt( c.getColumnIndex(COLUMN_ID) ) ) );
        gameManager.setBocks( c.getInt( c.getColumnIndex(COLUMN_BOCKS) )  );
        gameManager.setDoubleBocks( c.getInt( c.getColumnIndex(COLUMN_DOUBLE_BOCKS) ) );
        gameManager.setGiverIndex( c.getInt( c.getColumnIndex(COLUMN_GIVER_INDEX) ) );

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

        database.update(TABLE_PLAYERS, values, COLUMN_ID + "=" + player.getDataBaseId(), null);
    }

    public long createSettings(GameSettings settings) {
        ContentValues values = settingsValues(settings);
        long settingsId = database.insert(TABLE_SETTINGS, null, values);

        return settingsId;
    }

    public void updateSettings(GameSettings settings) {
        ContentValues values = settingsValues(settings);

        database.update(TABLE_SETTINGS, values, COLUMN_ID + "=" + settings.getDataBaseId(), null);
    }



    public Player[] getAllPlayers(long gameID) {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_GAME + " = " + gameID;

        Log.e(LOG, selectQuery);

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

    public GameSettings getSettings(long id) {
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_ID + " = " + id;

        Log.e(LOG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        if( c != null )
            c.moveToFirst();

        GameSettings gameSettings = new GameSettings(
                id,
                c.getInt( c.getColumnIndex(COLUMN_CENT_PER_POINT) ),
                c.getInt( c.getColumnIndex(COLUMN_IS_BOCK) ) == 1 ? true : false,
                c.getInt( c.getColumnIndex(COLUMN_IS_DOUBLE_BOCK) ) == 1 ? true : false,
                c.getInt( c.getColumnIndex(COLUMN_IS_SOLO_BOCK_CALCULATION) ) == 1 ? true : false
        );
        c.close();
        return gameSettings;
    }


    //contentvalues
    private ContentValues settingsValues(GameSettings settings) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CENT_PER_POINT, settings.getCentPerPoint());
        values.put(COLUMN_IS_BOCK, settings.isBock());
        values.put(COLUMN_IS_DOUBLE_BOCK, settings.isDoubleBock());
        values.put(COLUMN_IS_SOLO_BOCK_CALCULATION, settings.isSoloBockCalculation());
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
        values.put(COLUMN_SETTINGS, game.getSettings().getDataBaseId());
        values.put(COLUMN_BOCKS, game.getBocks());
        values.put(COLUMN_DOUBLE_BOCKS, game.getDoubleBocks());
        values.put(COLUMN_GIVER_INDEX, game.getGiverIndex());
        return values;
    }


}
