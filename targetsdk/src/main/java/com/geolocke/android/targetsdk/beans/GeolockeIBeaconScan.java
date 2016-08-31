package com.geolocke.android.targetsdk.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class GeolockeIBeaconScan implements Parcelable {
    private ArrayList<GeolockeIBeacon> mGeolockeIBeaconList;
    private ArrayList<Integer> mRssiList;

    protected GeolockeIBeaconScan(Parcel pParcel) {
        mGeolockeIBeaconList = pParcel.readArrayList(GeolockeIBeacon.class.getClassLoader());
        mRssiList = pParcel.readArrayList(int.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "GeolockeIBeaconScan{" +
                "mGeolockeIBeaconList=" + mGeolockeIBeaconList +
                ", mRssiList=" + mRssiList +
                '}';
    }

    public ArrayList<GeolockeIBeacon> getGeolockeIBeaconList() {
        return mGeolockeIBeaconList;
    }

    public ArrayList<Integer> getRssiList() {
        return mRssiList;
    }

    public GeolockeIBeaconScan(ArrayList<GeolockeIBeacon> pGeolockeIBeaconList, ArrayList<Integer> pRssiList) {

        mGeolockeIBeaconList = pGeolockeIBeaconList;
        mRssiList = pRssiList;
    }

    public static final Creator<GeolockeIBeaconScan> CREATOR = new Creator<GeolockeIBeaconScan>() {
        @Override
        public GeolockeIBeaconScan createFromParcel(Parcel pParcel) {
            return new GeolockeIBeaconScan(pParcel);
        }

        @Override
        public GeolockeIBeaconScan[] newArray(int pSize) {
            return new GeolockeIBeaconScan[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeList(mGeolockeIBeaconList);
        pParcel.writeList(mRssiList);
    }
}
