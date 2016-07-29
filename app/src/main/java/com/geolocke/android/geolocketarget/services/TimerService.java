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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Manasa on 08-07-2016.
 */
public class TimerService extends Service {

    static JSONArray sListArray;
    JSONArray mScanArray, mParseArray;
    ListReceiver mListReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("SCANLIST_BROADCAST");
        mListReceiver = new ListReceiver();
        registerReceiver(mListReceiver,filter);

        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ScanService.sClear = 1;
                Long timestamp = System.currentTimeMillis()/1000;
                try {
                    mScanArray = new JSONArray();
                    mParseArray = new JSONArray();
                    if(sListArray != null){
                        for(int i = 0; i< sListArray.length(); i++){
                            mScanArray.put(sListArray.getJSONObject(i));
                        }

                        String[] distinctMacAddresses = new String[mScanArray.length()];
                        int count, index = 0;
                        Double avg, error, dev, devsquare, sum=0.0;

                        for(int i=0; i<mScanArray.length(); i++){
                            JSONObject object = mScanArray.getJSONObject(i);

                            //get a mac
                            String MacAddress = object.get("MAC_ADDRESS").toString();

                            //if found for first time
                            if(!Arrays.asList(distinctMacAddresses).contains(MacAddress)){
                                //put it in unique
                                distinctMacAddresses[index] = MacAddress;
                                index++;
                                sum=0.0;
                                count = 0;

                                //go through the remaining array, find avg
                                for(int j=i; j<mScanArray.length(); j++){
                                    if(mScanArray.getJSONObject(j).get("MAC_ADDRESS").toString().equals(MacAddress)){
                                        sum+= mScanArray.getJSONObject(j).getDouble("RSSI");
                                        count++;
                                    }
                                }
                                avg = sum/count;

                                //find error
                                sum=0.0;
                                for (int k=i; k<mScanArray.length(); k++){
                                    if(mScanArray.getJSONObject(k).get("MAC_ADDRESS").toString().equals(MacAddress)){
                                        dev = avg-mScanArray.getJSONObject(k).getDouble("RSSI");
                                        devsquare = Math.pow(dev,2);
                                        sum+=devsquare;
                                    }
                                }
                                error = Math.pow((sum/count),0.5)/Math.pow(count,0.5);

                                //create new object
                                JSONObject newObject = new JSONObject();
                                newObject = object;
                                newObject.remove("RSSI");
                                newObject.put("RSSI", avg);
                                newObject.put("ERROR", error);

                                //put new object in new array
                                mParseArray.put(newObject);
                            }
                        }

                        JSONObject scanObject = new JSONObject();
                        scanObject.put("SCAN_ARRAY", mParseArray);
                        scanObject.put("SCAN_TIME", timestamp);

                        Intent intent = new Intent();
                        intent.putExtra("SCAN_STRUCTURE",scanObject.toString());
                        intent.setAction("SCAN_BROADCAST");
                        sendBroadcast(intent);
                        Log.i("got macs",distinctMacAddresses.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
            try {
                sListArray = new JSONArray(pIntent.getStringExtra("SCAN_LIST"));
                Log.i("TimerService got:", sListArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mListReceiver);
        stopSelf();
    }
}
