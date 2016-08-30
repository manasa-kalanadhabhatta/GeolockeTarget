package com.geolocke.android.geolocketarget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentName;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.geolocke.android.geolocketarget.authenticator.AccountGeneral;
import com.geolocke.android.geolocketarget.syncadapter.IBeaconsSyncAdapter;


public class Geolocke {

    private Context mContext = null;
    private static boolean mGeolockeConnected=false;
    private static GeolockeConnectionListener mGeolockeConnectionListener;


    public Geolocke() throws NoSuchMethodException {
        throw new NoSuchMethodException("CANNOT SUPPORT");
    }

    public static void disconnectGeolocke(GeolockeInstance pGeolockeInstance){
        if(mGeolockeConnected) {
            pGeolockeInstance.stopServices();
            mGeolockeConnected = false;
            mGeolockeConnectionListener.onGeolockeDisconnected();
        }
    }

    public static final void connectGeolocke(Context pContext,GeolockeCredentials pGeolockeCredentials,GeolockeConnectionListener pGeolockeConnectionListener) throws InvalidCredentialsException
    {
        if(!mGeolockeConnected) {
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
            GeolockeInstance geolockeInstance = new GeolockeInstance(pContext);
            mGeolockeConnected = true;
            mGeolockeConnectionListener.onGeolockeConnected(geolockeInstance);
        }
    }









}
