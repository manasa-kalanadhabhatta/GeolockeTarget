package com.geolocke.android.targetsdk.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class IBeaconsDbHelper extends SQLiteOpenHelper {

    //private static final String DATABASE_NAME = "udinic.db";\
    private static final String DATABASE_NAME = "geolocke.db";
    private static final int DATABASE_VERSION = 1;

    // DB Table consts
    /*
    public static final String TVSHOWS_TABLE_NAME = "tvshows";
    public static final String TVSHOWS_TABLE_NAME_REMOTE = "tvshows_remote";
    public static final String TVSHOWS_COL_ID = "_id";
    public static final String TVSHOWS_COL_NAME = "name";
    public static final String TVSHOWS_COL_YEAR = "year";*/


    public static final String IBEACONS_TABLE_NAME = "ibeacons";


    public static final String IBEACONS_COL_ID =  "_id";
    public static final String IBEACONS_COL_MAC_ADDRESS =  "mac_address";
    public static final String IBEACONS_COL_UUID = "uuid";
    public static final String IBEACONS_COL_MAJOR= "major";
    public static final String IBEACONS_COL_MINOR = "minor";
    public static final String IBEACONS_COL_NAME = "name";
    public static final String IBEACONS_COL_TX_POWER = "tx_power";




    // Database creation sql statement/*
    /*
    public static final String DATABASE_CREATE = "create table "
            + TVSHOWS_TABLE_NAME + "(" +
            TVSHOWS_COL_ID + " integer   primary key autoincrement, " +
            TVSHOWS_COL_NAME + " text not null, " +
            TVSHOWS_COL_YEAR + " integer " +
            ");";

    public static final String DATABASE_CREATE_REMOTE = "create table "
            + TVSHOWS_TABLE_NAME_REMOTE + "(" +
            TVSHOWS_COL_ID + " integer   primary key autoincrement, " +
            TVSHOWS_COL_NAME + " text not null, " +
            TVSHOWS_COL_YEAR + " integer " +
            ");";
    */

    public static final String DATABASE_CREATE= "create table "
            + IBEACONS_TABLE_NAME  + "(" +
            IBEACONS_COL_ID + " integer   primary key autoincrement, " +
            IBEACONS_COL_MAC_ADDRESS + " text not null, " +
            IBEACONS_COL_UUID + " integer not null," +
            IBEACONS_COL_MAJOR + " integer not null, " +
            IBEACONS_COL_MINOR + " integer not null," +
            IBEACONS_COL_NAME + " integer not null," +
            IBEACONS_COL_TX_POWER + " integer not null" +
            ");";

    public IBeaconsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(IBeaconsDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + IBEACONS_TABLE_NAME);
        onCreate(db);
    }

}
