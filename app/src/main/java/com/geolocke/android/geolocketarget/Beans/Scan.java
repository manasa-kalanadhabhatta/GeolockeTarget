package com.geolocke.android.geolocketarget.Beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ACER on 7/30/2016.
 */
public class Scan implements Parcelable {
    private double mLatitude;
    private double mLongitude;
    private ArrayList<GeolockeIBeacon> mGeolockeIBeacons;
    private ArrayList<Integer> mRssis;

    protected Scan(Parcel pParcel) {
        mLatitude = pParcel.readDouble();
        mLongitude = pParcel.readDouble();
        mGeolockeIBeacons = pParcel.readArrayList(GeolockeIBeacon.class.getClassLoader());
        mRssis = pParcel.readArrayList(int.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "Scan{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mGeolockeIBeacons=" + mGeolockeIBeacons +
                ", mRssis=" + mRssis +
                '}';
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public ArrayList<GeolockeIBeacon> getGeolockeIBeacons() {
        return mGeolockeIBeacons;
    }

    public ArrayList<Integer> getRssis() {
        return mRssis;
    }

    public Scan(double pLatitude, double pLongitude, ArrayList<GeolockeIBeacon> pGeolockeIBeacons, ArrayList<Integer> pRssis) {

        mLatitude = pLatitude;
        mLongitude = pLongitude;
        mGeolockeIBeacons = pGeolockeIBeacons;
        mRssis = pRssis;
    }

    public static final Creator<Scan> CREATOR = new Creator<Scan>() {
        @Override
        public Scan createFromParcel(Parcel pParcel) {
            return new Scan(pParcel);
        }

        @Override
        public Scan[] newArray(int pSize) {
            return new Scan[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeDouble(mLatitude);
        pParcel.writeDouble(mLongitude);
        pParcel.writeList(mGeolockeIBeacons);
        pParcel.writeList(mRssis);
    }
}
