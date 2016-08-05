package com.geolocke.android.geolocketarget.beans;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.geolocke.android.geolocketarget.contentprovider.IBeaconsDbHelper;


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


    // Create a TvShow object from a cursor
    public static IBeacon fromCursor(Cursor curIBeacons) {
        String name = curIBeacons.getString(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_NAME));
        String UUID = curIBeacons.getString(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_UUID));
        String macAddress = curIBeacons.getString(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_MAC_ADDRESS));
        int major = curIBeacons.getInt(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_MAJOR));
        int minor = curIBeacons.getInt(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_MINOR));;
        byte txPower = (byte) curIBeacons.getInt(curIBeacons.getColumnIndex(IBeaconsDbHelper.IBEACONS_COL_TX_POWER));

        return new IBeacon(macAddress,UUID,major,minor,name,txPower);
    }

    /**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(IBeaconsDbHelper.IBEACONS_COL_NAME, this.getName());
        values.put(IBeaconsDbHelper.IBEACONS_COL_MAC_ADDRESS, this.getMacAddress());
        values.put(IBeaconsDbHelper.IBEACONS_COL_UUID, this.getUUID());
        values.put(IBeaconsDbHelper.IBEACONS_COL_MAJOR, this.getMajor());
        values.put(IBeaconsDbHelper.IBEACONS_COL_MINOR, this.getMinor());
        values.put(IBeaconsDbHelper.IBEACONS_COL_TX_POWER, this.getTxPower());
        return values;
    }
}
