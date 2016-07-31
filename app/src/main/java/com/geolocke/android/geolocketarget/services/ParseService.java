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

import com.geolocke.android.geolocketarget.beans.GeolockeIBeacon;
import com.geolocke.android.geolocketarget.beans.GeolockeIBeaconScan;
import com.geolocke.android.geolocketarget.beans.IBeacon;
import com.geolocke.android.geolocketarget.beans.IBeaconScan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Manasa on 06-07-2016.
 */
public class ParseService extends Service {
    ScanReceiver mScanReceiver;
    GeolockeIBeaconScan mGeolockeIBeaconScan;
    ArrayList<GeolockeIBeacon> mGeolockeIBeaconList;
    ArrayList<Integer> mRssiList;

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("IBEACON_SCAN_BROADCAST");
        mScanReceiver = new ScanReceiver();
        registerReceiver(mScanReceiver, intentFilter);

        return START_STICKY;
    }

    public class ScanReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context pContext, Intent pIntent) {

            IBeaconScan iBeaconScan = pIntent.getExtras().getParcelable("IBEACON_SCAN");
            ArrayList<IBeacon> iBeaconList = new ArrayList<IBeacon>();
            iBeaconList = iBeaconScan.getIBeaconList();
            ArrayList<Integer> rssiList = new ArrayList<Integer>();
            rssiList = iBeaconScan.getRssiList();

            mGeolockeIBeaconList = new ArrayList<GeolockeIBeacon>();
            mRssiList = new ArrayList<Integer>();

            for(int i=0; i<iBeaconList.size(); i++){
                IBeacon iBeacon = iBeaconList.get(i);
                int rssi = rssiList.get(i);

                String uuid = iBeacon.getUUID();
                String[] split = uuid.split("-");

                String lat = split[0];
                Long l = Long.parseLong(lat, 16);
                Float f = Float.intBitsToFloat(l.intValue());
                Double latitude = Double.valueOf(f);

                String lng = split[1]+split[2];
                Long j = Long.parseLong(lng, 16);
                Float k = Float.intBitsToFloat(j.intValue());
                Double longitude = Double.valueOf(k);

                // TODO: 31-07-2016 store buildingId and floorId somewhere
                String buildingId = split[3];
                String floorId = split[4].substring(0, 4);

                GeolockeIBeacon geolockeIBeacon = new GeolockeIBeacon(iBeacon.getMacAddress(),iBeacon.getUUID(),iBeacon.getName(),iBeacon.getMajor(),iBeacon.getMinor(),iBeacon.getTxPower(),latitude,longitude);

                mGeolockeIBeaconList.add(geolockeIBeacon);
                mRssiList.add(rssi);
            }

            mGeolockeIBeaconScan = new GeolockeIBeaconScan(mGeolockeIBeaconList,mRssiList);

            Intent intent = new Intent();
            intent.putExtra("GEOLOCKE_IBEACON_SCAN",mGeolockeIBeaconScan);
            intent.setAction("GEOLOCKE_IBEACON_SCAN_BROADCAST");
            sendBroadcast(intent);
            Log.i("broadcast sent", "from parseservice");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScanReceiver);
        stopSelf();
    }
}
