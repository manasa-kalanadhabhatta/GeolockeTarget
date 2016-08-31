package com.geolocke.android.geolocketarget.interfaces;


import com.geolocke.android.geolocketarget.Geolocke;

public interface GeolockeConnectionListener {
    public void onGeolockeConnected(Geolocke pGeolocke);
    public void onGeolockeDisconnected();
}
