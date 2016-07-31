package com.geolocke.android.geolocketarget.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Location implements Parcelable {
    private ArrayList<Position> mPositionList;
    private double mLatitude;
    private double mLongitude;

    @Override
    public String toString() {
        return "Location{" +
                "mPositionList=" + mPositionList +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }

    public ArrayList<Position> getPositionList() {
        return mPositionList;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public Location(ArrayList<Position> pPositionList, double pLatitude, double pLongitude) {
        mPositionList = pPositionList;
        mLatitude = pLatitude;
        mLongitude = pLongitude;
    }

    protected Location(Parcel pParcel) {
        mPositionList = pParcel.readArrayList(Position.class.getClassLoader());
        mLatitude = pParcel.readDouble();
        mLongitude = pParcel.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel pParcel) {
            return new Location(pParcel);
        }

        @Override
        public Location[] newArray(int pSize) {
            return new Location[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeList(mPositionList);
        pParcel.writeDouble(mLatitude);
        pParcel.writeDouble(mLongitude);
    }
}
