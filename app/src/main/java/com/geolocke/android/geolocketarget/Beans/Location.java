package com.geolocke.android.geolocketarget.Beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ACER on 7/30/2016.
 */
public class Location implements Parcelable {
    private ArrayList<Position> mPositionsList;

    @Override
    public String toString() {
        return "Location{" +
                "mPositionsList=" + mPositionsList +
                '}';
    }

    public ArrayList<Position> getPositionsList() {
        return mPositionsList;
    }

    public Location(ArrayList<Position> pPositionsList) {
        mPositionsList = pPositionsList;
    }

    protected Location(Parcel pParcel) {
        mPositionsList = pParcel.readArrayList(Position.class.getClassLoader());
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
        pParcel.writeList(mPositionsList);
    }
}
