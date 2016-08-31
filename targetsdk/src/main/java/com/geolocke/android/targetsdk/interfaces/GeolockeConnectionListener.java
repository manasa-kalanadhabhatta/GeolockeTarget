package com.geolocke.android.targetsdk.interfaces;


import com.geolocke.android.targetsdk.Geolocke;

public interface GeolockeConnectionListener {
    public void onGeolockeConnected(Geolocke pGeolocke);
    public void onGeolockeDisconnected();
}
