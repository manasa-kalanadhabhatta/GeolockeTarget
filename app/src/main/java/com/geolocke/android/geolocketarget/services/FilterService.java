package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geolocke.android.geolocketarget.beans.BleBeaconScan;
import com.geolocke.android.geolocketarget.beans.IBeacon;
import com.geolocke.android.geolocketarget.beans.IBeaconScan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
