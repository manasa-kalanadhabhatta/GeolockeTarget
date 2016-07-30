package com.geolocke.android.geolocketarget.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GeolockeIBeacon implements Parcelable {

    private final String mMacAddress;
    private final String mUUID;
    private final String mName;
    private final int mMajor;
    private final int mMinor;
    private final double mLatitude;
    private final double mLongitude;

    protected GeolockeIBeacon(Parcel pParcel) {
        mMacAddress = pParcel.readString();
        mUUID = pParcel.readString();
        mName = pParcel.readString();
        mMajor= pParcel.readInt();
        mMinor = pParcel.readInt();
        mLatitude = pParcel.readDouble();
        mLongitude = pParcel.readDouble();
    }

    public GeolockeIBeacon(String pMacAddress, String pUUID, String pName, int pRssi, int pMajor, int pMinor, double pLatitude, double pLongitude) {
        mMacAddress = pMacAddress;
        mUUID = pUUID;
        mName = pName;
        mMajor = pMajor;
        mMinor = pMinor;
        mLatitude = pLatitude;
        mLongitude = pLongitude;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getUUID() {
        return mUUID;
    }

    public String getName() {
        return mName;
    }



    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public static final Creator<GeolockeIBeacon> CREATOR = new Creator<GeolockeIBeacon>() {
        @Override
        public GeolockeIBeacon createFromParcel(Parcel pParcel) {
            return new GeolockeIBeacon(pParcel);
        }

        @Override
        public GeolockeIBeacon[] newArray(int pSize) {
            return new GeolockeIBeacon[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeString(mMacAddress);
        pParcel.writeString(mUUID);
        pParcel.writeString(mName);

        pParcel.writeInt(mMajor);
        pParcel.writeInt(mMinor);
        pParcel.writeDouble(mLatitude);
        pParcel.writeDouble(mLongitude);
    }



    @Override
    public String toString() {
        return "GeolockeIBeacon{" +
                "MacAddress='" + mMacAddress + '\'' +
                ", UUID='" + mUUID + '\'' +
                ", Name='" + mName + '\'' +
                ", Major=" + mMajor +
                ", Minor=" + mMinor +
                ", Latitude=" + mLatitude +
                ", Longitude=" + mLongitude +
                '}';
    }

    @Override
    public boolean equals(Object pObject) {
        if(this == pObject) {
            return true;
        } else if(pObject != null && this.getClass() == pObject.getClass()) {
            GeolockeIBeacon var2 = (GeolockeIBeacon) pObject;
            return this.mMacAddress.equals(var2.getMacAddress());
        } else {
            return false;
        }
    }
}
