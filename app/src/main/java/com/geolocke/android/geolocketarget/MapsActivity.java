package com.geolocke.android.geolocketarget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.geolocke.android.geolocketarget.services.ParseService;
import com.geolocke.android.geolocketarget.services.PositioningService;
import com.geolocke.android.geolocketarget.services.ScanService;
import com.geolocke.android.geolocketarget.services.TimerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double mLatitude, mLongitude;
    public PositionReceiver mPositionReceiver;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("POSITION");
        mPositionReceiver = new PositionReceiver();
        registerReceiver(mPositionReceiver, intentFilter);
    }

    public class PositionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context pContext, Intent pIntent) {
            mLatitude = pIntent.getDoubleExtra("LATITUDE",0.0);
            mLongitude = pIntent.getDoubleExtra("LONGITUDE",0.0);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void button(View pView){
        Double latitude = mLatitude;
        Double longitude = mLongitude;
        LatLng buttonLatLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(buttonLatLng).title("Button wala marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(button));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buttonLatLng, 15));
    }
    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        mMap = pGoogleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPositionReceiver);
    }
}
