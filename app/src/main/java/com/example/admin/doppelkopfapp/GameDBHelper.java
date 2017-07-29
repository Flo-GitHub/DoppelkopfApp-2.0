package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that extends SQLiteOpenHelper and has got all the names and columns of the database in it.
 */

public class GameDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "game.db";
    public static final int    DB_VERSION = 2;

    public static final String TABLE_PLAYERS = "table_players";
    public static final String TABLE_SETTINGS = "table_settings";
    public static final String TABLE_GAME = "table_game";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME = "game";
    public static final String COLUMN_BOCKS = "bocks";
    public static final String COLUMN_DOUBLE_BOCKS = "doubleBocks";
    public static final String COLUMN_GIVER_INDEX = "giverIndex";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_POINTS_LOST = "pointsLost";
    public static final String COLUMN_CENT_PER_POINT = "centPerPoint";
    public static final String COLUMN_IS_BOCK = "isBock";
    public static final String COLUMN_IS_DOUBLE_BOCK = "isDoubleBock";
    public static final String COLUMN_IS_SOLO_BOCK_CALCULATION = "soloBockCalculation";


    public static final String SQL_CREATE_PLAYER =
            "create table " + TABLE_PLAYERS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_GAME + " integer, " +
                    COLUMN_NAME + " text not null, " +
                    COLUMN_POINTS + " integer, " +
                    COLUMN_POINTS_LOST + " integer, " +
                    "FOREIGN KEY (" + COLUMN_GAME + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_ID + "));";

    public static final String SQL_CREATE_SETTINGS =
            "create table " + TABLE_SETTINGS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_GAME + " integer, " +
                    COLUMN_CENT_PER_POINT + " integer, " +
                    COLUMN_IS_BOCK + " integer, " +
                    COLUMN_IS_DOUBLE_BOCK + " integer, " +
                    COLUMN_IS_SOLO_BOCK_CALCULATION + " integer, " +
                    "FOREIGN KEY (" + COLUMN_GAME + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_ID + "));";

    public static final String SQL_CREATE_GAME =
            "create table " + TABLE_GAME + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_BOCKS + " integer, " +
                    COLUMN_DOUBLE_BOCKS + " integer, " +
                    COLUMN_GIVER_INDEX  + " integer);";


    public GameDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLAYER);
        db.execSQL(SQL_CREATE_SETTINGS);
        db.execSQL(SQL_CREATE_GAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PLAYERS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SETTINGS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_GAME );

        onCreate(db);
    }

}
