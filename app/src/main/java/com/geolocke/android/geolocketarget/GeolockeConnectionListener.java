package com.geolocke.android.geolocketarget;

/**
 * Created by Rahul on 15/08/16.
 */
public interface GeolockeConnectionListener {
    public void onGeolockeConnected(Geolocke pGeolocke);
    public void onGeolockeDisconnected();
}
