package com.geolocke.android.geolocketarget;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeolockeCredentials{
    private final List<GeolockeFeature> mGeolockeFeatures;
    private final String mApplicationKey;
    private final String mDeveloperKey;

    private final Context mContext;
    private final String mMacAddress;

    private GeolockeCredentials(Builder builder) {
        this.mApplicationKey = builder.mApplicationKey;
        this.mDeveloperKey = builder.mDeveloperKey;
        this.mGeolockeFeatures = builder.mGeolockeFeatures;
        this.mContext = builder.mContext;
        this.mMacAddress = builder.mMacAddress;
    }

    public String description() {
        StringBuilder sb = new StringBuilder();
        if (mGeolockeFeatures != null) {
            sb.append("\nIt contains;");
            for (GeolockeFeature geolockeFeature: mGeolockeFeatures) {
                sb.append(String.format("\n%s", geolockeFeature.description()));
            }
        }

        return sb.toString();
    }

    public String getApplicationKey() {

        return this.mApplicationKey;
    }

    public String getDeveloperKey() {

        return this.mDeveloperKey;
    }

    public Context getContext(){
        return this.mContext;
    }




    public static class Builder {

        private Context mContext;
        private String mMacAddress;

        private String mApplicationKey;
        private String mDeveloperKey;

        private List<GeolockeFeature> mGeolockeFeatures;


        public Builder(Context pContext) {
            this.mContext = pContext;
            this.mMacAddress = this.setMacAddress(mContext);
        }

        private String setMacAddress(Context pContext){
            // check that interface name is wlan0
            // check that rights on /sys/class/net/wlan0/address are at least r--r--r--

            String macAddress = "";
            try {
                BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
                macAddress += br.readLine();
                Log.i("MAC", "mac addr: " + macAddress);
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return macAddress;
        }

        public Builder addGeolockeFeature(String pGeolockeFeature) {
            if (mGeolockeFeatures == null) {
                mGeolockeFeatures = new ArrayList<>();
            }
            mGeolockeFeatures.add(new GeolockeFeature(pGeolockeFeature));
            return this;
        }

        public Builder enable() {
            if (mGeolockeFeatures != null && mGeolockeFeatures.size() > 0) {
                GeolockeFeature geolockeFeature = mGeolockeFeatures.get(mGeolockeFeatures.size() - 1);
                geolockeFeature.setEnable(true);
            }
            return this;
        }

        public Builder setDeveloperKey(String pDeveloperKey) {
            this.mDeveloperKey = pDeveloperKey;
            return this;
        }
        public Builder setApplicationKey(String pApplicationKey) {
            this.mApplicationKey = pApplicationKey;
            return this;
        }

        public GeolockeCredentials build() {
            return new GeolockeCredentials(this);
        }
    }


    private static class GeolockeFeature {
        private final String mName;
        private boolean mEnable = false;

        public GeolockeFeature(String pName) {
            this.mName = pName;
        }

        public void setEnable(boolean pEnable) {
            this.mEnable = pEnable;
        }

        public String description() {
            return "The feature" + mName + " is  enabled? = " + mEnable;
        }
    }
}