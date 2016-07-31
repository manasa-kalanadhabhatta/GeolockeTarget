package com.geolocke.android.geolocketarget.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Manasa on 31-07-2016.
 */
public class BleBeaconScan implements Parcelable{
    private ArrayList<IBeacon> mIBeaconList;
    private ArrayList<Integer> mRssiList;

    protected BleBeaconScan(Parcel pParcel) {
       /* mIBeaconList = new ArrayList<IBeacon>();
        pParcel.readTypedList(mIBeaconList,IBeacon.CREATOR);
        mRssiList = (ArrayList<Integer>) pParcel.readSerializable();*/
        mIBeaconList = pParcel.readArrayList(IBeaconScan.class.getClassLoader());
        mRssiList = pParcel.readArrayList(int.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "IBeaconScan{" +
                "mIBeaconList=" + mIBeaconList +
                ", mRssiList=" + mRssiList +
                '}';
    }

    public ArrayList<IBeacon> getIBeaconList() {
        return mIBeaconList;
    }

    public ArrayList<Integer> getRssiList() {
        return mRssiList;
    }

    public BleBeaconScan(ArrayList<IBeacon> pIBeaconList, ArrayList<Integer> pRssiList) {

        mIBeaconList = pIBeaconList;
        mRssiList = pRssiList;
    }

    public static final Creator<BleBeaconScan> CREATOR = new Creator<BleBeaconScan>() {
        @Override
        public BleBeaconScan createFromParcel(Parcel pParcel) {
            return new BleBeaconScan(pParcel);
        }

        @Override
        public BleBeaconScan[] newArray(int pSize) {
            return new BleBeaconScan[pSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        //pParcel.writeTypedList(mIBeaconList);
        //pParcel.writeSerializable(mRssiList);
        pParcel.writeList(mIBeaconList);
        pParcel.writeList(mRssiList);
    }
}
