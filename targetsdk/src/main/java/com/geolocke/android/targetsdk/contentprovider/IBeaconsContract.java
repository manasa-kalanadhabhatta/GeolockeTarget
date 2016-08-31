package com.geolocke.android.targetsdk.contentprovider;

import android.net.Uri;


public class IBeaconsContract {

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.geolocke.ibeacon";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.geolocke.ibeacon";

    //public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.udinic.tvshow";
    //public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.udinic.tvshow";
    //public static final String CONTENT_ITEM_TYPE_REMOTE = "vnd.android.cursor.item/vnd.udinic.tvshow_remote";
    //public static final String CONTENT_TYPE_DIR_REMOTE = "vnd.android.cursor.dir/vnd.udinic.tvshow_remote";


    //public static final String AUTHORITY = "com.udinic.tvshows.provider";
    public static final String AUTHORITY = "com.geolocke.android.geolocketarget.ibeacons.provider";



    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/ibeacons");
    // content://<authority>/<path to type>
    //public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/tvshows");
    //public static final Uri CONTENT_URI_REMOTE = Uri.parse("content://"+AUTHORITY+"/tvshows_remote");

    //public static final String TV_SHOW_ID = "_id";
    //public static final String TV_SHOW_NAME = "name";
    //public static final String TV_SHOW_YEAR = "year";

    public static final String IBEACON_ID =  "_id";
    public static final String IBEACON_MAC_ADDRESS =  "mac_address";
    public static final String IBEACON_UUID = "uuid";
    public static final String IBEACON_MAJOR= "major";
    public static final String IBEACON_MINOR = "minor";
    public static final String IBEACON_NAME = "name";
    public static final String IBEACON_TX_POWER = "tx_power";


}
