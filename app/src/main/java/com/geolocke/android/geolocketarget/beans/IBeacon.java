package com.geolocke.android.geolocketarget.beans;

import android.os.Parcel;
import android.os.Parcelable;


public final class IBeacon implements Parcelable
{
    private final String mMacAddress;
    private final String mUUID;
    private final int mMajor;
    private final int mMinor;
    private final String mName;
    private final byte mTxPower;


    protected IBeacon(Parcel in) {
        mMacAddress = in.readString();
        mUUID = in.readString();
        mMajor = in.readInt();
        mMinor = in.readInt();
        mName = in.readString();
        mTxPower = in.readByte();
    }

    @Override
    public String toString() {
        return "IBeacon{" +
                "mMacAddress='" + mMacAddress + '\'' +
                ", mUUID='" + mUUID + '\'' +
                ", mMajor=" + mMajor +
                ", mMinor=" + mMinor +
                ", mName='" + mName + '\'' +
                ", mTxPower=" + mTxPower +
                '}';
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getUUID() {
        return mUUID;
    }

    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public String getName() {
        return mName;
    }

    public byte getTxPower() {
        return mTxPower;
    }

    public IBeacon(String pMacAddress, String pUUID, int pMajor, int pMinor, String pName, byte pTxPower) {

        mMacAddress = pMacAddress;
        mUUID = pUUID;
        mMajor = pMajor;
        mMinor = pMinor;
        mName = pName;
        mTxPower = pTxPower;
    }

    public static final Creator<IBeacon> CREATOR = new Creator<IBeacon>() {
        @Override
        public IBeacon createFromParcel(Parcel in) {
            return new IBeacon(in);
        }

        @Override
        public IBeacon[] newArray(int size) {
            return new IBeacon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMacAddress);
        dest.writeString(mUUID);
        dest.writeInt(mMajor);
        dest.writeInt(mMinor);
        dest.writeString(mName);
        dest.writeByte(mTxPower);
    }
}
