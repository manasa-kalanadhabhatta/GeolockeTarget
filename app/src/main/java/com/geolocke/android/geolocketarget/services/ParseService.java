package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Manasa on 06-07-2016.
 */
public class ParseService extends Service {
    ScanReceiver mScanReceiver;
    JSONObject mParseObject, mDeviceObject;
    JSONArray mDeviceListArray, mParsedList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SCAN_BROADCAST");
        mScanReceiver = new ScanReceiver();
        registerReceiver(mScanReceiver, intentFilter);

        return START_STICKY;
    }

    public class ScanReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject scanObject = new JSONObject(intent.getStringExtra("SCAN_STRUCTURE"));
                Log.i("ParseService got:", scanObject.toString());

                mParseObject = new JSONObject();
                mParsedList = new JSONArray();

                mDeviceListArray = scanObject.getJSONArray("SCAN_ARRAY");
                for(int i=0; i<mDeviceListArray.length(); i++){
                    mDeviceObject = mDeviceListArray.getJSONObject(i);

                    Double rssi = mDeviceObject.getDouble("RSSI");
                    Double error = mDeviceObject.getDouble("ERROR");
                    int txpw = mDeviceObject.getInt("TX_POWER"); // TODO: 08-07-2016 have to set actual tx power
                    Double distance = calculateDistance(rssi, txpw);
                    Double accuracy = calculateDistance(error, txpw);
                    mDeviceObject.put("DISTANCE_IN_METRES", distance);
                    mDeviceObject.put("ACCURACY", accuracy); // TODO: 12-07-2016 have to correct accuracy

                    // TODO: 08-07-2016 get latLng from UUID and put in mDeviceObject

                    String uuid = mDeviceObject.getString("UUID");
                    String[] splitresult = uuid.split("-");
                    String Lat = splitresult[0];
                    Long l = Long.parseLong(Lat, 16);
                    Float f = Float.intBitsToFloat(l.intValue());
                    Double latitude = Double.valueOf(f);
                    String Lng = splitresult[1]+splitresult[2];
                    Long j = Long.parseLong(Lng, 16);
                    Float k = Float.intBitsToFloat(j.intValue());
                    Double longitude = Double.valueOf(k);
                    String buildingId = splitresult[3];
                    String floorId = splitresult[4].substring(0, 4);
                    mDeviceObject.put("LATITUDE", latitude);
                    mDeviceObject.put("LONGITUDE", longitude);

                    mParsedList.put(mDeviceObject);
                }
                Log.i("Parsed List", mParsedList.toString());
                mParseObject.put("PARSED_LIST", mParsedList);
                mParseObject.put("SCAN_TIME", scanObject.getLong("SCAN_TIME"));
                Log.i("Parsed Object", mParseObject.toString());

                Intent i = new Intent();
                i.putExtra("PARSED_STRUCTURE",mParseObject.toString());
                i.setAction("PARSED_BROADCAST");
                sendBroadcast(i);
                Log.i("broadcast sent", "from parseservice");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Double calculateDistance(Double rssi, int txpw){
        if(rssi == 0)
            return -1.0;
        Double ratio = rssi*1.0/txpw;
        if(ratio<1.0){
            return Math.pow(ratio, 10);
        }else {
            return ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScanReceiver);
        stopSelf();
    }
}
