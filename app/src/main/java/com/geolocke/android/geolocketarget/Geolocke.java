package com.geolocke.android.geolocketarget;

import android.content.ComponentName;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.IBinder;



public class Geolocke {

    private static GeolockeService mGeolockeService;
    private static GeolockeCredentials mGeolockeCredentials;
    private static boolean mGeolockeServiceBound = false;
    private static GeolockeConnectionListener mGeolockeConnectionListener;
    private static Context mGeolockeContext;
    private Context mContext = null;


    public static final void initializeGeolocke(GeolockeCredentials pGeolockeCredentials){
        mGeolockeCredentials = pGeolockeCredentials;
    }

    public Geolocke() throws NoSuchMethodException {
        throw new NoSuchMethodException("CANNOT SUPPORT");
    }

    private Geolocke(Context pContext){
        mContext = pContext;
    }

    public void startServices(Context pContext){
        if(pContext == mGeolockeContext && mGeolockeServiceBound){
            pContext.startService(new Intent(pContext,GeolockeService.class));
        }
    }

    public Context getContext(){
        return this.mContext;
    }


    public void disconnectGeolocke(){
        if(mGeolockeServiceBound) {
            mGeolockeContext.unbindService(mGeolockeServiceConnection);
        }
    }

    public static final void connectGeolocke(GeolockeConnectionListener pGeolockeConnectionListener) throws InvalidCredentialsException
    {

        if(mGeolockeCredentials.getApplicationKey()!="spencers"){
            throw new InvalidCredentialsException("Wrong Credentials",mGeolockeCredentials.getApplicationKey(),mGeolockeCredentials.getDeveloperKey());
        }
        mGeolockeContext = mGeolockeCredentials.getContext();
        mGeolockeConnectionListener = pGeolockeConnectionListener;

        if(!mGeolockeServiceBound) {
            Intent intent = new Intent(mGeolockeContext, GeolockeService.class);
            mGeolockeContext.bindService(intent, mGeolockeServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }


    private static ServiceConnection mGeolockeServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGeolockeServiceBound = false;
            mGeolockeConnectionListener.onGeolockeDisconnected();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GeolockeService.GeolockeServiceBinder binder = (GeolockeService.GeolockeServiceBinder) service;
            mGeolockeService = binder.getService();
            mGeolockeServiceBound = true;

            mGeolockeConnectionListener.onGeolockeConnected(new Geolocke(mGeolockeContext));
        }
    };






}
