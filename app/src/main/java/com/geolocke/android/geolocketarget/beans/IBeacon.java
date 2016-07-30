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

    public static final Creator<IBeacon> CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };


    private IBeacon(Parcel pParcel) {

        this.mMacAddress = pParcel.readString();
        this.mUUID = pParcel.readString();
        this.mName = pParcel.readString();
        this.mMajor = pParcel.readInt();
        this.mMinor = pParcel.readInt();
    }

    public IBeacon(String pMacAddress, String pUUID, int pMajor, int pMinor, String pName) {
        this.mMacAddress = pMacAddress;
        this.mUUID = pUUID;
        this.mMajor = pMajor;
        this.mMinor = pMinor;
        this.mName = pName;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object var1) {
        if(this == var1) {
            return true;
        } else if(var1 != null && this.getClass() == var1.getClass()) {
            IBeacon var2 = (IBeacon)var1;
            return this.mMacAddress.equals(var2.mMacAddress);
        } else {
            return false;
        }
    }

    public String getMacAddress() {

        return this.mMacAddress;
    }

    public int getMajor() {

        return this.mMajor;
    }


    public int getMinor() {
        return this.mMinor;
    }

    public String getName() {
        return this.mName;
    }

    public String getUUID() {
        return this.mUUID;
    }


    public int hashCode() {
        int var1 = this.mUUID.hashCode();
        var1 = 31 * var1 + this.mMajor;
        return 31 * var1 + this.mMinor;
    }


    @Override
    public String toString() {
        return "IBeacon{" +
                ", MacAddress='" + mMacAddress + '\'' +
                ", UUID='" + mUUID + '\'' +
                ", Major=" + mMajor +
                ", Minor=" + mMinor +
                ", Name='" + mName + '\'' +
                '}';
    }

    public void writeToParcel(Parcel pParcel, int pInt) {
        pParcel.writeString(this.mMacAddress);
        pParcel.writeString(this.mUUID);
        pParcel.writeString(this.mName);
        pParcel.writeInt(this.mMajor);
        pParcel.writeInt(this.mMinor);

    }
}
