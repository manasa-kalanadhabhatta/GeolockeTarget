package com.geolocke.android.geolocketarget;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GeolockeTestActivity  extends Activity implements GeolockeConnectionListener{

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
        Geolocke.initializeGeolocke(mGeolockeCredentials);
        Log.d(TAG, "Initializing Geolocke Credentials. Will attempt Connect Soon.");

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
        super.onPause();
        if (mGeolockConnected){
            mGeolocke.disconnectGeolocke();
            Log.d(TAG, "Attempting Geolocke Disonnection. Wait for Callback!");
        }
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
                Geolocke.connectGeolocke(this);
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
        this.mGeolocke.startServices(mGeolocke.getContext());
        Log.d(TAG,"Geolocke Connected. Starting Geolocke Service!");
    }

    @Override
    public void onGeolockeDisconnected() {
        this.mGeolocke = null;
        this.mGeolockConnected = false;
        Log.d(TAG,"Geolocke Disconnected. Stopping Geolocke Service!");
    }
}
