package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "game.db";
    public static final int    DB_VERSION = 4;

    public static final String TABLE_PLAYERS = "table_players";
    public static final String TABLE_SETTINGS = "table_settings";
    public static final String TABLE_GAME = "table_game";
    public static final String TABLE_PARTY = "table_group";
    public static final String TABLE_POINTS = "table_points";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_PARTY = "party";
    public static final String COLUMN_GAME = "game";
    public static final String COLUMN_BOCKS = "bocks";
    public static final String COLUMN_DOUBLE_BOCKS = "doubleBocks";
    public static final String COLUMN_GIVER_INDEX = "giverIndex";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_POINTS_LOST = "pointsLost";
    public static final String COLUMN_CENT_PER_POINT = "centPerPoint";
    public static final String COLUMN_IS_ADD_POINTS = "isAddPoints";
    public static final String COLUMN_IS_BOCK = "isBock";
    public static final String COLUMN_IS_DOUBLE_BOCK = "isDoubleBock";
    public static final String COLUMN_IS_SOLO_BOCK_CALCULATION = "soloBockCalculation";
    public static final String COLUMN_LAST_DATE = "lastDate";

    public static final String SQL_CREATE_PARTY =
            "create table " + TABLE_PARTY + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAST_DATE + " text not null, " +
                    COLUMN_NAME + " text not null);";

    public static final String SQL_CREATE_PLAYER =
            "create table " + TABLE_PLAYERS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PARTY + " integer, " +
                    COLUMN_NAME + " text not null, " +//todo text
                    "FOREIGN KEY (" + COLUMN_PARTY + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_ID + "));";

    public static final String SQL_CREATE_POINTS =
            "create table " + TABLE_POINTS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_POINTS + " integer, "  +
                    COLUMN_POINTS_LOST + " integer, " +
                    COLUMN_GAME + " integer, " +
                    COLUMN_PLAYER + " integer, " +
                    "FOREIGN KEY (" + COLUMN_GAME + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_PLAYER + ") REFERENCES " + TABLE_PLAYERS + "(" + COLUMN_ID + "));";


    public static final String SQL_CREATE_SETTINGS =
            "create table " + TABLE_SETTINGS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PARTY + " integer, " +
                    COLUMN_CENT_PER_POINT + " integer, " +
                    COLUMN_IS_BOCK + " integer, " +
                    COLUMN_IS_DOUBLE_BOCK + " integer, " +
                    COLUMN_IS_SOLO_BOCK_CALCULATION + " integer, " +
                    COLUMN_IS_ADD_POINTS + " integer, " +
                    "FOREIGN KEY (" + COLUMN_PARTY + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_ID + "));";

    public static final String SQL_CREATE_GAME =
            "create table " + TABLE_GAME + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PARTY + " integer, " +
                    COLUMN_BOCKS + " integer, " +
                    COLUMN_DOUBLE_BOCKS + " integer, " +
                    COLUMN_GIVER_INDEX  + " integer, " +
                    "FOREIGN KEY (" + COLUMN_PARTY + ") REFERENCES " + TABLE_PARTY + "(" + COLUMN_ID + "));";


    public GameDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POINTS);
        db.execSQL(SQL_CREATE_PLAYER);
        db.execSQL(SQL_CREATE_SETTINGS);
        db.execSQL(SQL_CREATE_GAME);
        db.execSQL(SQL_CREATE_PARTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_POINTS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PLAYERS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SETTINGS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_GAME );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PARTY);
        onCreate(db);
    }

}
