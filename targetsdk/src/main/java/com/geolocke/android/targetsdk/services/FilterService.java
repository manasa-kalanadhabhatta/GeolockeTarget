package com.geolocke.android.targetsdk.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.geolocke.android.targetsdk.beans.BleBeaconScan;
import com.geolocke.android.targetsdk.beans.IBeacon;
import com.geolocke.android.targetsdk.beans.IBeaconScan;
import com.geolocke.android.targetsdk.contentprovider.IBeaconsContract;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Manasa on 08-07-2016.
 */
public class FilterService extends Service {
    static BleBeaconScan sReceivedBleBeaconScan;
    static ArrayList<IBeacon> sIBeaconList;
    static ArrayList<Integer> sRssiList;
    ArrayList<IBeacon> mScannedBeaconList, mCorrectedBeaconList;
    ArrayList<Integer> mScannedRssiList, mCorrectedRssiList;
    ListReceiver mListReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("BLE_SCAN_BROADCAST");
        mListReceiver = new ListReceiver();
        registerReceiver(mListReceiver,filter);

        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                BleScanService.sClear = 1;

                // TODO: 31-07-2016 decide where, and how, to store timestamp
                Long timestamp = System.currentTimeMillis()/1000;

                mScannedBeaconList = new ArrayList<IBeacon>();
                mCorrectedBeaconList = new ArrayList<IBeacon>();

                mScannedRssiList = new ArrayList<Integer>();
                mCorrectedRssiList = new ArrayList<Integer>();

                if(sIBeaconList != null){
                    for(int i = 0; i<sIBeaconList.size(); i++){
                        mScannedBeaconList.add(sIBeaconList.get(i));
                        mScannedRssiList.add(sRssiList.get(i));
                    }

                    String[] distinctMacAddresses = new String[mScannedBeaconList.size()];
                    int count, index = 0;
                    Double avg, error, dev, devsquare, sum=0.0;

                    for(int i=0; i<mScannedBeaconList.size(); i++){

                        IBeacon iBeacon = mScannedBeaconList.get(i);
                        int rssi = mScannedRssiList.get(i);

                        //get a mac
                        String MacAddress = iBeacon.getMacAddress();

                        //if found for first time
                        if(!Arrays.asList(distinctMacAddresses).contains(MacAddress)){
                            //put it in unique
                            distinctMacAddresses[index] = MacAddress;
                            index++;
                            sum=0.0;
                            count = 0;

                            //go through the remaining array, find avg
                            for(int j=i; j<mScannedBeaconList.size(); j++){
                                if(mScannedBeaconList.get(j).getMacAddress().equals(MacAddress)){
                                    sum+= mScannedRssiList.get(j);
                                    count++;
                                }
                            }
                            avg = sum/count;

                            // TODO: 31-07-2016  use the standard error somewhere if need be
                            //find error
                            sum=0.0;
                            for (int k=i; k<mScannedBeaconList.size(); k++){
                                if(mScannedBeaconList.get(k).getMacAddress().equals(MacAddress)){
                                    dev = avg-mScannedRssiList.get(k);
                                    devsquare = Math.pow(dev,2);
                                    sum+=devsquare;
                                }
                            }
                            error = Math.pow((sum/count),0.5)/Math.pow(count,0.5);

                            /* ADDED BY RAHUL - ADD CURRENT IBEACON TO TABLE  */

                            /*
                             * This defines a one-element String array to contain the selection argument.
                            */

                            String selectionClause = IBeaconsContract.IBEACON_MAC_ADDRESS + "= ?";
                            String[] selectionArgs ={""};
                            String sortOrder = "";
                            String[] projection = {IBeaconsContract.IBEACON_ID,IBeaconsContract.IBEACON_NAME,IBeaconsContract.IBEACON_MAC_ADDRESS,IBeaconsContract.IBEACON_UUID,IBeaconsContract.IBEACON_MAJOR,IBeaconsContract.IBEACON_MINOR,IBeaconsContract.IBEACON_TX_POWER};
                            Cursor cursor;


                            String macAddress = iBeacon.getMacAddress();
                            // Remember to insert code here to check for invalid or malicious input.

                            // If the word is the empty string, gets everything
                            if (TextUtils.isEmpty(macAddress)) {
                                // Setting the selection clause to null will return all words
                                selectionClause = null;
                                selectionArgs[0] = "";

                            } else {
                                // Constructs a selection clause that matches the word that the user entered.
                                selectionClause = IBeaconsContract.IBEACON_MAC_ADDRESS + " = ?";

                                // Moves the user's input string to the selection arguments.
                                selectionArgs[0] = macAddress;

                            }

                            // Does a query against the table and returns a Cursor object
                            cursor = getContentResolver().query(
                                    IBeaconsContract.CONTENT_URI,  // The content URI of the words table
                                    projection,                       // The columns to return for each row
                                    selectionClause,                   // Either null, or the word the user entered
                                    selectionArgs,                    // Either empty, or the string the user entered
                                    sortOrder);

                                                        // The sort order for the returned rows
                            Log.d("BOMB","C = "+cursor.getCount());

                            // Some providers return null if an error occurs, others throw an exception
                            if (cursor==null) {
                            /*
                             * Insert code here to handle the error. Be sure not to use the cursor! You may want to
                             * call android.util.Log.e() to log this error.
                             *
                             */
                                Log.d("CONTENTPROVIDER","ERROR");
                            // If the Cursor is empty, the provider found no matches
                            } else if (cursor.getCount() < 1) {
                                /*
                                 * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
                                 * an error. You may want to offer the user the option to insert a new row, or re-type the
                                 * search term.
                                 */
                                Log.d("CONTENTPROVIDER","NOPE");
                                Uri u = getContentResolver().insert(IBeaconsContract.CONTENT_URI, iBeacon.getContentValues());
                                Log.d("CONTENTPROVIDER","THE ID OF NEW BEACON IS"+u.toString());

                            }
                            else{

                                while (cursor.moveToNext()) {
                                    IBeacon b = IBeacon.fromCursor(cursor);
                                    Log.d("CONTENTPROVIDER", b.toString());
                                }
                                cursor.close();

                            }



                            /*- END */

                            mCorrectedBeaconList.add(iBeacon);
                            mCorrectedRssiList.add((int)Math.ceil(avg));
                        }
                    }

                    IBeaconScan iBeaconScan = new IBeaconScan(mCorrectedBeaconList,mCorrectedRssiList);

                    Intent intent = new Intent();
                    intent.putExtra("IBEACON_SCAN",iBeaconScan);
                    intent.setAction("IBEACON_SCAN_BROADCAST");
                    sendBroadcast(intent);
                }

                handler.postDelayed(this, 3000);
            }
        };

        handler.post(run);

        return START_STICKY;
    }

    public class ListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context pContext, Intent pIntent) {

            sReceivedBleBeaconScan = pIntent.getExtras().getParcelable("BLE_SCAN");
            sIBeaconList = sReceivedBleBeaconScan.getIBeaconList();
            sRssiList = sReceivedBleBeaconScan.getRssiList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mListReceiver);
        stopSelf();
    }
}
