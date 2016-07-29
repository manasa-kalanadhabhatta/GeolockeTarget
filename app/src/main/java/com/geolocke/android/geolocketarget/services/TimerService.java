package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Manasa on 08-07-2016.
 */
public class TimerService extends Service {

    static JSONArray mListArray;
    JSONArray mScanArray, mParseArray;
    ListReceiver mListReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("SCANLIST_BROADCAST");
        mListReceiver = new ListReceiver();
        registerReceiver(mListReceiver,filter);

        final Handler mHandler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ScanService.clear = 1;
                Long timestamp = System.currentTimeMillis()/1000;
                try {
                    mScanArray = new JSONArray();
                    mParseArray = new JSONArray();
                    if(mListArray != null){
                        for(int i=0; i<mListArray.length(); i++){
                            mScanArray.put(mListArray.getJSONObject(i));
                        }

                        /*String[] distinctMacAddresses = new String[mScanArray.length()];
                        int index = 0;

                        for(int i=mScanArray.length()-1; i>-1; i--){
                            String MacAddress = mScanArray.getJSONObject(i).get("MAC_ADDRESS").toString();
                            if(!Arrays.asList(distinctMacAddresses).contains(MacAddress)){
                                Log.i("got and putting", MacAddress);
                                distinctMacAddresses[index] = MacAddress;
                                index++;
                                mParseArray.put(mScanArray.getJSONObject(i));
                            }
                        }*/

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

                        Intent i = new Intent();
                        i.putExtra("SCAN_STRUCTURE",scanObject.toString());
                        i.setAction("SCAN_BROADCAST");
                        sendBroadcast(i);
                        Log.i("got macs",distinctMacAddresses.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(this, 3000);
            }
        };

        mHandler.post(run);

        return START_STICKY;
    }

    public class ListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mListArray = new JSONArray(intent.getStringExtra("SCAN_LIST"));
                Log.i("TimerService got:", mListArray.toString());
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
