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

    public final int REQUEST_ENABLE_BT = 1;
    BleScanService mBleScanService;
    boolean mBleScanServiceBound = false;

    ParseService mParseService;
    boolean mParseServiceBound = false;

    private ServiceConnection mBleScanServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleScanServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleScanService.BleScanServiceBinder binder = (BleScanService.BleScanServiceBinder) service;
            mBleScanService = binder.getService();
            mBleScanServiceBound = true;
            startService(new Intent(getBaseContext(), BleScanService.class));
        }
    };

    private ServiceConnection mParseServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mParseServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ParseService.ParseServiceBinder binder = (ParseService.ParseServiceBinder) service;
            mParseService = binder.getService();
            mParseServiceBound = true;
            mParseService.registerGeolockeIBeaconListener(MainActivity.this);
            startService(new Intent(getBaseContext(), ParseService.class));

        }
    };



    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BleScanService.class);
        bindService(intent, mBleScanServiceConnection, Context.BIND_AUTO_CREATE);

        intent = new Intent(this, ParseService.class);
        bindService(intent, mParseServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBleScanServiceBound) {
            unbindService(mBleScanServiceConnection);
        }
        if (mParseServiceBound) {
            unbindService(mParseServiceConnection);
        }
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter bluetoothAdapter;

        int count = 0;

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /**
         * Checks if Bluetooth is enabled on device
         * Use this within and Activity
         */
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            /*Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);*/
            bluetoothAdapter.enable();
        }

        //startService(new Intent(getBaseContext(), BleScanService.class));
        //startService(new Intent(getBaseContext(), ParseService.class));
        startService(new Intent(getBaseContext(), FilterService.class));
        startService(new Intent(getBaseContext(), PositioningService.class));

        /*Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), BleScanService.class));
        stopService(new Intent(getBaseContext(), ParseService.class));
        stopService(new Intent(getBaseContext(), FilterService.class));
        stopService(new Intent(getBaseContext(), PositioningService.class));
    }

    @Override
    public void onGeolockeIBeaconFound(GeolockeIBeacon pGeolockeIBeacon) {
        Toast.makeText(getBaseContext(),"BEACON",Toast.LENGTH_SHORT).show();
    }
}
