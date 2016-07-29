package com.geolocke.android.geolocketarget.Beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ACER on 7/30/2016.
 */
public class Position implements Parcelable {
    private double mLatitude;
    private double mLongitude;

    public Position(double pLatitude, double pLongitude) {
        mLatitude = pLatitude;
        mLongitude = pLongitude;
    }

    public Position(Parcel pParcel) {
        mLatitude = pParcel.readDouble();
        mLongitude = pParcel.readDouble();
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
                '}';
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeDouble(this.mLatitude);
        pParcel.writeDouble(this.mLongitude);
    }
}
