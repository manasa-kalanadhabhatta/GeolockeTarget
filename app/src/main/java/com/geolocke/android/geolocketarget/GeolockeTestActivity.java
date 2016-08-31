package com.geolocke.android.geolocketarget;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.geolocke.android.geolocketarget.beans.GeolockeCredentials;
import com.geolocke.android.geolocketarget.beans.GeolockeIBeacon;
import com.geolocke.android.geolocketarget.exceptions.InvalidCredentialsException;
import com.geolocke.android.geolocketarget.interfaces.GeolockeConnectionListener;
import com.geolocke.android.geolocketarget.interfaces.GeolockeIBeaconListener;

public class GeolockeTestActivity  extends Activity implements GeolockeConnectionListener,GeolockeIBeaconListener{

    private static final String TAG = GeolockeTestActivity.class.getSimpleName();
    private Geolocke mGeolocke;
    private static GeolockeCredentials mGeolockeCredentials;
    private boolean mGeolockConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocke_test);
        mGeolockeCredentials = new GeolockeCredentials.Builder(this).
                setApplicationKey("spencers").
                setDeveloperKey("hi").
                addGeolockeFeature("j").
                build();
        try {
            Geolocke.connectGeolocke(this,mGeolockeCredentials,this);
        } catch (InvalidCredentialsException e) {
            Log.d(TAG,"Invalid Credentials");
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();/*
        if (mGeolockConnected){
            if(mGeolockeInstance.cancelGeolockeIBeaconUpdates())
                Log.d(TAG, "Cancelling IBeacon Updates.");
            Geolocke.disconnectGeolocke(mGeolockeInstance);
            Log.d(TAG, "Attempting Geolocke Disonnection. Wait for Callback!");
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGeolockConnected){
            try {
                Geolocke.connectGeolocke(this,mGeolockeCredentials,this);
                Log.d(TAG, "Attempting Geolocke Connection. Check for Credentials");

            } catch (InvalidCredentialsException e) {
                Log.d(TAG, "Invalid! msg = " + e.getMessage());
            }
        }
    }

    @Override
    public void onGeolockeConnected(Geolocke pGeolocke) {
        this.mGeolocke = pGeolocke;
        this.mGeolockConnected = true;
        Log.d(TAG, "Geolocke Connected. Requesting for IBeacon Updates");
        if(this.mGeolocke.requestGeolockeIBeaconUpdates(this)){
            Log.d(TAG, "Successful in Registering IBeacon Listener");
        }
    }

    @Override
    public void onGeolockeDisconnected() {
        this.mGeolocke = null;
        this.mGeolockConnected = false;
        Log.d(TAG,"Geolocke Disconnected. Stopping Geolocke Service!");
    }

    @Override
    public void onGeolockeIBeaconFound(GeolockeIBeacon pGeolockeIBeacon) {
        Toast.makeText(this, "IBEACON = "+pGeolockeIBeacon.getMacAddress(), Toast.LENGTH_SHORT).show();
    }
}
