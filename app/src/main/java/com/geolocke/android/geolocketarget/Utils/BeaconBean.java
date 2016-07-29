package com.geolocke.android.geolocketarget.Utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ACER on 7/29/2016.
 */
public final class BeaconBean implements Parcelable
{
    private final int rssi;
    private final String macAddress;
    private final String UUID;
    private final int major;
    private final int minor;
    //private final double distance;
    private final String name;

    public static final Creator<BeaconBean> CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };
    int d;

    private BeaconBean(Parcel var1) {

        this.macAddress = var1.readString();
        this.UUID = var1.readString();
        this.name = var1.readString();
        this.major = var1.readInt();
        this.minor = var1.readInt();
        this.rssi = var1.readInt();
        //this.distance = Double.parseDouble(null);
    }

    public BeaconBean(int rssi, String macAddress, String UUID, int major, int minor, String name) {
        this.rssi = rssi;
        this.macAddress = macAddress;
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.name = name;
        //this.distance = Double.parseDouble(null);
    }

    /*public BeaconBean(int rssi, String macAddress, String UUID, int major, int minor, String name, int distance) {
        this.rssi = rssi;
        this.macAddress = macAddress;
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.distance = distance;
    }*/


    public int describeContents() {
        return 0;
    }

    public boolean equals(Object var1) {
        if(this == var1) {
            return true;
        } else if(var1 != null && this.getClass() == var1.getClass()) {
            BeaconBean var2 = (BeaconBean)var1;
            return this.macAddress.equals(var2.macAddress);
        } else {
            return false;
        }
    }

   /* public double getDistance() {
        if(this.distance == Double.parseDouble(null)) {
            double var2 = this.c != null?this.c.doubleValue():(double)this.k;
            int var1 = this.j;
            double var4;
            this.b = Double.valueOf(var2 == 0.0D?-1.0D:(var1 == 0?-1.0D:((var4 = var2 * 1.0D / (double)var1) < 1.0D?Math.pow(var4, 10.0D):0.42093D * Math.pow(var4, 6.9476D) + 0.54992D)));
        }

        return (new BigDecimal(this.b.doubleValue())).setScale(2, 4).doubleValue();
    }*/

    public String getMacAddress() {

        return this.macAddress;
    }

    public int getMajor() {

        return this.major;
    }

    /*public int getMeasuredPower() {
        return this.j;
    }*/

    public int getMinor() {
        return this.minor;
    }

    public String getName() {
        return this.name;
    }

    /*public int getProximity() {
        if(this.a == null) {
            double var1;
            this.a = Integer.valueOf((var1 = this.getDistance()) < 0.0D?0:(var1 >= 0.0D && var1 < 0.5D?1:(var1 >= 0.5D && var1 < 3.0D?2:3)));
        }

        return this.a.intValue();
    }*/

    public String UUID() {
        return this.UUID;
    }

    public int getRssi() {
        return this.rssi;
    }

    /*public int getPower() {
        return this.d;
    }*/

    public int hashCode() {
        int var1 = this.UUID.hashCode();
        var1 = 31 * var1 + this.major;
        return 31 * var1 + this.minor;
    }

    public String toString() {
        return "Name: "+this.name+" macAddress: "+ this.macAddress + " UUDD: "+ this.UUID + " major: " + this.major + " minor: " + this.minor + " RSSI: "+ this.rssi;
        // return distance.a("macAddress", this.macAddress).a("proximityUUID", this.UUID).a("major", this.major).a("minor", this.minor).a("rssi", this.rssi).toString();
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeString(this.macAddress);
        var1.writeString(this.UUID);
        var1.writeString(this.name);
        var1.writeInt(this.major);
        var1.writeInt(this.minor);
        var1.writeInt(this.rssi);

    }
}
