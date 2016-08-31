package com.geolocke.android.geolocketarget;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.geolocke.android.geolocketarget.authenticator.AccountGeneral;
import com.geolocke.android.geolocketarget.beans.GeolockeCredentials;
import com.geolocke.android.geolocketarget.exceptions.InvalidCredentialsException;
import com.geolocke.android.geolocketarget.interfaces.GeolockeConnectionListener;
import com.geolocke.android.geolocketarget.interfaces.GeolockeIBeaconListener;
import com.geolocke.android.geolocketarget.services.BleScanService;
import com.geolocke.android.geolocketarget.services.FilterService;
import com.geolocke.android.geolocketarget.services.ParseService;
import com.geolocke.android.geolocketarget.services.PositioningService;
import com.geolocke.android.geolocketarget.syncadapter.IBeaconsSyncAdapter;

public class Geolocke {
    private String TAG = Geolocke.class.getSimpleName();
    private Context mContext;
    private static boolean mGeolockeConnected=false;
    private static GeolockeConnectionListener mGeolockeConnectionListener;

    protected Geolocke(Context pContext){
        mContext = pContext;
    }

    public static void disconnectGeolocke(Geolocke pGeolocke){
        if(mGeolockeConnected) {
            pGeolocke.stopServices();
            mGeolockeConnected = false;
            mGeolockeConnectionListener.onGeolockeDisconnected();
        }
    }

    public static final void connectGeolocke(Context pContext,GeolockeCredentials pGeolockeCredentials,GeolockeConnectionListener pGeolockeConnectionListener) throws InvalidCredentialsException {
        if (!mGeolockeConnected) {
            if (pGeolockeCredentials.getApplicationKey() != "spencers") {
                throw new InvalidCredentialsException("Wrong Credentials", pGeolockeCredentials.getApplicationKey(), pGeolockeCredentials.getDeveloperKey());
            }
            mGeolockeConnectionListener = pGeolockeConnectionListener;
            try {
                Account account = IBeaconsSyncAdapter.getSyncAccount(pContext);
                AccountManager accountManager = (AccountManager) pContext.getSystemService(pContext.ACCOUNT_SERVICE);
                Log.d("RAHUL", "AuthToken = " + accountManager.getUserData(account, AccountGeneral.USERNAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Geolocke geolocke = new Geolocke(pContext);
            mGeolockeConnected = true;
            mGeolockeConnectionListener.onGeolockeConnected(geolocke);
        }
    }

    private GeolockeIBeaconListener mGeolockeIBeaconListener;

    private BleScanService mBleScanService;
    private boolean mBleScanServiceBound = false;

    private ParseService mParseService;
    private boolean mParseServiceBound = false;

    private ServiceConnection mBleScanServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleScanServiceBound = false;
            mContext.stopService(new Intent(mContext, BleScanService.class));

            Log.d(TAG, "BLEScan Service Disconnected!");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleScanService.BleScanServiceBinder binder = (BleScanService.BleScanServiceBinder) service;
            mBleScanService = binder.getService();
            mContext.startService(new Intent(mContext, mBleScanService.getClass()));
            mBleScanServiceBound = true;
            Log.d(TAG, "BLEScan Service Connected!");
        }
    };


    private ServiceConnection mParseServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mParseServiceBound = false;
            mContext.stopService(new Intent(mContext, ParseService.class));

            Log.d(TAG, "Parse Service Disconnected!");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ParseService.ParseServiceBinder binder = (ParseService.ParseServiceBinder) service;
            mParseService = binder.getService();
            mContext.startService(new Intent(mContext, mParseService.getClass()));
            mParseService.registerGeolockeIBeaconListener(mGeolockeIBeaconListener);
            mParseServiceBound = true;
            Log.d(TAG, "Parse Service Connected!");

        }
    };

    public void initializeServices(){
        final BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
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
        Intent intent = new Intent(mContext, BleScanService.class);
        mContext.bindService(intent, mBleScanServiceConnection, Context.BIND_AUTO_CREATE);
        intent = new Intent(mContext, ParseService.class);
        mContext.bindService(intent, mParseServiceConnection, Context.BIND_AUTO_CREATE);

        mContext.startService(new Intent(mContext, FilterService.class));
        mContext.startService(new Intent(mContext, PositioningService.class));
    }

    public boolean requestGeolockIBeaconUpdates(GeolockeIBeaconListener pGeolockeIBeaconListener){
        this.mGeolockeIBeaconListener = pGeolockeIBeaconListener;
        this.initializeServices();
        return true;
    }

    public boolean cancelGeolockIBeaconUpdates(){
        if(mParseServiceBound){
            mParseService.unregisterGeolockeIBeaconListener();
            return true;
        }
        else{
            return false;
        }
    }


    public Geolocke() throws NoSuchMethodException {
        throw new NoSuchMethodException("CANNOT SUPPORT");
    }



    public void stopServices(){
        if (mBleScanServiceBound) {
            mContext.unbindService(mBleScanServiceConnection);
        }
        if (mParseServiceBound) {
            mContext.unbindService(mParseServiceConnection);
        }
        mContext.stopService(new Intent(mContext, FilterService.class));
        mContext.stopService(new Intent(mContext, PositioningService.class));


        final BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
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
    }






}
