package com.geolocke.android.geolocketarget.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Location implements Parcelable {
    private ArrayList<Position> mPositionList;

    @Override
    public String toString() {
        return "Location{" +
                "mPositionList=" + mPositionList +
                '}';
    }

    public ArrayList<Position> getPositionList() {
        return mPositionList;
    }

    public Location(ArrayList<Position> pPositionList) {
        mPositionList = pPositionList;
    }

    protected Location(Parcel pParcel) {
        mPositionList = pParcel.readArrayList(Position.class.getClassLoader());
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
    }
}
