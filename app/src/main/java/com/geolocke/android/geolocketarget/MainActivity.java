package com.geolocke.android.geolocketarget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.geolocke.android.geolocketarget.beans.GeolockeIBeacon;
import com.geolocke.android.geolocketarget.interfaces.GeolockeIBeaconListener;
import com.geolocke.android.geolocketarget.services.ParseService;
import com.geolocke.android.geolocketarget.services.PositioningService;
import com.geolocke.android.geolocketarget.services.BleScanService;
import com.geolocke.android.geolocketarget.services.FilterService;

public class MainActivity extends AppCompatActivity implements GeolockeIBeaconListener{

    @Override
    public void onGeolockeIBeaconFound(GeolockeIBeacon pGeolockeIBeacon) {
        Toast.makeText(getBaseContext(),"BEACON",Toast.LENGTH_SHORT).show();
    }
}
