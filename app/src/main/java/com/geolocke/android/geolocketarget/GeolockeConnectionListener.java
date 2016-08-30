package com.geolocke.android.geolocketarget;


public interface GeolockeConnectionListener {
    public void onGeolockeConnected(GeolockeInstance pGeolockeInstance);
    public void onGeolockeDisconnected();
}
