package com.geolocke.android.geolocketarget;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.geolocke.android.geolocketarget.beans.GeolockeIBeacon;
import com.geolocke.android.geolocketarget.interfaces.GeolockeIBeaconListener;
import com.geolocke.android.geolocketarget.services.BleScanService;
import com.geolocke.android.geolocketarget.services.FilterService;
import com.geolocke.android.geolocketarget.services.ParseService;
import com.geolocke.android.geolocketarget.services.PositioningService;


public class GeolockeService extends Service implements GeolockeIBeaconListener {

    private final String TAG = GeolockeService.class.getSimpleName();
    private IBinder mBinder = new GeolockeServiceBinder();

    BleScanService mBleScanService;
    boolean mBleScanServiceBound = false;

    ParseService mParseService;
    boolean mParseServiceBound = false;

    private ServiceConnection mBleScanServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleScanServiceBound = false;
            Log.d(TAG, "BLEScan Service Disconnected!");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleScanService.BleScanServiceBinder binder = (BleScanService.BleScanServiceBinder) service;
            mBleScanService = binder.getService();
            mBleScanServiceBound = true;
            Log.d(TAG, "BLEScan Service Connected!");
        }
    };

    private ServiceConnection mParseServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mParseServiceBound = false;
            Log.d(TAG, "Parse Service Disconnected!");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ParseService.ParseServiceBinder binder = (ParseService.ParseServiceBinder) service;
            mParseService = binder.getService();
            mParseServiceBound = true;
            mParseService.registerGeolockeIBeaconListener(GeolockeService.this);
            Log.d(TAG, "Parse Service Connected!");

        }
    };

    @Override
    public void onGeolockeIBeaconFound(GeolockeIBeacon pGeolockeIBeacon) {
        Toast.makeText(getBaseContext(), "BEACON", Toast.LENGTH_SHORT).show();

    }


    public class GeolockeServiceBinder extends Binder {
        public GeolockeService getService() {
            return GeolockeService.this;
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "in onUnbind");
        if (mBleScanServiceBound) {
            unbindService(mBleScanServiceConnection);
        }
        if (mParseServiceBound) {
            unbindService(mParseServiceConnection);
        }
        final BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /**
         * Checks if Bluetooth is enabled on device
         * Use this within and Activity
         */
        if (bluetoothAdapter !=null && bluetoothAdapter.isEnabled()) {
            /*Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);*/
            bluetoothAdapter.disable();
        }

        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        Log.v(TAG, "in onBind");
        Intent intent = new Intent(this, BleScanService.class);
        bindService(intent, mBleScanServiceConnection, Context.BIND_AUTO_CREATE);
        intent = new Intent(this, ParseService.class);
        bindService(intent, mParseServiceConnection, Context.BIND_AUTO_CREATE);
        final BluetoothAdapter bluetoothAdapter;
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
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {


        if(mBleScanServiceBound && mParseServiceBound){
            startService(new Intent(getBaseContext(), BleScanService.class));
            startService(new Intent(getBaseContext(), ParseService.class));
            startService(new Intent(getBaseContext(), FilterService.class));
            startService(new Intent(getBaseContext(), PositioningService.class));
        }


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), BleScanService.class));
        stopService(new Intent(getBaseContext(), ParseService.class));
        stopService(new Intent(getBaseContext(), FilterService.class));
        stopService(new Intent(getBaseContext(), PositioningService.class));

        Log.v(TAG, "in onDestroy");

        stopSelf();
    }
}
