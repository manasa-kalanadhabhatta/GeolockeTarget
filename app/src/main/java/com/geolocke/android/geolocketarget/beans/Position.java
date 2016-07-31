package com.geolocke.android.geolocketarget.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {
    private double mLatitude;
    private double mLongitude;
    private GeolockeIBeaconScan mGeolockeIBeaconScan;

    public Position(double pLatitude, double pLongitude, GeolockeIBeaconScan pGeolockeIBeaconScan) {
        mLatitude = pLatitude;
        mLongitude = pLongitude;
        mGeolockeIBeaconScan = pGeolockeIBeaconScan;
    }

    public Position(Parcel pParcel) {
        mLatitude = pParcel.readDouble();
        mLongitude = pParcel.readDouble();
        mGeolockeIBeaconScan = pParcel.readParcelable(GeolockeIBeaconScan.class.getClassLoader());
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel pParcel) {
            return new Position(pParcel);
        }

        @Override
        public Position[] newArray(int pSize) {
            return new Position[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Position{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mGeolockeIBeaconScan=" + mGeolockeIBeaconScan +
                '}';
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public GeolockeIBeaconScan getGeolockeIBeaconScan() {
        return mGeolockeIBeaconScan;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeDouble(this.mLatitude);
        pParcel.writeDouble(this.mLongitude);
        pParcel.writeParcelable(mGeolockeIBeaconScan,0);
    }
}
